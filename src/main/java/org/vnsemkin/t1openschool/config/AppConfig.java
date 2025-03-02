package org.vnsemkin.t1openschool.config;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

  @Bean
  public MailProperties mailProperties() {
    return new MailProperties();
  }

  @Bean
  public JavaMailSender javaMailSender(MailProperties mailProperties) {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(mailProperties.getHost());
    mailSender.setPort(mailProperties.getPort());
    mailSender.setUsername(mailProperties.getUsername());
    mailSender.setPassword(mailProperties.getPassword());

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.debug", "true");
    props.put("mail.smtp.auth", "true");
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.starttls.enable", "true");

    return mailSender;
  }
}
