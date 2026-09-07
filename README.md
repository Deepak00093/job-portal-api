# Job Portal API

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Multi--Stage-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Render-Live-46E3B7?style=flat-square&logo=render&logoColor=black)](https://job-portal-api-lg3r.onrender.com/swagger-ui/index.html)
[![Aiven MySQL](https://img.shields.io/badge/Aiven-MySQL_8-00758F?style=flat-square&logo=mysql&logoColor=white)](https://aiven.io/)

A production-deployed Spring Boot REST API for a job recruitment platform. Features role-based access control, relational entity modeling, and automated cloud deployments via multi-stage Docker builds.

- **Live Swagger Documentation:** [https://job-portal-api-lg3r.onrender.com/swagger-ui/index.html](https://job-portal-api-lg3r.onrender.com/swagger-ui/index.html)
- **Base Service URL:** `https://job-portal-api-lg3r.onrender.com`

---

## Architecture Overview

```text
    [ Client / Swagger UI / Web App ]
                   │
                   ▼ (HTTPS / JSON)
    ┌────────────────────────────────────────┐
    │          Render Web Service            │
    │  ┌──────────────────────────────────┐  │
    │  │  Spring Security (RBAC Guard)    │  │
    │  └────────────────┬─────────────────┘  │
    │                   │                    │
    │  ┌────────────────▼─────────────────┐  │
    │  │  REST Controllers                │  │
    │  │  (Job, User, Company, Review)    │  │
    │  └────────────────┬─────────────────┘  │
    │                   │                    │
    │  ┌────────────────▼─────────────────┐  │
    │  │  Service Layer (Business Logic)  │  │
    │  └────────────────┬─────────────────┘  │
    │                   │                    │
    │  ┌────────────────▼─────────────────┐  │
    │  │  Spring Data JPA / Hibernate     │  │
    │  │  (HikariCP Connection Pool)      │  │
    │  └────────────────┬─────────────────┘  │
    └───────────────────┼────────────────────┘
                        │
                        ▼ (TLS Encrypted)
    ┌────────────────────────────────────────┐
    │     Aiven Managed Cloud MySQL DB       │
    │  (Tables: users, jobs, companies, ...) │
    └────────────────────────────────────────┘