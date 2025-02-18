# Проект Spring Boot для курса T1 Open School 🚀

[![Java 21][Java-badge]][Java-url]  [![Spring Boot][Spring-badge]][Spring-url]  [![Gradle][Gradle-badge]][Gradle-url] [![License: MIT][License-badge]][License-url]

[Java-badge]: https://img.shields.io/badge/Java-21-orange.svg

[Java-url]: https://www.oracle.com/java/technologies/javase-jdk21-archive-downloads.html

[Spring-badge]: https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg

[Spring-url]: https://spring.io/projects/spring-boot

[Gradle-badge]: https://img.shields.io/badge/Gradle-8.12.1-blue.svg

[Gradle-url]: https://gradle.org/install/

[License-badge]: https://img.shields.io/badge/License-MIT-yellow.svg

[License-url]: https://opensource.org/licenses/MIT

***

Этот проект разработан в рамках обучения на курсе T1 Open School по направлению "Java-разработка". Он содержит примеры
кода и домашние задания, выполненные с использованием **Spring Boot**, **Java 21** и **Gradle**.

## Описание проекта 📖

Данный проект представляет собой набор Spring Boot приложений, демонстрирующих различные аспекты разработки на Java с
применением Spring Framework. В ходе разработки были рассмотрены следующие ключевые темы:

- **🌐 RESTful API**: Создание современных и масштабируемых веб-сервисов.
- **🗃️ Работа с базами данных**: Использование Spring Data JPA для эффективного управления данными.
- **⚙️ Конфигурация Spring Boot**:  Настройка приложения с применением возможностей Spring Boot.
- **🧪 Тестирование**: Разработка Unit и Integration тестов для обеспечения надежности кода.
- **🔒 Spring Security**:  Реализация механизмов безопасности и аутентификации.
- **🛠️ Сборка проекта Gradle**:  Применение Gradle для автоматизации сборки и управления зависимостями.

## Технологии 🛠️

Проект построен на базе следующих технологий:

- **Java 21**: Основной язык программирования, обеспечивающий современную функциональность и
  производительность.  ([Скачать Java 21](https://www.oracle.com/java/technologies/javase-jdk21-archive-downloads.html))
- **Spring Boot**:  Ведущий фреймворк для разработки Spring-приложений, упрощающий настройку
  и запуск. ([Подробнее о Spring Boot](https://spring.io/projects/spring-boot))
- **Gradle**:  Мощная система сборки, автоматизирующая процессы компиляции, тестирования и
  развертывания. ([Установка Gradle](https://gradle.org/install/))
- **Spring Boot Starters**:  Использование Spring Boot Starters (Spring Web, Spring Data JPA, Spring Security и др.) для
  упрощения подключения необходимых зависимостей.

## Структура проекта 📂

**Описание структуры проекта:**

- `src/main/java`: Содержит основной Java-код приложения.
    - `org/vnsemkin/t1openschool`:  Корневой пакет проекта.
        - `controller`:  Директория для классов-контроллеров, обрабатывающих входящие HTTP-запросы и отвечающих за
          взаимодействие с пользователем.
        - `service`:  Сервисный слой, где располагается бизнес-логика приложения и осуществляется взаимодействие между
          контроллерами и репозиториями.
        - `repository`:  Репозитории, отвечающие за доступ к данным и взаимодействие с базой данных. Используют Spring
          Data JPA для упрощения работы с БД.
        - `model`:  Модели данных (сущности), представляющие структуры таблиц базы данных и используемые для обмена
          данными между слоями приложения.
        - `config`:  Конфигурационные классы Spring Boot, содержащие настройки приложения, бины и другие параметры.
        - `T1OpenSchoolApplication.java`:  Главный класс приложения.
- `src/main/resources`:  Ресурсы приложения, такие как файлы конфигурации, статические ресурсы и шаблоны.
    - `application.yml`:  Основной файл конфигурации приложения, где задаются настройки
      базы данных, порта сервера, языковые параметры и другие свойства.
- `src/test/java`: Содержит исходный код для автоматизированных тестов.
    - `controller`, `service`, `integration`:  Пакеты для хранения Unit-тестов (тестов отдельных компонентов) и
      Integration-тестов (тестов взаимодействия нескольких компонентов) для контроллеров, сервисов и интеграционных
      сценариев.

## Запуск проекта 🚀

Для запуска проекта необходимо выполнить следующие шаги:

1. **Установка необходимого программного обеспечения:**
    - **Java 21**:  Убедитесь, что на вашем компьютере установлена Java 21. Скачать можно
      с [официального сайта Oracle](https://www.oracle.com/java/technologies/javase-jdk21-archive-downloads.html).
    - **Gradle**:  Необходимо установить Gradle для сборки проекта. Инструкции по установке доступны
      на [официальном сайте Gradle](https://gradle.org/install/).

2. **Клонирование репозитория:**

   ```bash
   git clone https://github.com/vnsemkin/T1OpenSchool.git
   ```

3. **Переход в директорию проекта:**

   ```bash
   cd T1OpenSchool
   ```

4. **Сборка проекта с помощью Gradle:**

   ```bash
   ./gradlew build
   ```
   Эта команда загрузит все необходимые зависимости и соберет проект.

5. **Запуск приложения:**

   ```bash
   ./gradlew bootRun
   ```
   Приложение будет запущено на встроенном сервере Tomcat.

6. **Доступ к приложению:**

   Приложение будет доступно по адресу [http://localhost:8080](http://localhost:8080) в вашем веб-браузере.

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