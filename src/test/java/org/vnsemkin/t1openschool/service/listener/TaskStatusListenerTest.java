package org.vnsemkin.t1openschool.service.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;
import org.vnsemkin.t1openschool.service.common.NotificationService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskStatusListenerTest {

  @Mock
  private NotificationService notificationService;

  @Mock
  private Acknowledgment acknowledgment;

  @InjectMocks
  private TaskStatusListener taskStatusListener;

  private List<TaskStatusMessage> messages;

  @BeforeEach
  void setUp() {
    // Подготавливаем тестовые сообщения
    TaskStatusMessage message1 = TaskStatusMessage.builder()
            .id(1L)
            .status("IN_PROGRESS")
            .build();

    TaskStatusMessage message2 = TaskStatusMessage.builder()
            .id(2L)
            .status("COMPLETED")
            .build();

    messages = Arrays.asList(message1, message2);
  }

  @Test
  @DisplayName("Слушатель должен обрабатывать сообщения и отправлять уведомления")
  void listen_ShouldProcessMessagesAndSendNotifications() {
    // Arrange - настраиваем поведение мока
    doNothing().when(notificationService).sendNotification(any(TaskStatusMessage.class));
    doNothing().when(acknowledgment).acknowledge();

    // Act - вызываем тестируемый метод
    taskStatusListener.listen(messages, acknowledgment);

    // Assert - проверяем, что были отправлены уведомления для каждого сообщения
    verify(notificationService, times(2)).sendNotification(any(TaskStatusMessage.class));
    verify(notificationService).sendNotification(messages.get(0));
    verify(notificationService).sendNotification(messages.get(1));

    // Проверяем, что было отправлено подтверждение обработки сообщений
    verify(acknowledgment).acknowledge();
  }

  @Test
  @DisplayName("Слушатель должен обрабатывать пустой список сообщений")
  void listen_ShouldHandleEmptyMessageList() {
    // Arrange - создаем пустой список сообщений
    List<TaskStatusMessage> emptyMessages = List.of();

    // Act - вызываем тестируемый метод
    taskStatusListener.listen(emptyMessages, acknowledgment);

    // Assert - проверяем, что не было отправлено уведомлений
    verify(notificationService, never()).sendNotification(any(TaskStatusMessage.class));

    // Проверяем, что было отправлено подтверждение обработки сообщений
    verify(acknowledgment).acknowledge();
  }

  @Test
  @DisplayName("Слушатель должен выбрасывать исключение при ошибке обработки сообщения")
  void listen_ShouldThrowException_WhenProcessingFails() {
    // Arrange - настраиваем поведение мока для выброса исключения
    doThrow(new RuntimeException("Failed to send notification"))
            .when(notificationService).sendNotification(messages.get(0));

    // Act & Assert - проверяем, что выбрасывается исключение
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      taskStatusListener.listen(messages, acknowledgment);
    });

    // Проверяем содержимое сообщения об ошибке
    assertTrue(exception.getMessage().contains("Failed to process message"));

    // Проверяем, что была попытка отправить уведомление
    verify(notificationService).sendNotification(messages.get(0));

    // Проверяем, что не было подтверждения обработки сообщений
    verify(acknowledgment, never()).acknowledge();
  }
}
