# Collaborative Classroom

Collaborative Classroom is a distributed platform built with **Spring Boot** that connects teachers and students in real time.  
It uses a microservice architecture to handle communication, authentication, file sharing, and notifications, with a focus on scalability and security.

**Built with:** Spring Boot · Kafka · JWT · OAuth2 · gRPC · Netflix Eureka · AWS S3 · Java Mail

---

## Overview

Collaborative Classroom is designed to create a modern, interactive online learning environment.  
It enables real-time collaboration, class management, and content sharing between users.  
Each service is modular and communicates through **gRPC** and **Kafka** to ensure performance and reliability as the system scales.

---

## Tech Stack

| Category | Technologies |
|-----------|--------------|
| Backend Framework | Spring Boot (Java 17+) |
| Service Communication | gRPC, REST |
| Message Broker | Apache Kafka |
| Service Discovery | Netflix Eureka |
| Authentication & Security | JWT, OAuth2 |
| Cloud Integration | AWS S3 |
| Email Service | Java Mail Sender |
| Build Tool | Maven |
| Database | PostgreSQL |
