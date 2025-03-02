package org.vnsemkin.t1openschool.service.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;
import org.vnsemkin.t1openschool.service.common.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusListener {

  private final NotificationService notificationService;

  @KafkaListener(
          topics = "${spring.kafka.topic}",
          containerFactory = "kafkaListenerContainerFactoryForTaskStatus")
  public void listen(List<TaskStatusMessage> messages, Acknowledgment ack) {
    messages.stream()
            .peek(message -> log.info("Processing Kafka message: {}", message))
            .forEach(message -> {
              try {
                notificationService.sendNotification(message);
              } catch (Exception e) {
                log.error("Error processing message {}: {}", message, e.getMessage());
                throw new RuntimeException("Failed to process message " + message, e);
              }
            });
    ack.acknowledge();
    log.info("Successfully processed and acknowledged batch of {} messages", messages.size());
  }
}