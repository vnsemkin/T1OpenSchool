package org.vnsemkin.t1openschool.config;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    // Настраиваем десериализацию: используем наш кастомный MessageDeserializer для значения
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class);
    // Группа потребителей
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    // Читаем с начала, если нет сохранённых offset
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    // Отключаем авто-коммит
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    // Разрешаем десериализацию классов из указанных пакетов
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "org.vnsemkin.t1openschool.*");
    return props;
  }

  @Bean
  public ConsumerFactory<String, TaskStatusMessage> taskStatusConsumerFactory() {
    Map<String, Object> configs = consumerConfigs();
    return new DefaultKafkaConsumerFactory<>(
        configs, new StringDeserializer(), new JsonDeserializer<>(TaskStatusMessage.class));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TaskStatusMessage>
      kafkaListenerContainerFactoryForTaskStatus() {
    ConcurrentKafkaListenerContainerFactory<String, TaskStatusMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConcurrency(1);
    factory.setBatchListener(true);
    factory.setCommonErrorHandler(errorHandler());
    factory.setConsumerFactory(taskStatusConsumerFactory());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    return factory;
  }

  @Bean
  public CommonErrorHandler errorHandler() {
    DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
    defaultErrorHandler.addNotRetryableExceptions(IllegalStateException.class);
    defaultErrorHandler.setRetryListeners(
        (record, ex, deliveryAttempt) -> log.error(
            "Retry listener: record = {}, ex = {}, deliveryAttempt = {}",
            record,
            ex,
            deliveryAttempt));
    return defaultErrorHandler;
  }
}