# Онлайн Чат

Учебный прототип веб-приложения для обмена сообщениями в реальном времени на Spring Boot с реализацией системы аутентификации, авторизации и управления данными.

<img width="1412" height="799" alt="8bc8053e-5886-4a02-ace8-acd340142b95" src="https://github.com/user-attachments/assets/af6e5e91-989c-460b-b753-9307d1dc903a" />

## Описание проекта

Проект, демонстрирующий построение современной многослойной архитектуры (MVC + Service + Repository) на базе Spring Framework.

- **Этап разработки №1** (сентябрь-декабрь 2025): Базовая структура, WebSocket, фронтенд на Thymeleaf
- **Этап разработки №2** (январь-май 2026): Полная система безопасности, CRUD, логирование, управление ролями

### Реализованный функционал:

**Аутентификация и Безопасность:**
- Регистрация пользователей с валидацией данных
- JWT (JSON Web Token) аутентификация
- BCrypt хэширование паролей
- Spring Security интеграция
- Управление сессиями
- Role-Based Access Control (RBAC)

**Функциональность чата:**
- Личные диалоги между пользователями
- Обмен сообщениями в реальном времени (WebSocket)
- Управление комнатами чата (создание, удаление)
- История сообщений в базе данных
- Редактирование и удаление сообщений

**Управление данными:**
- CRUD операции для пользователей, комнат, сообщений
- Spring Data JPA с кастомными запросами
- Оптимизированная работа с БД
- Транзакционность операций

**Качество кода:**
- Многослойная архитектура (Controller → Service → Repository)
- DTO паттерн для передачи данных
- SLF4J логирование во всех слоях
- Глобальная обработка ошибок (GlobalExceptionHandler)
- Валидация входных данных (Jakarta Validation)
- Использование Lombok для сокращения boilerplate кода

**Тестирование:**
- Unit тесты на базе JUnit 5
- Подготовка для Mockito интеграционных тестов
- Test fixtures и test data конфигурация

## Технологии

- **Java 21** - современный LTS версия языка
- **Spring Boot 3.5.6** - основной фреймворк
- **Spring Security 6.3.6** - аутентификация и авторизация
- **Spring WebSocket** - коммуникация в реальном времени
- **Spring Data JPA** - работа с базой данных
- **H2 Database** - встроенная база данных (in-memory)
- **Thymeleaf** - шаблонизатор для UI
- **Lombok** - сокращение boilerplate кода
- **JWT (JSON Web Token)** - токены для безопасности
- **BCrypt** - хэширование паролей
- **SLF4J** - логирование
- **JUnit 5 & Mockito** - тестирование
- **Maven** - система сборки

## Быстрый старт

### Требования
- Java 21 или выше
- Maven 3.9 или выше

### 1. Клонирование репозитория

```bash
git clone https://github.com/NadiaYanchuk/spring-chat-prototype.git
cd spring-chat-prototype
```

### 2. Сборка проекта

```bash
mvn clean compile
```

### 3. Запуск приложения

```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: **http://localhost:8080**

### 4. Доступ к БД (H2 Console)

Консоль для управления БД доступна по: **http://localhost:8080/h2-console**

Параметры подключения:
- JDBC URL: `jdbc:h2:mem:chatdb`
- User: `sa`
- Password: (оставить пусто)

## Основной пользовательский flow

```
1. РЕГИСТРАЦИЯ
   └─ Пользователь переходит на /registration
   └─ Вводит username, пароль, email
   └─ Пароль хэшируется (BCrypt)
   └─ Данные сохраняются в БД

2. АВТОРИЗАЦИЯ
   └─ Пользователь переходит на /login
   └─ Вводит username и пароль
   └─ Система проверяет учетные данные
   └─ Генерируется JWT токен (24 часа)
   └─ Токен сохраняется в HttpOnly cookie
   └─ Перенаправление на /chat

3. ДЕЙСТВИЯ В ЧАТЕ
   └─ Просмотр списка всех пользователей
   └─ Просмотр своих контактов
   └─ Создание нового диалога
   └─ Отправка сообщений в реальном времени
   └─ Редактирование и удаление сообщений

4. ВЫХОД ИЗ СИСТЕМЫ
   └─ JWT cookie удаляется
   └─ Пользователь перенаправляется на /login
```

## Архитектура проекта

Проект использует многослойную архитектуру:

```
┌─────────────────────────────────────────┐
│     PRESENTATION LAYER (UI)             │
│  Thymeleaf Templates, HTML, CSS, JS     │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│  WEB LAYER (Controllers)                │
│  • AuthController                       │
│  • UserController                       │
│  • RoomController                       │
│  • MessageController                    │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│  SERVICE LAYER (Business Logic)         │
│  • UserEntityService                    │
│  • RoomEntityService                    │
│  • MessageEntityService                 │
│  • JwtService                           │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│  REPOSITORY LAYER (Data Access)         │
│  Spring Data JPA Repositories           │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│  DATABASE LAYER (H2)                    │
│  users, rooms, messages tables          │
└─────────────────────────────────────────┘

