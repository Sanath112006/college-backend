# Digital Complaint Portal – Backend

Backend for the **Digital Complaint Portal for Colleges** built with Java Spring Boot and MySQL.

## Tech Stack

- **Java 17**, **Spring Boot 3.2**
- **Spring Web**, **Spring Data JPA**, **Spring Security**, **Validation**
- **MySQL**, **Lombok**, **Thymeleaf**, **DevTools**
- **JWT** (JJWT) for authentication

## Features

- **Authentication**: Register and login (JWT)
- **Complaints**: Submit (normal or anonymous), view, track status, delete
- **Categories**: Hostel, Infrastructure, Academic, Administration
- **Status**: PENDING, IN_PROGRESS, RESOLVED, REJECTED
- **Admin**: List all complaints, add responses, update status

## Setup

1. **MySQL**: Create a database (or let the app create it):
   ```sql
   CREATE DATABASE digital_complaint_portal;
   ```
2. **Configuration**: Edit `src/main/resources/application.properties`:
   - `spring.datasource.username` / `spring.datasource.password`
   - `app.jwt.secret` (min 256-bit for HS256)
3. **Run**:
   ```bash
   mvn spring-boot:run
   ```
   Server: `http://localhost:8080`

## API Overview

All responses use the format:
```json
{ "success": true, "message": "...", "data": { ... }, "timestamp": "..." }
```

### Auth (public)

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Register (body: name, email, password, role) |
| POST | `/api/auth/login` | Login (body: email, password) |

After login, send the token in the header: `Authorization: Bearer <token>`.

### Complaints (authenticated)

| Method | URL | Description | Role |
|--------|-----|-------------|------|
| POST | `/api/complaints` | Submit complaint | STUDENT |
| GET | `/api/complaints/my` | My complaints | USER |
| GET | `/api/complaints/{id}` | Get one (owner or admin) | USER |
| GET | `/api/complaints` | List all (paginated, optional category/status) | ADMIN |
| POST | `/api/complaints/{id}/responses` | Add admin response | ADMIN |
| PATCH | `/api/complaints/{id}/status` | Update status (body: status) | ADMIN |
| DELETE | `/api/complaints/{id}` | Delete (owner or admin) | USER |

### Enums

- **Role**: `STUDENT`, `ADMIN`
- **ComplaintCategory**: `HOSTEL`, `INFRASTRUCTURE`, `ACADEMIC`, `ADMINISTRATION`
- **ComplaintStatus**: `PENDING`, `IN_PROGRESS`, `RESOLVED`, `REJECTED`

## Project Structure

```
src/main/java/com/college/complaintportal/
├── config/          # Security, JWT
├── controller/      # REST controllers
├── dto/             # Request/response DTOs
├── entity/          # JPA entities and enums
├── exception/       # Custom exceptions and global handler
├── repository/     # Spring Data JPA repositories
├── security/        # JWT filter
└── service/         # Business logic
```
