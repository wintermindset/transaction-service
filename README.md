# Transaction Service

A REST API microservice for processing financial transactions, accounts, and users built with **Spring Boot**, **Spring Security**, **JWT**, **PostgreSQL**, and **JPA/Hibernate**.


## Features

- User registration and authentication with **JWT**.
- Account management (CRUD).
- Money transactions between accounts.
- Role-based security.
- Profiles for different environments: `dev`, `prod`.
- **PostgreSQL** integration.
- Unit and integration testing with **JUnit 5** and **Spring Boot Test**.


## Technologies

- **Java 25**.
- **Maven**.
- **Spring Boot 4.0.1**.
- **Spring Security**.
- **Spring Data JPA / Hibernate**.
- **PostgreSQL**.
- **JWT (JJWT 0.12.6)**.


## Getting Started


### Prerequisites

- **Java 25**.
- **PostgreSQL**.


### Setup

1. Clone the repository:

``` bash
git clone https://github.com/wintermindset/transaction-service.git
cd transaction-service
```

2. Create a **PostgreSQL** database:

``` sql
CREATE DATABASE bank_db;
```

3. Configure your local profile in `src/main/resources/application-dev.yaml`:

``` YAML
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bank_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
```

4. Run the application:

``` bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```


## Profiles

- `dev`: Local development with `application-dev.yaml`.
- `prod`: Production profile (`application-prod.yaml`) — keep it out of git and use environment variables for secrets.


## JWT Authentication

- Users authenticate via `/api/auth/login`.
- JWT token returned must be included in `Authorization: Bearer <token>` header for protected endpoints.
- Roles are supported (e.g., USER, ADMIN).


## Running Tests

``` bash
mvn test
```

Includes unit and integration tests for services, repositories, and controllers.


## License

This project is licensed under the MIT License. See [`LICENSE`](./LICENSE) for details.
