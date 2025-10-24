# Security Artifact Backend

A modular Java Spring Boot backend for a web application, providing authentication, authorization, and session management using JWT, OAuth2 (Google, GitHub), PostgreSQL, and Redis.

> **This README applies to the `main` branch of [rorouser/security-artifact](https://github.com/rorouser/security-artifact/tree/main).**

---

## 📚 Table of Contents

- [🚀 Installation](#-installation)
- [⚙️ Usage](#-usage)
- [🗂️ Project Structure](#-project-structure)
- [🤝 Contribution](#-contribution)
- [🧪 Testing](#-testing)
- [📝 License](#-license)
- [❓ FAQ / Common Issues](#-faq--common-issues)

---

## 🚀 Installation

1. **Clone the repository and switch to the `main` branch:**
   ```bash
   git clone https://github.com/rorouser/security-artifact.git
   cd security-artifact
   git checkout main
   ```

2. **Prerequisites:**
   - Java 17 or higher
   - Maven 3.8+
   - PostgreSQL
   - Redis

3. **Set up your environment variables:**
   - Copy `.env.example` to `.env` and fill in your database, JWT, and OAuth credentials.
   - Alternatively, update module-specific configuration files or `src/main/resources/application.yml` as needed for local development.

4. **Install dependencies:**
   ```bash
   mvn clean install
   ```

---

## ⚙️ Usage

- **Run the backend locally:**
  ```bash
  mvn spring-boot:run -pl main-app
  ```
- The main application will be available at `http://localhost:8080`.

- **Required environment variables:**  
  - PostgreSQL credentials  
  - Redis configuration  
  - JWT secret  
  - Google and/or GitHub OAuth2 Client ID and Secret

---

## 🗂️ Project Structure

The project is modularized as follows:
```
security-artifact/
├── auth-module/       # Authentication logic (JWT, OAuth2)
├── business-module/   # Business logic/services
├── common-module/     # Shared utilities and classes
├── data-module/       # Database access and repositories
├── main-app/          # Spring Boot application entry point
├── redis-module/      # Redis caching/session management
├── .gitignore
└── pom.xml            # Parent Maven configuration
```

- Each module contains its own code, resources, and potentially its own configuration files.
- The `main-app` module is the entry point for running the application.

---

## 🤝 Contribution

1. Fork the repository.
2. Create a branch for your feature:  
   `git checkout -b feature/my-feature`
3. Make your changes and commit:  
   `git commit -m 'Add new functionality'`
4. Push and open a Pull Request targeting the `main` branch.
5. Ensure your changes are covered by tests.

---

## 🧪 Testing

- Run tests with Maven:
  ```bash
  mvn test
  ```
- Integration tests require PostgreSQL and Redis running.

---

## 📝 License

This project is licensed under the MIT License.

---

## ❓ FAQ / Common Issues

- **Database connection error:**  
  Check your credentials in `.env` or module-specific configuration.

- **OAuth errors:**  
  Ensure your callback URL in Google/GitHub matches the one configured in the backend.

- **Redis issues:**  
  Make sure Redis is running and accessible.

- **JWT errors:**  
  The JWT secret must be defined and consistent.

- **More questions?**  
  Open an issue in the repository.

---
