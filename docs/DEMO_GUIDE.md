# Internship demo guide (≈ 8 minutes)

**Before you start:** open `/api/health` to wake the server; open `/swagger-ui.html` in a tab.

1. **Problem & idea (1 min)** - Survivors of domestic violence need safe, private, quick access to help. FreeRein
   connects them with resources, counsellors and legal advisors.
2. **Architecture (1 min)** - Show the diagram in README §1: React → Spring Boot REST → PostgreSQL, JWT security, Docker.
3. **Public resources (30 s)** - `GET /api/resources` in Swagger: no login needed because help must be one click away.
4. **Register / login (1 min)** - Login `victim@freerein.demo`. Point out the JWT; click *Authorize* in Swagger.
5. **Victim journey (2 min)** - `GET /api/auth/me`, create a case, create a **private safety plan**, create a support request.
6. **Staff journey (1.5 min)** - Login as `counsellor@freerein.demo`: list `PENDING` requests → `assign` → send a chat message.
   Then try to read the victim's safety plan → **404** - privacy by design.
7. **Authorisation (30 s)** - As victim call `/api/admin/stats` → **403**.
8. **Admin (1 min)** - Login as admin → `/api/admin/stats` (users by role, cases by status...).
9. **Engineering quality (1 min)** - Docker + compose, health check, env-based config, automated tests, uniform error format,
   soft deletes, no secrets in code.
10. **Future work** - see README §7.

Fallback if the network is bad: `docker compose up` locally and run `./docs/demo.sh`.

## Likely questions
- *How is the password stored?* BCrypt hash, never returned by the API.
- *How does login work?* Credentials → server validates → signed JWT (24 h) → sent as `Authorization: Bearer`.
- *Why can a counsellor not see safety plans?* Deliberate privacy rule: only the owner.
- *What stops someone registering as admin?* The registration service rejects it; admins are seeded or promoted by an admin.
- *How is it deployed?* Multi-stage Dockerfile → container on a hosting platform, config through environment variables.