CROSS-CUTTING CONCERNS:
├── Security (Spring Security + JWT)
├── Exception Handling
├── Logging (SLF4J)
├── WebSocket Configuration
└── Transaction Management
```

## API Endpoints

### Аутентификация
- `GET /registration` - Форма регистрации
- `POST /registration` - Регистрация пользователя
- `GET /login` - Форма входа
- `POST /login` - Логин пользователя
- `GET /logout` - Выход из системы

### Пользователи
- `GET /fetchallusers?searchTerm={term}` - Найти пользователей
- `GET /fetchknownusers` - Получить контакты
- `GET /getprincipal` - Информация о текущем пользователе
- `GET /fetchuser?id={id}` - Получить пользователя по ID

### Комнаты чата
- `GET /fetchallrooms` - Получить все комнаты текущего пользователя
- `GET /writetofound?principalId={id1}&recipientId={id2}` - Создать новую комнату

### Сообщения (WebSocket)
- `WebSocket /chat/{recipient}` - Отправить сообщение
- `PUT /updatemessage?timestamp={ts}` - Редактировать сообщение
- `DELETE /deletemessage?messageId={id}` - Удалить сообщение

## Модель базы данных

Проект использует три основные таблицы:

### users (Пользователи)
```
id              BIGINT PRIMARY KEY
username        VARCHAR(50) UNIQUE NOT NULL
password        VARCHAR(255) NOT NULL
email           VARCHAR(255) UNIQUE NOT NULL
join_time       TIMESTAMP NOT NULL
last_activity   TIMESTAMP
```

### rooms (Комнаты чата)
```
id              BIGINT PRIMARY KEY
creator_id      BIGINT FOREIGN KEY → users.id
user1_id        BIGINT FOREIGN KEY → users.id
user2_id        BIGINT FOREIGN KEY → users.id
created_at      TIMESTAMP NOT NULL
is_active       BOOLEAN DEFAULT TRUE
```

### messages (Сообщения)
```
id              BIGINT PRIMARY KEY
room_id         BIGINT FOREIGN KEY → rooms.id
user_id         BIGINT FOREIGN KEY → users.id
text            TEXT NOT NULL
timestamp       TIMESTAMP NOT NULL
```

## Структура проекта

```
src/main/java/com/example/chat/
├── controller/         # REST Controllers
├── service/            # Business Logic Layer
├── repository/         # Data Access Layer
├── entity/             # Domain Models (JPA Entities)
├── dto/                # Data Transfer Objects
├── security/           # Security Components (JWT)
├── config/             # Configuration Classes
├── exception/          # Exception Handling
├── component/          # Application Components
└── constants/          # Application Constants
```

## Безопасность

Приложение включает следующие меры безопасности:

- **Spring Security**: Интеграция с фреймворком для управления доступом
- **JWT токены**: Безопасная аутентификация с использованием JSON Web Tokens
- **BCrypt хэширование**: Пароли хранятся в зашифрованном виде (ни в коем случае не в открытом!)
- **CSRF защита**: Защита от подделки межсайтовых запросов
- **HttpOnly cookies**: JWT сохраняются в защищенных cookies
- **Валидация входных данных**: Все входные данные валидируются перед обработкой
- **SQL Injection защита**: Использование JPA для защиты от SQL инъекций
- **Role-Based Access Control**: Управление доступом на основе ролей пользователей

## Тестирование

Проект включает:

- **Unit тесты**: JUnit 5 тесты для изоляции компонентов
- **Mockito**: Framework для создания mock объектов
- **Integration тесты**: Тесты взаимодействия компонентов
- **Test fixtures**: Подготовленные тестовые данные

Запуск тестов:

```bash
mvn test
```

## Примеры использования

### Регистрация нового пользователя

```
GET http://localhost:8080/registration
```

Форма заполняется данными:
- Username: john_doe
- Password: secure_password
- Email: john@example.com

```
POST http://localhost:8080/registration
Body (form-data):
  username: john_doe
  password: secure_password
  email: john@example.com
```

### Логин

```
POST http://localhost:8080/login
Body (form-data):
  username: john_doe
  password: secure_password
```

После успешного входа JWT токен сохраняется в cookie и доступны защищенные endpoints.

### Отправка сообщения (WebSocket)

```javascript
// JavaScript пример
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/chat'
});

stompClient.onConnect = function(frame) {
    // Подписываемся на входящие сообщения
    stompClient.subscribe('/topic/messages/2', function(message) {
        console.log('Received:', message.body);
    });
    
    // Отправляем сообщение
    stompClient.send('/app/chat/2', {}, JSON.stringify({
        senderId: 1,
        recipientId: 2,
        text: 'Hello!',
        timestamp: Date.now()
    }));
};

stompClient.activate();
```

## Развитие проекта

Планируемые улучшения:

- [ ] Расширение системы ролей (ADMIN, MODERATOR)
- [ ] Двухфакторная аутентификация (2FA)
- [ ] Групповые чаты
- [ ] Загрузка файлов и медиа
- [ ] Emoji и rich text сообщения
- [ ] Уведомления о доставке сообщений
- [ ] Оффлайн режим
- [ ] Кэширование
- [ ] Docker контейнеризация
- [ ] Kubernetes развертывание

## Ресурсы и ссылки

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [Maven Documentation](https://maven.apache.org/)
- [H2 Database](http://h2database.com/)

## Авторы

- **Nadejda Ianciuc** - [GitHub](https://github.com/NadiaYanchuk)
- **Nichita Rusanov** - [GitHub](https://github.com/n1kry)
- **David Boroznet** - [GitHub](https://github.com/DavidBoroznet)
- **Valentina Advahova** - [GitHub](https://github.com/advahovalentina)
