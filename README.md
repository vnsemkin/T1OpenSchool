# Проект T1 Open School — Сервис управления задачами 🚀

[![Java 21][Java-badge]][Java-url] [![Spring Boot][Spring-badge]][Spring-url] [![Gradle][Gradle-badge]][Gradle-url] [![License: MIT][License-badge]][License-url]

[Java-badge]: https://img.shields.io/badge/Java-21-orange.svg

[Java-url]: https://www.oracle.com/java/technologies/javase-jdk21-archive-downloads.html

[Spring-badge]: https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg

[Spring-url]: https://spring.io/projects/spring-boot

[Gradle-badge]: https://img.shields.io/badge/Gradle-8.12.1-blue.svg

[Gradle-url]: https://gradle.org/install/

[License-badge]: https://img.shields.io/badge/License-MIT-yellow.svg

[License-url]: https://opensource.org/licenses/MIT

***

Этот проект разработан в рамках обучения на курсе **T1 Open School** по направлению *Java-разработка*. Проект
представляет собой RESTful сервис для управления задачами, разработанный с использованием **Spring Boot**, **Java 21** и
**Gradle**.

## Описание проекта 📖

В проекте реализованы следующие возможности:

- **CRUD-операции для задач**: создание, получение, обновление и удаление задач.  
  Каждая задача имеет следующие поля: `id`, `title`, `description`, `userId`.
- **Логирование через аспекты**: используется Spring AOP для логирования работы методов сервисного слоя (Before,
  AfterReturning, AfterThrowing, Around) с дополнительной информацией (например, id).
- **Обработка ошибок**: глобальный обработчик ошибок через `@RestControllerAdvice` для валидации и обработки исключений.
- **Пагинация**: получение списка задач реализовано с использованием параметров `page` и `limit`.
- **Liquibase**: управление схемой базы данных с помощью changelog в формате YAML.
- **Docker Compose**: база данных PostgreSQL поднимается через Docker Compose.

## Технологии 🛠️

