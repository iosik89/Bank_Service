# 💳 Bank Card Service

## 📘 Описание

**Bank Card Service** — это REST API приложение для управления банковскими картами.  
Функционал включает:
- регистрацию и авторизацию пользователей (JWT);
- создание и управление картами (для администраторов);
- просмотр, поиск и операции с картами (для пользователей);
- проверки прав доступа через Spring Security и `@PreAuthorize`.

---

## 🛠️ Технологии

- **Java 17**
- **Spring Boot 3**
- **Spring Security (JWT)**
- **Spring Data JPA / Hibernate**
- **PostgreSQL**
- **Liquibase**
- **Docker & Docker Compose**
- **Swagger / OpenAPI 3**

---

## ⚙️ Подготовка окружения

Перед запуском убедись, что установлены:
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Git](https://git-scm.com/)
- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/) (если запускаешь локально без Docker)

---

## 🚀 Запуск проекта (через Docker Compose)

1. **Клонируй репозиторий:**
   ```bash
   git clone https://github.com/iosik89/Bank_Service.git
   cd Bank_Service
  
2. **Собери приложение:**
mvn clean package -DskipTests


3. **Запусти в Docker:**
docker-compose up --build

4. **После запуска сервис будет доступен по адресу:**
👉 http://localhost:8080

PostgreSQL запущен на порту 5432



