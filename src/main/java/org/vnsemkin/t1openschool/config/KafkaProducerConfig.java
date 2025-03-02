package org.vnsemkin.t1openschool.config;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;

@Slf4j
@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public NewTopic taskStatusTopic(@Value("${spring.kafka.topic}") String topicName) {
    return new NewTopic(topicName, 3, (short) 1);
  }

  @Bean
  public Map<String, Object> producerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    // Сериализация ключа и значения
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    // Настройки идемпотентности и ретраев
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    // Пакетная отправка
    props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    return props;
  }

  @Bean
  public ProducerFactory<String, TaskStatusMessage> taskStatusProducerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public KafkaTemplate<String, TaskStatusMessage> taskStatusKafkaTemplate() {
    return new KafkaTemplate<>(taskStatusProducerFactory());
  }
}
