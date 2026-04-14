# Online Course Reservation System

Spring Boot application for managing courses and reservations with MySQL persistence and a simple browser UI.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web, Spring Security, Spring Data JPA, Thymeleaf
- MySQL

## MySQL Setup

1. Create a MySQL user/password you want to use (or use `root/root`).
2. Update database credentials in [src/main/resources/application.properties](src/main/resources/application.properties).
3. Run the app. It automatically initializes schema and seed data from:
	- [src/main/resources/schema.sql](src/main/resources/schema.sql)
	- [src/main/resources/data.sql](src/main/resources/data.sql)

The app reads DB settings from environment variables (with defaults):

```properties
DB_URL=jdbc:mysql://localhost:3306/course_reservation_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=
```

PowerShell example:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_actual_mysql_password"
mvn spring-boot:run
```

Default DB URL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/course_reservation_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

## Run

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## UI

Open `http://localhost:8080/`

You can:
- Create a course
- Create a reservation for a student/course
- Cancel an existing reservation
- View course seat availability and reservation status

## API

- `GET /api` - API entry metadata
- `GET /api/courses`
- `GET /api/courses/{id}`
- `GET /api/courses/available`
- `GET /api/courses/search?keyword=...`
- `POST /api/courses`
- `PUT /api/courses/{id}`
- `DELETE /api/courses/{id}`
- `GET /api/enrollments`
- `GET /api/enrollments/student/{studentId}`
- `POST /api/enrollments`
- `POST /api/enrollments/{enrollmentId}/drop`
- `GET /api/enrollments/insights`

## Test

```bash
./mvnw test
```