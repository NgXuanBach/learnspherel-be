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
learnspherel-be/
├── src/
│ ├── main/
│ │ ├── java/
│ │ └── resources/
├── pom.xml
└── README.md

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

