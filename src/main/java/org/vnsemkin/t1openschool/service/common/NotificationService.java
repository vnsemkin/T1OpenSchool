package org.vnsemkin.t1openschool.service.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final JavaMailSender mailSender;
  private final MailProperties mailProperties;

  @Value("${notification.recipient:vnsemkin@gmail.com}")
  private String recipient;

  public void sendNotification(TaskStatusMessage message) {
    try {
      // Используем JavaMailSender для отправки HTML-сообщений
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom(mailProperties.getUsername());
      helper.setTo(recipient);
      helper.setSubject("Изменение статуса задачи #" + message.id());

      String content =
          String.format(
              "<h3>Уведомление о смене статуса задачи</h3>"
                  + "<p>Идентификатор задачи: <strong>%s</strong></p>"
                  + "<p>Новый статус: <strong>%s</strong></p>"
                  + "<p>Время изменения: %s</p>",
              message.id(), message.status(), java.time.LocalDateTime.now());

      helper.setText(content, true); // true indicates HTML content

      mailSender.send(mimeMessage);
      log.info("Email sent for task id: {} with new status: {}", message.id(), message.status());
    } catch (MessagingException e) {
      log.error("Failed to send email notification for task: {}", message.id(), e);
    }
  }

  // Простое уведомление
  public void sendSimpleNotification(TaskStatusMessage message) {
    SimpleMailMessage email = new SimpleMailMessage();
    email.setFrom(mailProperties.getUsername());
    email.setTo(recipient);
    email.setSubject("Изменение статуса задачи #" + message.id());
    email.setText(
        "Статус задачи с id "
            + message.id()
            + " изменился на: "
            + message.status()
            + "\nВремя изменения: "
            + java.time.LocalDateTime.now());

    try {
      mailSender.send(email);
      log.info(
          "Simple email sent for task id: {} with new status: {}", message.id(), message.status());
    } catch (Exception e) {
      log.error("Failed to send simple email notification for task: {}", message.id(), e);
    }
  }
}
