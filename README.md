# Personal Finance Tracker - Backend API

A production-ready RESTful API for personal finance and expense management built with **Spring Boot**, **Spring Security**, and **PostgreSQL**. Features robust JWT authentication, Google OAuth 2.0 ID token verification, and consolidated financial metrics calculation.

---

## Features

- **Authentication & Security**:
  - Stateless JWT (JSON Web Token) authentication with HMAC-SHA256 signature verification.
  - **Google OAuth 2.0 Sign-In**: Directly validates Google ID tokens with Google's Token Verification API.
  - Safe DTO architecture ensuring hashed passwords and internal user credentials never leak in API responses.
  - Generic authentication error handling preventing email enumeration attacks.
  - Cross-Origin Resource Sharing (CORS) configured with credential support and preflight `OPTIONS` handling.
- **Transaction & Wealth Management**:
  - Full CRUD operations for income and expense transactions.
  - User-isolated transactions ensuring strict tenant isolation.
  - **Single Round-trip Summary**: High-performance `/api/transactions/summary` endpoint providing total income, total expenses, net balance, and record counts in a single payload.

---

## Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Java 21+ |
| **Framework** | Spring Boot (Spring MVC, Spring Data JPA) |
| **Security** | Spring Security & JJWT (JSON Web Token) |
| **Database** | PostgreSQL |
| **Build Tool** | Apache Maven |
| **Deployment** | Docker & Render support |

---

## Project Structure

```
src/main/java/com/example/myproject/
├── config/
│   ├── JwtFilter.java         # Intercepts requests to validate Bearer tokens
│   ├── JwtUtil.java           # Generates and decodes JWT signatures
│   └── SecurityConfig.java    # Spring Security filter chain & CORS configuration
├── controller/
│   ├── AuthController.java    # /auth/register, /auth/login, /auth/google, /auth/me
│   └── TransactionController.java # /api/transactions CRUD & summary metrics
├── dto/
│   ├── AuthResponse.java      # Safe token + user payload
│   ├── GoogleAuthRequest.java # Google ID token container
│   ├── LoginRequest.java      # Login payload
│   ├── RegisterRequest.java   # Registration payload
│   ├── SummaryResponse.java   # Aggregated financial metrics
│   └── UserDTO.java           # Safe user profile DTO without password
├── entity/
│   ├── Transaction.java       # Financial record JPA entity
│   └── User.java              # User account JPA entity
├── exception/
│   └── GlobalExceptionHandler.java # Standardized API error responses
├── repository/
│   ├── TransactionRepository.java  # Isolated queries by user
│   └── UserRepository.java         # Email lookups & account provisioning
└── service/
    ├── AuthService.java       # User authentication & registration logic
    ├── GoogleAuthService.java # Verifies Google ID tokens with Google API
    └── TransactionService.java# Financial calculations & transaction management
```

---

## API Endpoints

### Authentication (`/auth/**`)
| Method | Endpoint | Description | Protected |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Register new account with email & password | No |
| `POST` | `/auth/login` | Sign in with email & password | No |
| `POST` | `/auth/google` | Sign in or register via Google ID token | No |
| `GET` | `/auth/me` | Fetch currently authenticated user profile | Yes (Bearer Token) |

### Transactions (`/api/transactions/**`)
| Method | Endpoint | Description | Protected |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/transactions` | List all transactions for authenticated user | Yes |
| `POST` | `/api/transactions` | Create a new income or expense transaction | Yes |
| `PUT` | `/api/transactions/{id}` | Update existing transaction | Yes |
| `DELETE` | `/api/transactions/{id}` | Delete transaction | Yes |
| `GET` | `/api/transactions/summary`| Consolidated metrics (income, expense, balance) | Yes |

---

## Environment Configuration

Create an `application.properties` or configure environment variables:

| Variable | Default | Description |
| :--- | :--- | :--- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/project_db` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `issac123` | Database password |
| `PORT` | `8080` | Backend HTTP server port |
| `JWT_SECRET` | *(256-bit default)* | HMAC-SHA256 secret key for signing JWTs |
| `GOOGLE_CLIENT_ID` | ` ` | Optional Google OAuth Client ID for verification |

---

## Getting Started Locally

### Prerequisites
- Java 21 or higher installed (`java -version`)
- PostgreSQL database running (`project_db`)

### Running with Maven
```powershell
# Run using the Maven wrapper
.\mvnw.cmd spring-boot:run
```

The server starts on `http://localhost:8080`.
