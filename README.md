# Inclusive Trip Planner – Backend

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Build Status](https://github.com/DavidCC812/inclusive-trip-planner-backend/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.3.5-success.svg)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-informational.svg)

Frontend repository: [inclusive-trip-planner-frontend](https://github.com/DavidCC812/inclusive-trip-planner-frontend)

## Overview

This repository contains the backend API for the Inclusive Trip Planner — a mobile-first travel assistant designed to help users with disabilities discover and plan accessible itineraries. The backend is implemented in Java using Spring Boot and provides secured RESTful endpoints, JWT-based authentication, PostgreSQL persistence, CI/CD, and full deployment via Render.

Originally created as a thesis project under the internal name “moonshot-project”, this codebase was refactored and opened to the public as part of a Master 2 admission portfolio.

## Table of Contents

- [Key Features](#key-features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Setup Instructions](#setup-instructions)
- [Usage](#usage)
- [Testing](#testing)
- [CI/CD Pipeline](#cicd-pipeline)
- [Deployment on Render](#deployment-on-render)
- [Project History](#project-history)
- [Contact](#contact)

## Key Features

- Modular REST API built with Spring Boot
- JWT-based authentication and Firebase token verification
- Liquibase-managed PostgreSQL database schema
- Secure controller-service-repository architecture
- Docker + Docker Compose for local and CI environments
- GitHub Actions for testing and deployment
- Deployed backend container hosted on Render
- Integration tests covering full entity lifecycle

## Architecture

The backend follows a layered package-by-feature structure, organized as follows:

```
auth/              - JWT + Firebase-based authentication and token filters  
config/            - Spring Security rules, CORS, and application config  
controller/        - REST endpoints for each entity (User, Itinerary, etc.)  
dto/               - Request and response models used by the API  
model/             - JPA entities representing database tables  
repository/        - Spring Data interfaces for persistence  
service/           - Business logic and orchestration per domain  
liquibase/         - XML-based schema migrations (versioned and tracked)  
docker/            - Docker and Docker Compose files for local setup and CI  
test/.../          - Integration tests (using H2) for each feature  
                   - Includes a shared cleanup component for data isolation
```

This structure ensures a clear separation between API exposure, business rules, and persistence. Docker is used to run the full stack locally and in CI/CD pipelines. The app is deployed to Render as a containerized service.

## Tech Stack

- Language: Java 21
- Framework: Spring Boot 3.3.5
- Database: PostgreSQL
- CI/CD: GitHub Actions
- Deployment: Docker + Render
- Authentication: JWT (with Firebase token validation)
- Migration: Liquibase
- Testing: Spring Boot Test, MockMvc, H2

## Setup Instructions

1. Clone the Repository

   ```bash
   git clone <https://github.com/yourusername/inclusive-trip-planner-backend.git>  
   cd inclusive-trip-planner-backend
   ```

2. Configure Environment

No manual environment setup is required for local development.

The backend loads its configuration from `application.yml`, and Docker Compose provides default database credentials for local use.

In production (e.g. on Render), the following environment variables must be defined in the Render dashboard:

- `JWT_SECRET`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_PROFILES_ACTIVE`

3. Run Locally with Docker

   docker compose -f docker/docker-compose.yaml up --build

   The app will start on `http://localhost:8080` and connect to the linked PostgreSQL container.

4. Health Check Endpoint

   <http://localhost:8080/health>

## Usage

The backend exposes secured endpoints for:

- User authentication (`/api/auth`)
- Destination browsing and filtering
- Saving itineraries (`/api/saved-itineraries`)
- User accessibility and country preferences
- Writing and retrieving reviews

All authenticated routes require a valid JWT in the `Authorization` header. A Firebase ID token can be exchanged for a JWT at `/api/auth/firebase`.

## Mobile App Integration

This backend is consumed by the official Android frontend:

[inclusive-trip-planner-android](https://github.com/yourusername/inclusive-trip-planner-android)

It supports Firebase authentication and exposes secured REST endpoints used by the mobile app via Retrofit. The mobile app exchanges Firebase tokens for backend JWTs to authorize all user actions.

## Testing

To run integration tests:

    ```bash
   ./mvnw test

   ```

Tests are defined with `@SpringBootTest` and cover:

- CRUD operations for all entities
- Secure access to protected endpoints
- User-authenticated flows
- Data cleanup using a centralized `TestDataCleaner`

Test isolation is ensured using H2 in-memory database and a `test` Spring profile.

## CI/CD Pipeline

Every push or pull request to `develop` or `main` triggers the GitHub Actions pipeline defined in `.github/workflows/ci.yml`. The CI workflow:

- Builds the application using Maven
- Builds and runs containers via Docker Compose
- Waits for the backend to become healthy (`/health`)
- Executes all integration tests
- Uploads test reports as CI artifacts
- On `main`, tags and pushes a Docker image to Docker Hub

## Deployment on Render

The backend is automatically deployed to [Render](https://inclusive-trip-backend.onrender.com/) using the Docker image built by GitHub Actions.

Deployment is triggered when code is merged into the `main` branch. The container is hosted on a Render web service that exposes the backend over HTTPS with automatic health checks and secret management.

This approach provides a reliable production-like environment and a public URL that can be used directly by the mobile app during development and demonstration.

**Note:** Since the project is hosted on Render’s free tier, the backend may enter a sleep state after inactivity. When clicking the link, you may see a “Service Waking Up…” screen for a few seconds while it initializes.

Render also supports environment variable injection, service logs, and build status monitoring, making it a suitable platform for solo development and rapid iteration.

To verify availability, you can check the health endpoint:  
[`https://inclusive-trip-backend.onrender.com/health`](https://inclusive-trip-backend.onrender.com/health)

## Project History

- Initial Thesis Phase: March – June 2025 (as "moonshot-project")
- Repository Refactoring and Public Migration: July 2025
- Deployment and CI/CD Finalization: July 28, 2025

## Contact

**David Cuahonte Cuevas**  
[GitHub](https://github.com/DavidCC812)  
[LinkedIn](https://www.linkedin.com/in/david-cuahonte-527781221/)
>>>>>>> Stashed changes
