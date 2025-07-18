# Employee Attendance API

This project is a Spring Boot RESTful API for managing employee attendance data. It includes persistence with MySQL, containerization with Docker, and Kubernetes deployment.

---

## Project Overview

- **Spring Boot** based backend API for attendance management
- **MySQL** as the relational database for storing employee and attendance data
- **JPA / Hibernate** for ORM and database interactions
- **Docker** support for containerizing the application
- **Kubernetes** manifests for deployment and service exposure
- **Port forwarding** configured for local testing

---

## Features Implemented

- CRUD operations on attendance records
- Employee details linked to attendance entries
- JPA entities with proper mappings and repositories
- REST controllers exposing `/api/attendance` endpoints
- Proper connection pooling with HikariCP
- Configuration for MySQL connection in Azure
- Dockerfile added for building container images
- Kubernetes deployment manifest (`attendance-deployment.yaml`) with:
  - Deployment for app pods
  - Service of type `LoadBalancer` with NodePort and port mapping
- Local testing via `kubectl port-forward` and curl commands
- Debugged and fixed runtime errors related to Java version compatibility and connection issues
- Application exposed on custom port (`9090` in container)
- Database connection verified and integrated successfully
- Logs and health verified on Kubernetes pods

---

## How to Run Locally

1. Clone the repo
2. Update `application.yml` with your MySQL Azure DB credentials
3. Build the project:
   ```bash
   mvn clean install
