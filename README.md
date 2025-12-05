# Онлайн Чат

Веб-приложение чата в реальном времени на Spring Boot с поддержкой WebSocket, аутентификацией и базой данных H2, для курса по Spring Framework.

## Описание проекта

Веб-приложение реализует функционал чата с:
- Регистрацией и аутентификацией пользователей
- Обменом сообщениями в реальном времени через WebSocket
- Личными диалогами между пользователями
- Управлением комнатами чата
- Хранением истории сообщений в БД

## Технологии

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Security** - аутентификация и авторизация
- **Spring WebSocket** - коммуникация в реальном времени
- **Spring Data JPA** - работа с базой данных
- **H2 Database** - встроенная база данных
- **Thymeleaf** - шаблонизатор для UI
- **Lombok** - упрощение кода
- **Maven** - система сборки

## Быстрый старт

### 1. Клонирование репозитория

```bash
git clone https://github.com/NadiaYanchuk/spring-chat-prototype.git
cd spring-chat-prototype
```

### 2. Установка Maven (если не установлен)

```bash
sudo apt install maven -y
```

### 3. Сборка проекта

```bash
mvn clean compile
```

### 4. Запуск приложения

```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: **http://localhost:8080**

## Архитектура проекта

```
src/
├── main/
│   ├── java/com/example/chat/
│   │   ├── ChatApplication.java                   # Главный класс приложения
│   │   ├── component/
│   │   │   └── DatabaseInitializer.java           # Инициализация БД
│   │   ├── config/
│   │   │   ├── DataLoader.java                    # Загрузка тестовых данных
│   │   │   ├── SecurityConfig.java                # Конфигурация безопасности
│   │   │   └── WebSocketConfig.java               # Конфигурация WebSocket
│   │   ├── constants/
│   │   │   └── WebSocketDestinations.java         # Константы WebSocket топиков
│   │   ├── controller/
│   │   │   ├── AuthController.java                # Регистрация/вход
│   │   │   ├── ChatController.java                # Основной контроллер чата
│   │   │   ├── MessageEntController.java          # REST API для сообщений
│   │   │   ├── RoomsController.java               # Управление комнатами
│   │   │   └── UsersController.java               # Управление пользователями
│   │   ├── dto/
│   │   │   ├── DeleteMessageDTO.java              # DTO для удаления
│   │   │   ├── MessageDTO.java                    # DTO сообщения
│   │   │   ├── MessagesDataDTO.java               # DTO данных сообщения
│   │   │   ├── RoomDTO.java                       # DTO комнаты
│   │   │   ├── UpdateMessageDTO.java              # DTO для обновления
│   │   │   └── UserDTO.java                       # DTO пользователя
│   │   ├── entity/
│   │   │   ├── MessageEntity.java                 # Сущность сообщения
│   │   │   ├── RoomEntity.java                    # Сущность комнаты
│   │   │   └── UserEntity.java                    # Сущность пользователя
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java        # Обработчик исключений
│   │   ├── repository/
│   │   │   ├── MessageEntityRepository.java       # Репозиторий сообщений
│   │   │   ├── RoomEntityRepository.java          # Репозиторий комнат
│   │   │   └── UserEntityRepository.java          # Репозиторий пользователей
│   │   └── service/
│   │       ├── MessageEntityService.java          # Сервис сообщений
│   │       ├── RoomEntityService.java             # Сервис комнат
│   │       └── UserEntityService.java             # Сервис пользователей
│   └── resources/
│       ├── application.properties                 # Конфигурация приложения
│       ├── static/
│       │   ├── img/                               # Изображения
│       │   ├── js/                                # JavaScript файлы
│       │   │   ├── chat.js                        # Логика чата
│       │   │   └── custom.js                      # Дополнительная логика
│       │   └── styles/
│       │       └── style.css                      # Стили
│       └── templates/
│           ├── chat.html                          # Главная страница чата
│           ├── login.html                         # Страница входа
│           └── registration.html                  # Страница регистрации
└── test/
    └── java/com/example/chat/
        └── ChatApplicationTests.java              # Тесты приложения
```

## REST API Endpoints

### Аутентификация
- `GET /login` - Страница входа
- `POST /login` - Аутентификация
- `GET /registration` - Страница регистрации
- `POST /registration` - Создание аккаунта
- `GET /logout` - Выход

### Чат
- `GET /chat` - Главная страница чата
- `GET /getmessages?sender={id}&recipient={id}` - Получить историю сообщений
- `PUT /updatemessage?timestamp={ts}` - Обновить сообщение
- `DELETE /deletemessage` - Удалить сообщение

### Пользователи
- `GET /fetchallusers?searchTerm={term}` - Поиск пользователей
- `GET /fetchknownusers` - Получить известных пользователей
- `GET /getprincipal` - Получить текущего пользователя
- `GET /fetchuser?id={id}` - Получить пользователя по ID

### Комнаты
- `GET /fetchallrooms` - Получить все комнаты текущего пользователя
- `GET /writetofound?principalId={id1}&recipientId={id2}` - Создать/найти комнату

## База данных

### H2 Console
Доступ к консоли БД: **http://localhost:8080/h2-console**

**Настройки подключения:**
- JDBC URL: `jdbc:h2:mem:chatdb`
- User: `sa`
- Password: (пусто)

## Авторы

- **Nadejda Ianciuc** - [GitHub](https://github.com/NadiaYanchuk)
- **Nichita Rusanov** - [GitHub](https://github.com/n1kry)
- **David Boroznet** - [GitHub](https://github.com/DavidBoroznet)
- **Valentina Advahova** - [GitHub](https://github.com/advahovalentina)

