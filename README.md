# Student Management System (Spring Boot)

A simple CRUD web application for managing student records.
Spring Boot 3 + Spring Data JPA + Thymeleaf, storing data in MySQL.
Features: add, view, update, delete and search student records, with server-side validation.

## Requirements
- Java 17+
- Maven 3.9+ (or open the folder in IntelliJ IDEA / VS Code and run the app from there)
- MySQL 8 running locally

## Database setup
Start MySQL, then either use the defaults (user `root`, password `root`) or set your own:
```bash
# Option A: MySQL in Docker
docker run --name sms-mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8

# Option B: use your own MySQL, and tell the app the password
export DB_USERNAME=root
export DB_PASSWORD=your_password        # Windows PowerShell: $env:DB_PASSWORD="your_password"
```
The `student_db` database and `students` table are created automatically on first run.

## Run
```bash
mvn spring-boot:run        # open http://localhost:8080
```

## Test
```bash
mvn test                   # uses in-memory H2, no MySQL needed
```

## Structure
| Package / file | Purpose |
|---|---|
| `model/Student` | JPA entity plus validation rules |
| `repository/StudentRepository` | Database access (Spring Data JPA, parameterised queries) |
| `service/StudentService` | Business logic and transactions |
| `controller/StudentController` | Web routes (MVC) |
| `resources/templates`, `static/css` | Responsive Thymeleaf UI |
| `src/test` | MockMvc tests for every feature |
