# Онлайн Чат - Прототип

Прототип системы онлайн чата на Spring Boot с поддержкой WebSocket для курса по Spring Framework.

## Описание проекта

Это веб-приложение реализует базовую функциональность чата в реальном времени с использованием технологий Spring Boot, WebSocket...

## Требования

- **Java 17** или выше
- **Maven 3.6+** (используется Maven Wrapper)

## Быстрый старт

### 1. Клонирование репозитория

```bash
git clone <repository-url>
cd demo
```

### 2. Сборка проекта

```bash
./mvnw clean compile
```

### 3. Запуск приложения

```bash
./mvnw spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`


## Архитектура проекта

```
src/
├── main/
│   ├── java/com/example/chat/
(допишем)
```

## Основные компоненты

### WebSocket Configuration
- **Endpoint**: `/ws` - точка подключения клиентов
- **Message Broker**: Simple broker для топика `/topic`
- **Application Prefix**: `/app` для маршрутизации сообщений

### Message Model
```java
public class Message {
    private String from;  // Отправитель сообщения
    private String text;  // Текст сообщения
}
```

### Запуск тестов

```bash
./mvnw test
```

## Полезные команды Maven

```bash
# Очистка проекта
./mvnw clean

# Компиляция
./mvnw compile

# Запуск тестов
./mvnw test

# Сборка JAR файла
./mvnw package

# Запуск приложения в dev режиме
./mvnw spring-boot:run

# Генерация отчета о зависимостях
./mvnw dependency:tree
```

## Образовательные цели

Проект демонстрирует:
- Настройку Spring Boot приложения
- Работу с WebSocket в Spring
- Структуру типичного Spring проекта
- Основы тестирования Spring приложений