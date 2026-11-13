# FreeRein - Backend

REST API for **FreeRein**, a domestic-violence support platform. Survivors can find help resources, keep a
private safety plan, open cases, and chat with counsellors / legal advisors. Admins get a dashboard.

**Stack:** Java 17 · Spring Boot 3.2 · Spring Security (stateless JWT) · Spring Data JPA / Hibernate · PostgreSQL ·
springdoc OpenAPI (Swagger UI) · Docker

---

## 1. Architecture

```
React frontend ──HTTPS/JSON──▶  Controllers  ──▶  Services  ──▶  Repositories  ──▶  PostgreSQL
                                   ▲                                   
              JWT filter + role rules (SecurityConfig)   GlobalExceptionHandler (uniform errors)
```

| Layer | Package | What it does |
|---|---|---|
| Controllers | `controller` | HTTP endpoints, request validation, role checks |
| Services | `service` | Business logic (`AuthService`, `AccessService` = who may touch what) |
| Repositories | `repository` | Spring Data JPA queries |
| Entities | `entity` | Database tables |
| Security | `security`, `config/SecurityConfig` | JWT creation/validation, route rules |
| Config | `config` | CORS, OpenAPI, demo-data seeder, DB-URL converter |

### Data model

```mermaid
erDiagram
  USERS ||--o{ CASES : opens
  USERS ||--o{ SAFETY_PLANS : owns
  USERS ||--o{ SUPPORT_REQUESTS : creates
  CASES ||--o{ CASE_NOTES : has
  SUPPORT_REQUESTS ||--o{ SUPPORT_MESSAGES : contains
  USERS { long id string name string email string password string role boolean active }
  CASES { long id string title text description string status }
  SAFETY_PLANS { long id string title text steps text emergencyContacts }
  SUPPORT_REQUESTS { long id string type text description string status string assignedSupporter }
  RESOURCES { long id string title string category string contactInfo }
```

### Roles & permissions

| Capability | Victim | Counsellor / Legal advisor | Admin |
|---|:-:|:-:|:-:|
| Browse resources (public, no login) | ✅ | ✅ | ✅ |
| Own cases / support requests | ✅ | ✅ | ✅ |
| See **all** cases & support requests | ❌ | ✅ | ✅ |
| Take a support request, reply in chat | ❌ | ✅ | ✅ |
| **Safety plans** (private - owner only, even staff get 404) | ✅ own | own only | own only |
| Manage resources, users, dashboard stats | ❌ | ❌ | ✅ |

Public sign-up can create `VICTIM`, `COUNSELLOR`, `LEGAL_ADVISOR`; **`ADMIN` can never be self-registered**.

---

## 2. Run it

### Option A - Docker (easiest, includes Postgres)
```bash
docker compose up --build
# API http://localhost:8080   Swagger http://localhost:8080/swagger-ui.html
```

### Option B - locally (IDE / Maven)
1. Create a Postgres database `FreeRein_db`.
2. Set env vars (or just edit the defaults in `application.properties`):
   ```powershell
   $env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/FreeRein_db"
   $env:SPRING_DATASOURCE_USERNAME="postgres"
   $env:SPRING_DATASOURCE_PASSWORD="your_password"
   ./mvnw spring-boot:run
   ```
   In Eclipse/STS: *Run Configurations → Environment* tab → add the same variables.

Every setting has a local default, so the app now starts even when no variable is set.

### Environment variables
| Variable | Default | Meaning |
|---|---|---|
| `SPRING_DATASOURCE_URL` / `DATABASE_URL` | `jdbc:postgresql://localhost:5432/FreeRein_db` | `jdbc:postgresql://...` **or** `postgres://user:pass@host/db` (auto-converted) |
| `SPRING_DATASOURCE_USERNAME` / `_PASSWORD` | `postgres` / `postgres` | DB credentials |
| `JWT_SECRET` | dev value | **Change in production** (≥ 32 chars) |
| `JWT_EXPIRATION_MS` | `86400000` | Token lifetime (24 h) |
| `CORS_ALLOWED_ORIGINS` | `*` | Comma-separated frontend URLs |
| `SEED_DEMO_USERS` | `true` | Create demo accounts + sample data |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` / `DEMO_PASSWORD` | see `.env.example` | Seeded credentials |
| `DDL_AUTO` | `update` | Hibernate schema mode |
| `PORT` | `8080` | HTTP port (hosting platforms set this) |

---

## 3. API reference
Interactive docs: **`/swagger-ui.html`** (click *Authorize* and paste the token from `/api/auth/login`).

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/health` | public | Status + DB ping |
| POST | `/api/auth/register` | public | Create account → returns JWT |
| POST | `/api/auth/login` | public | Login → returns JWT |
| GET | `/api/auth/me` | any user | Current user |
| GET | `/api/resources`, `/category/{c}`, `/search?q=` | public | Help resources |
| POST/PUT/DELETE | `/api/resources[/{id}]` | admin | Manage resources |
| GET/POST | `/api/cases` | user | List own (staff: all) / open a case (`?status=`) |
| GET/PUT/DELETE | `/api/cases/{id}` | owner/staff | View / edit / archive |
| PATCH | `/api/cases/{id}/status` | owner/staff | OPEN · IN_PROGRESS · RESOLVED · CLOSED |
| GET/POST | `/api/cases/{id}/notes` | owner/staff | Case notes |
| GET/POST/PUT/DELETE | `/api/safety-plans[/{id}]` | owner only | Private safety plans |
| GET/POST | `/api/support/requests` | user | Ask for help (`?status=PENDING`) |
| PATCH | `/api/support/requests/{id}/assign` | staff | Take the request |
| PATCH | `/api/support/requests/{id}/status` | owner/staff | Update status |
| GET/POST | `/api/support/requests/{id}/messages` | owner/staff | Chat thread |
| GET | `/api/admin/stats` | admin | Dashboard numbers |
| GET | `/api/admin/users` | admin | All users |
| PATCH | `/api/admin/users/{id}/active` · `/role` | admin | Disable account / change role |

Errors are always JSON: `{ "success": false, "status": 403, "message": "...", "errors": {...} }`.

---

## 4. Demo accounts (when `SEED_DEMO_USERS=true`)
| Role | Email | Password |
|---|---|---|
| Victim | `victim@freerein.demo` | `Demo@12345` |
| Counsellor | `counsellor@freerein.demo` | `Demo@12345` |
| Legal advisor | `legal@freerein.demo` | `Demo@12345` |
| Admin | `admin@freerein.demo` | `Admin@12345` |

Run the scripted walkthrough: `./docs/demo.sh http://localhost:8080` - see **docs/DEMO_GUIDE.md** for a talking-point script.

## 5. Security design (talking points)
- BCrypt password hashing; password hash is never serialised in responses.
- Stateless JWT (HS256, 24 h), role carried in the token; invalid/expired tokens → clean `401`.
- Method-level security (`@EnableMethodSecurity`) plus URL rules; ownership checks in `AccessService`.
- Safety plans are visible only to their owner; cases/requests are scoped to owner or staff.
- Soft delete (`active=false`) for cases, plans and resources - nothing is destroyed accidentally.
- No stack traces or internal messages returned to clients.

## 6. Tests
`./mvnw test` - runs against an in-memory H2 database (no Postgres needed). Covers health, public resources,
registration/login, admin-registration block, role restrictions, and case / safety-plan privacy.

## 7. Future work
Pagination on list endpoints, refresh tokens, email/SMS alerts for new support requests, WebSocket live chat,
rate limiting on login, Flyway migrations instead of `ddl-auto=update`, audit log.
