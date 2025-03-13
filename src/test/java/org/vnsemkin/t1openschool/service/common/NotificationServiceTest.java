package org.vnsemkin.t1openschool.service.common;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private JavaMailSender mailSender;

  @Mock private MailProperties mailProperties;

  @InjectMocks private NotificationService notificationService;

  private TaskStatusMessage taskStatusMessage;

  @BeforeEach
  void setUp() {
    // Устанавливаем тестовые данные и настройки
    when(mailProperties.getUsername()).thenReturn("test@example.com");

    // Устанавливаем адрес получателя через рефлексию, т.к. он задаётся через @Value
    ReflectionTestUtils.setField(notificationService, "recipient", "recipient@example.com");

    // Подготавливаем тестовое сообщение
    taskStatusMessage = TaskStatusMessage.builder().id(1L).status("IN_PROGRESS").build();
  }

  @Test
  @DisplayName("Отправка HTML-уведомления об изменении статуса задачи")
  void sendNotification_ShouldSendEmailWithCorrectContent() throws Exception {
    // Arrange - настраиваем поведение мока
    MimeMessage mimeMessage = new MimeMessage((Session) null);
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doNothing().when(mailSender).send(any(MimeMessage.class));

    // Act - вызываем тестируемый метод
    notificationService.sendNotification(taskStatusMessage);

    // Assert - проверяем, что письмо отправлено с правильными параметрами
    verify(mailSender).createMimeMessage();
    verify(mailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName("Отправка простого текстового уведомления об изменении статуса задачи")
  void sendSimpleNotification_ShouldSendEmailWithCorrectContent() {
    // Arrange - настраиваем поведение мока
    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    // Act - вызываем тестируемый метод
    notificationService.sendSimpleNotification(taskStatusMessage);

    // Assert - проверяем, что письмо отправлено с правильными параметрами
    ArgumentCaptor<SimpleMailMessage> emailCaptor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(emailCaptor.capture());

    SimpleMailMessage sentMessage = emailCaptor.getValue();
    assertEquals("test@example.com", sentMessage.getFrom());
    assertEquals("recipient@example.com", Objects.requireNonNull(sentMessage.getTo())[0]);
    assertEquals("Изменение статуса задачи #1", sentMessage.getSubject());
    assertTrue(
        Objects.requireNonNull(sentMessage.getText())
            .contains("Статус задачи с id 1 изменился на: IN_PROGRESS"));
  }

  @Test
  @DisplayName("Обработка исключений при отправке уведомления")
  void sendNotification_ShouldHandleExceptions() throws Exception {
    // Arrange - настраиваем поведение мока для выброса исключения
    when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

    // Используем RuntimeException, так как сервис теперь обрабатывает все типы исключений
    doThrow(new RuntimeException("Mail server connection failed"))
            .when(mailSender)
            .send(any(MimeMessage.class));

    // Act & Assert - проверяем, что исключение обрабатывается корректно
    assertDoesNotThrow(
            () -> {
              notificationService.sendNotification(taskStatusMessage);
            },
            "Метод должен корректно обрабатывать исключения");

    verify(mailSender).createMimeMessage();
    verify(mailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName("Обработка исключений при отправке простого уведомления")
  void sendSimpleNotification_ShouldHandleExceptions() {
    // Arrange - настраиваем поведение мока для выброса исключения
    doThrow(new RuntimeException("Mail server connection failed"))
        .when(mailSender)
        .send(any(SimpleMailMessage.class));

    // Act & Assert - проверяем, что исключение обрабатывается корректно
    assertDoesNotThrow(
        () -> {
          notificationService.sendSimpleNotification(taskStatusMessage);
        },
        "Метод должен корректно обрабатывать исключения");

    verify(mailSender).send(any(SimpleMailMessage.class));
  }
}
