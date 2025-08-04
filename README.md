# LearnSphereL - Backend

This is the backend system for **LearnSphereL**, a personalized e-learning platform that supports role-based access for Admins, Instructors, and Learners. It is built with Java 17 and Spring Boot, providing secure RESTful APIs, scalable architecture, and efficient data handling.

## 🌟 Key Features

- JWT-based authentication and authorization  
- Role management: Admin, Instructor, User  
- Course management (create, update, delete)  
- Quiz creation and evaluation  
- User progress tracking and personalized recommendations  
- Feedback management between users and instructors  
- Exception handling and logging

## 🛠️ Tech Stack

- Java 17  
- Spring Boot, Spring Security, Spring Data JPA  
- MySQL  
- JWT for authentication  
- Docker (for containerization)  
- RESTful APIs  
- Lombok, Logger

## 📦 Project Structure

```
learnspherel-be/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java 17
- Maven
- MySQL
- Docker (optional for deployment)

### Run locally

```bash
git clone https://github.com/yourusername/learnspherel-be.git
cd learnspherel-be
./mvnw spring-boot:run
```

### Environment variables
Create a `.env` or configure `application.properties` for:
- Database credentials
- JWT secret
- Port, context-path, etc.

## 🧪 API Overview

| Method | Endpoint             | Description                          |
|--------|----------------------|--------------------------------------|
| POST   | /api/auth/login      | User login                           |
| POST   | /api/users           | Create new user                      |
| GET    | /api/courses         | Get all courses                      |
| POST   | /api/courses         | Create a course (Instructor only)    |
| GET    | /api/users/me        | Get current user info                |
| ...    |                      | (More endpoints in progress)         |

## 📄 License

This project is for educational and portfolio purposes.