- **Java 21** – современный язык программирования  
  [Скачать Java 21](https://www.oracle.com/java/technologies/javase-jdk21-archive-downloads.html)
- **Spring Boot 3.4.2** – фреймворк для создания Spring-приложений  
  [Подробнее о Spring Boot](https://spring.io/projects/spring-boot)
- **Gradle** – система сборки  
  [Установка Gradle](https://gradle.org/install/)
- **Liquibase** – управление схемой БД (YAML changelog)
- **Lombok** – генерация геттеров/сеттеров, конструкторов и др.
- **PostgreSQL** – база данных, поднимаемая через Docker Compose
- **Spring AOP** – аспектно-ориентированное программирование для логирования

## Структура проекта 📂

**Описание структуры проекта:**

- `src/main/java`: Содержит основной Java-код приложения.
    - `org/vnsemkin/t1openschool`: Корневой пакет проекта.
        - `controller`: Директория для классов-контроллеров, обрабатывающих входящие HTTP-запросы.
        - `service`: Сервисный слой, где располагается бизнес-логика приложения и осуществляется взаимодействие между
          контроллерами и репозиториями.
        - `repository`: Репозитории для доступа к данным с использованием Spring Data JPA.
        - `entity`: JPA-сущности, отражающие структуру таблиц базы данных.
        - `exception`: Глобальный обработчик ошибок (`GlobalExceptionHandler`) и исключения, такие как
          `TaskNotFoundException`.
        - `mapper`: Классы для преобразования между DTO и сущностями (например, `TaskMapper`).
        - `dto`: Data Transfer Object (например, `TaskDTO`).
        - `aspect`: Аспекты для логирования (например, `LoggingAspect`).
        - `T1OpenSchoolApplication.java`: Главный класс приложения.
- `src/main/resources`: Ресурсы приложения, включая конфигурационные файлы.
    - `application.yml`: Основной файл конфигурации приложения (настройки БД, Liquibase, логирование).
    - `db/changelog/db.changelog.yml`: Liquibase changelog для управления схемой БД.
    - `openapi.yaml`: OpenAPI спецификация для REST‑эндпойнтов.
- `src/test/java`: Исходный код для автоматизированных тестов (Unit и Integration тесты).

## Запуск проекта 🚀

### 1. Необходимое программное обеспечение

- **Java 21**  
  [Скачать Java 21](https://www.oracle.com/java/technologies/javase-jdk21-archive-downloads.html)
- **Gradle**  
  [Установка Gradle](https://gradle.org/install/) или используйте Gradle Wrapper (`./gradlew`).
- **Docker** – убедитесь, что Docker установлен и запущен.

### 2. Запуск базы данных через Docker Compose

Перейдите в директорию `docker` и запустите PostgreSQL:

```bash
cd docker
docker-compose up -d
```

### 3. Установка переменных окружения

Убедитесь, что переменные окружения POSTGRES_USER и POSTGRES_PASSWORD заданы (например, в файле .env).

### 4. Клонирование репозитория

   ```bash
   git clone https://github.com/vnsemkin/T1OpenSchool.git
   ```

### 5. Переход в директорию проекта:

   ```bash
   cd T1OpenSchool
   ```

### 6. Сборка проекта с помощью Gradle:

   ```bash
   ./gradlew build
   ```

Эта команда загрузит все необходимые зависимости и соберет проект.

### 7. Запуск приложения:

   ```bash
   ./gradlew bootRun
   ```

Приложение будет запущено на встроенном сервере Tomcat.

### 8. Доступ к приложению:

Приложение будет доступно по адресу [http://localhost:8080](http://localhost:8080) в вашем веб-браузере.

## Проверка эндпойнтов

Создание задачи:
POST http://localhost:8080/api/v1/tasks
Request Body (TaskDTO):

Json
{"title": "Новая задача",
"description": "Описание задачи",
"userId": 123}
Ответ: созданный объект TaskDTO, статус 201 Created.

Получение задачи по ID:
GET http://localhost:8080/api/v1/tasks/{id}
Ответ: объект TaskDTO, статус 200 OK.

Обновление задачи:
PUT http://localhost:8080/api/v1/tasks/{id}
Request Body (TaskDTO):

Json
{
"id": 1,
"title": "Обновленная задача",
"description": "Новое описание",
"userId": 123
}
Ответ: обновленный объект TaskDTO, статус 200 OK.

Удаление задачи:
DELETE http://localhost:8080/api/v1/tasks/{id}
Ответ: статус 204 No Content.

Получение списка задач с пагинацией:
GET http://localhost:8080/api/v1/tasks?page=0&limit=10
Ответ: массив объектов TaskDTO, статус 200 OK.

OpenAPI спецификация
Файл openapi.yaml расположен в /spec/openapi.yaml и описывает все доступные REST‑эндпойнты, их параметры и
структуры данных. Вы можете загрузить его в Swagger Editor или Redoc для визуализации.

## Лицензия 📜

[![License: MIT][License-badge]][License-url]

[License-badge]: https://img.shields.io/badge/License-MIT-yellow.svg

[License-url]: https://opensource.org/licenses/MIT

***

Проект распространяется под лицензией **[MIT License](https://opensource.org/licenses/MIT)**.

**MIT License** — это одна из самых популярных и разрешительных лицензий с открытым исходным кодом. Она позволяет
практически неограниченное использование, изменение и распространение программного обеспечения как в коммерческих, так и
в некоммерческих целях.

**Основные условия лицензии MIT вкратце:**

* **Разрешено:**
    * Коммерческое использование
    * Модификация
    * Распространение
    * Приватное использование

* **Запрещено:**
    * Нет явных запретов, кроме пункта об отказе от ответственности (см. ниже).

* **Условия:**
    * **Указание авторства (attribution):** Необходимо включать текст лицензии и уведомление об авторских правах при
      распространении.
    * **Отказ от ответственности (warranty disclaimer):**  Программное обеспечение предоставляется "как есть" (as is)
      без каких-либо гарантий.

**Подробности лицензии:**

Полный текст лицензии MIT вы можете найти в файле  [`LICENSE`](https://opensource.org/licenses/MIT) в корне репозитория,
а также на [официальном сайте Open Source Initiative](https://opensource.org/licenses/MIT).

**Почему MIT License?**

Выбор MIT License для этого проекта обусловлен ее простотой и гибкостью. Она идеально подходит для образовательных
проектов и проектов с открытым исходным кодом, позволяя другим разработчикам свободно использовать и развивать ваш код.

## Контакты 📧

Если у вас возникли вопросы, предложения или замечания по проекту, свяжитесь со мной:

**Автор**: Semkin Vladimir

**Email**: [vnsemkin@gmail.com](mailto:vnsemkin@gmail.com)

**GitHub**: [https://github.com/vnsemkin](https://github.com/vnsemkin)

---