# Bringing the backend live again

## Why it broke
`application.properties` read the DB settings from `${SPRING_DATASOURCE_URL}` etc. Where the app was started, those
environment variables did not exist, so Spring handed the literal text `${SPRING_DATASOURCE_URL}` to the Postgres driver →
*"Driver org.postgresql.Driver claims to not accept jdbcUrl"*. Fixed: every setting now has a default, and a
`postgres://user:pass@host/db` URL is converted automatically.

## Checklist for the hosted deployment (Docker web service, e.g. Render)
1. **Database still exists?** Free hosted Postgres instances are often deleted/expire after ~30 days. If yours is gone,
   create a new one (this is the most likely reason a deployment that worked last year is dead today).
2. In the web-service **Environment** tab set:
   - `SPRING_DATASOURCE_URL` = the database's *Internal* URL (either `jdbc:postgresql://host:5432/db` or the
     `postgres://...` form - both work). For an *External* URL append `?sslmode=require`.
   - `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` (not needed if they are inside the `postgres://` URL)
   - `JWT_SECRET` = any long random string
   - `CORS_ALLOWED_ORIGINS` = your frontend URL
   - `SEED_DEMO_USERS` = `true` for the demo
3. Push this code to GitHub → the host rebuilds from the `Dockerfile` automatically.
4. Open `https://<your-service>/api/health` → should show `"status":"UP"`. Then `/swagger-ui.html`.
5. Free instances sleep after inactivity: **open the health URL a few minutes before the demo** to wake it
   (first request can take ~1 minute).
6. Point the frontend's API base URL at the new backend URL.

## Existing database note
New columns for long text (`safety_plans.steps`, `support_requests.description`, ...) are `TEXT`. Hibernate's `update`
mode will not widen columns that already exist as `varchar(255)`. On an **old** database run once:
```sql
ALTER TABLE safety_plans ALTER COLUMN steps TYPE TEXT, ALTER COLUMN emergency_contacts TYPE TEXT, ALTER COLUMN notes TYPE TEXT;
ALTER TABLE support_requests ALTER COLUMN description TYPE TEXT;
ALTER TABLE support_messages ALTER COLUMN content TYPE TEXT;
ALTER TABLE resources ALTER COLUMN description TYPE VARCHAR(500), ALTER COLUMN content TYPE TEXT;
```
(or simply use a fresh database).

## Local run in 3 commands
```bash
cp .env.example .env     # edit values
docker compose up --build
curl http://localhost:8080/api/health
```

## Frontend (React) hosting
Build with the backend URL baked in, then upload `dist/` to any static host (Netlify, Vercel, Render Static, GitHub Pages):
```bash
cd FreeRein
VITE_API_URL=https://<your-backend>/api npm run build     # PowerShell: $env:VITE_API_URL="https://<your-backend>/api"; npm run build
```
`public/_redirects` already makes Netlify-style hosts serve `index.html` for client-side routes.
Then set `CORS_ALLOWED_ORIGINS=https://<your-frontend>` on the backend.
