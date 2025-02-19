package org.vnsemkin.t1openschool.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  // Точка входа: все методы сервисного слоя
  @Pointcut("within(org.vnsemkin.t1openschool.service..*)")
  public void serviceMethods() {}

  // Before advice: логируем начало выполнения метода с дополнительными данными (например, id)
  @Before("serviceMethods()")
  public void logServiceBefore(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().toShortString();
    String idInfo = extractIdInfo(joinPoint.getArgs());
    log.info("Начало выполнения сервиса: {}{}", methodName, idInfo);
  }

  // AfterReturning advice: логируем успешное завершение метода
  @AfterReturning(pointcut = "serviceMethods()", returning = "result")
  public void logServiceAfterReturning(JoinPoint joinPoint, Object result) {
    String methodName = joinPoint.getSignature().toShortString();
    log.info("Сервис {} успешно завершил работу. Результат: {}", methodName, result);
  }

  // AfterThrowing advice: логируем исключения, если метод завершился с ошибкой
  @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
  public void logServiceAfterThrowing(JoinPoint joinPoint, Throwable ex) {
    String methodName = joinPoint.getSignature().toShortString();
    String idInfo = extractIdInfo(joinPoint.getArgs());
    log.error("Сервис {}{} завершился с ошибкой: {}", methodName, idInfo, ex.getMessage());
  }

  // Around advice: замер времени выполнения метода
  @Around("serviceMethods()")
  public Object logServiceAround(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    String idInfo = extractIdInfo(joinPoint.getArgs());
    long start = System.currentTimeMillis();
    try {
      Object result = joinPoint.proceed();
      long elapsedTime = System.currentTimeMillis() - start;
      log.info("Сервис {}{} выполнен за {} ms", methodName, idInfo, elapsedTime);
      return result;
    } catch (Throwable ex) {
      long elapsedTime = System.currentTimeMillis() - start;
      log.error(
          "Сервис {}{} завершился с ошибкой через {} ms: {}",
          methodName,
          idInfo,
          elapsedTime,
          ex.getMessage());
      throw ex;
    }
  }

  // Вспомогательный метод для извлечения "id" из аргументов (если есть аргумент типа Long)
  private String extractIdInfo(Object[] args) {
    for (Object arg : args) {
      if (arg instanceof Long) {
        return " [id=" + arg + "]";
      }
    }
    return "";
  }
}
