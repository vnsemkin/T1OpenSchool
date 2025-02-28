package org.vnsemkin.t1openschool.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;
import org.vnsemkin.t1openschool.service.common.NotificationService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusListener {

  private final NotificationService notificationService;

  @KafkaListener(
          topics = "${spring.kafka.topic}",
          containerFactory = "kafkaListenerContainerFactoryForTaskStatus")
  public void listen(List<TaskStatusMessage> messages, Acknowledgment ack) {
    try {
      // Обрабатываем сообщения и создаем список исключений
      Optional<Exception> processingException = messages.stream()
              .map(message -> {
                try {
                  log.info("Processing Kafka message: {}", message);
                  notificationService.sendNotification(message);
                  return Optional.<Exception>empty();
                } catch (Exception e) {
                  log.error("Error processing message {}: {}", message, e.getMessage());
                  return Optional.of(e);
                }
              })
              .filter(Optional::isPresent)
              .map(Optional::get)
              .findFirst();

      // Бросаем исключение, если хоть одно сообщение не удалось обработать
      if (processingException.isPresent()) {
        throw new RuntimeException("Failed to process at least one message in the batch",
                processingException.get());
      }

      // Подтверждаем обработку сообщений только если все прошли успешно
      ack.acknowledge();
      log.info("Successfully processed and acknowledged batch of {} messages", messages.size());
    } catch (Exception e) {
      log.error("Batch processing failed, not acknowledging: {}", e.getMessage());
    }
  }
}