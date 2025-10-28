# 🏫 Collaborative Classroom

A distributed **Spring Boot** project that powers a modern collaborative classroom platform.  
It integrates real-time communication, secure authentication, microservices, and cloud storage using:

**Spring Boot · Kafka · JWT · OAuth2 · gRPC · Netflix Eureka · AWS S3 · Java Mail**

---

## 🚀 Overview

Collaborative Classroom is a microservice-based platform that enables real-time interaction between teachers and students.  
It supports seamless class management, notifications, file sharing, and secure authentication — built on top of a scalable architecture.

---

## 🧩 Tech Stack

| Category | Technologies           |
|-----------|------------------------|
| **Backend Framework** | Spring Boot (Java 17+) |
| **Service Communication** | gRPC, REST             |
| **Message Broker** | Apache Kafka           |
| **Service Discovery** | Netflix Eureka         |
| **Authentication & Security** | JWT, OAuth2            |
| **Cloud Integration** | AWS S3 (file storage)  |
| **Email Service** | Java Mail Sender       |
| **Build Tool** | Maven                  |
| **Database** | (PostgreSQL)           |

---

## 🏗️ Architecture

```text
 ┌────────────────────────────┐
 │        API Gateway         │
 └────────────┬───────────────┘
              │
 ┌────────────▼───────────────┐
 │     Authentication Service │  ← OAuth2 + JWT
 └────────────┬───────────────┘
              │
 ┌────────────▼───────────────┐
 │     Classroom Service      │  ← gRPC + Eureka
 └────────────┬───────────────┘
              │
 ┌────────────▼───────────────┐
 │   Notification Service     │  ← Kafka
 └────────────┬───────────────┘
              │
 ┌────────────▼───────────────┐
 │        S3 Service          │  ← AWS S3 Uploads
 └────────────────────────────┘
