# GuardRail Scanner Service (Java Spring Boot)

A security vulnerability management microservice and vulnerability playground built with **Spring Boot 3**, **Spring Data JPA**, and **OpenAPI/Swagger**.

---

## 🚀 Features

1. **Scan Management & Reporting**:
   - Ingests JSON reports from SAST scanners (Brakeman, Trivy, SonarQube).
   - Automatically classifies findings by severity (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`, `INFO`), tracks CWE IDs, code snippets, and fingerprinting.
   - RESTful APIs with OpenAPI/Swagger UI at `/swagger-ui.html`.

2. **Java Vulnerability Playground (`/api/v1/lab`)**:
   - Includes intentional security vulnerabilities in Java for testing scanners (SonarQube, SpotBugs, Semgrep, CodeQL, Snyk).

---

## 📦 API Endpoints

### 🛡️ Core Scan APIs
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/scans` | Submit and parse a security scan JSON report |
| `GET` | `/api/v1/scans` | Retrieve all historical scans |
| `GET` | `/api/v1/scans/{id}` | Get full scan details and associated findings |
| `GET` | `/api/v1/scans/project/{projectName}` | Get scan history for a specific project |
| `GET` | `/api/v1/scans/{id}/vulnerabilities` | List findings for a scan |
| `GET` | `/api/v1/vulnerabilities/{id}` | Get specific vulnerability details |

### ⚠️ Vulnerability Playground APIs (`/api/v1/lab`)
| Vulnerability | Method | Endpoint | Description |
|---|---|---|---|
| **SQL Injection (SQLi)** | `GET` | `/api/v1/lab/sqli?projectName=...` | Concatenation in native SQL query |
| **Command Injection (RCE)** | `POST` | `/api/v1/lab/rce?host=...` | `Runtime.getRuntime().exec()` with user input |
| **Path Traversal (LFI)** | `GET` | `/api/v1/lab/lfi?filename=...` | Arbitrary file reading via `File` / `Files.readString` |
| **Insecure Deserialization** | `POST` | `/api/v1/lab/deserialize` | Unsafe `ObjectInputStream.readObject()` on Base64 payload |
| **Reflected XSS** | `GET` | `/api/v1/lab/xss?name=...` | Unescaped HTML rendering |
| **SSRF** | `GET` | `/api/v1/lab/ssrf?targetUrl=...` | Arbitrary URL fetch via `HttpURLConnection` |
| **XXE Injection** | `POST` | `/api/v1/lab/xxe` | XML parsing with external entities enabled |
| **Hardcoded Secrets** | `GET` | `/api/v1/lab/secrets` | Hardcoded API keys / AWS credentials |

---

## 🛠️ How to Run

### Using Maven:
```bash
mvn clean spring-boot:run
```

### Using Docker:
```bash
docker build -t guardrail-scanner-service .
docker run -p 8080:8080 guardrail-scanner-service
```

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console` (`jdbc:h2:mem:guardrail_db`)
