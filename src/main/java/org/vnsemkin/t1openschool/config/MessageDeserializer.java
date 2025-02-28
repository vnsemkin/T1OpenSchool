package org.vnsemkin.t1openschool.config;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

/**
 * Кастомный десериализатор, перехватывающий ошибки при десериализации.
 */
@Slf4j
@Component
public class MessageDeserializer<T> extends JsonDeserializer<T> {

  /**
   * Преобразовать сырые байты в строку (UTF-8).
   */
  private static String getMessage(byte[] data) {
    return new String(data, StandardCharsets.UTF_8);
  }

  /**
   * Десериализация с учётом заголовков (Headers).
   */
  @Override
  public T deserialize(String topic, Headers headers, byte[] data) {
    try {
      return super.deserialize(topic, headers, data);
    } catch (Exception e) {
      log.warn("Ошибка во время десериализации (headers): {}", getMessage(data), e);
      return null; // или выбросить исключение, если хотите "падать" при ошибке
    }
  }

  /**
   * Десериализация без заголовков.
   */
  @Override
  public T deserialize(String topic, byte[] data) {
    try {
      return super.deserialize(topic, data);
    } catch (Exception e) {
      log.warn("Ошибка во время десериализации: {}", getMessage(data), e);
      return null;
    }
  }
}