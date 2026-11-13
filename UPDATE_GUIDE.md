# Updating your existing backend repo (FreeRein--Backend)

1. In STS/Eclipse close the project (or just stop the server). Open a terminal in your backend repo folder.
2. Make a safety branch:  `git checkout -b updated-backend`
3. Copy the contents of this zip **over** your repo folder (replace when asked). The zip only has source, config and docs;
   it does NOT contain `.project`, `.classpath`, `.settings` or `.git`, so your IDE setup and history stay untouched.
4. **Delete this old file** (replaced by a bean in SecurityConfig):
   `src/main/java/com/hr/freerein/config/AuthenticationProviderImpl.java`
5. Refresh the project: right-click project > Maven > Update Project (Alt+F5), tick *Force update*.
   (pom.xml gained one test-only dependency: H2.)
6. Run the tests: `./mvnw test`   (uses in-memory H2, needs no Postgres)
7. Run the app. Local defaults point to `jdbc:postgresql://localhost:5432/FreeRein_db`, user `postgres`.
   Set your password:  STS > Run Configurations > Environment > `SPRING_DATASOURCE_PASSWORD`
   (or PowerShell `$env:SPRING_DATASOURCE_PASSWORD="..."`).
8. Check http://localhost:8080/api/health  and  http://localhost:8080/swagger-ui.html
9. Existing database? Run the ALTER statements in `docs/DEPLOYMENT.md` once (or use a fresh database).
10. `git add -A && git commit -m "Fix datasource config, secure auth, add cases/chat/evidence/safety-plan APIs" && git push`
    then merge to your deploy branch; set the environment variables listed in `docs/DEPLOYMENT.md` on the host.

Files changed/added (for reviewing the diff): pom.xml, application.properties, config/*, security/*, exception/*,
controller/*, service/AccessService, entity/*, repository/*, dto/Requests, src/test/*, Dockerfile, docker-compose.yml.
