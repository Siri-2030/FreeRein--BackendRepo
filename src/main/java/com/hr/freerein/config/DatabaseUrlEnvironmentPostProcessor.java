package com.hr.freerein.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Hosting platforms (Render, Heroku, Railway ...) hand out a database URL like
 * <pre>postgres://user:password@host:5432/dbname</pre>
 * but the JDBC driver needs
 * <pre>jdbc:postgresql://host:5432/dbname</pre>
 * Passing the first form is one of the most common reasons a Spring Boot app
 * "claims to not accept jdbcUrl". This post-processor converts it automatically
 * (and also picks up the credentials embedded in the URL).
 *
 * Looks at SPRING_DATASOURCE_URL first, then DATABASE_URL.
 * A value that already starts with "jdbc:" is left untouched.
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String raw = firstNonBlank(
                environment.getProperty("SPRING_DATASOURCE_URL"),
                environment.getProperty("DATABASE_URL"));

        if (raw == null) {
            return;
        }
        raw = raw.trim();
        if (!(raw.startsWith("postgres://") || raw.startsWith("postgresql://"))) {
            return; // already a jdbc: URL (or something we should not touch)
        }

        URI uri = URI.create(raw);
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        StringBuilder jdbc = new StringBuilder("jdbc:postgresql://")
                .append(uri.getHost()).append(':').append(port)
                .append(uri.getPath() == null ? "" : uri.getPath());
        if (uri.getRawQuery() != null && !uri.getRawQuery().isEmpty()) {
            jdbc.append('?').append(uri.getRawQuery());
        }

        Map<String, Object> overrides = new HashMap<>();
        overrides.put("spring.datasource.url", jdbc.toString());

        String userInfo = uri.getUserInfo(); // already percent-decoded
        if (userInfo != null && !userInfo.isEmpty()) {
            int idx = userInfo.indexOf(':');
            if (idx >= 0) {
                overrides.put("spring.datasource.username", userInfo.substring(0, idx));
                overrides.put("spring.datasource.password", userInfo.substring(idx + 1));
            } else {
                overrides.put("spring.datasource.username", userInfo);
            }
        }

        environment.getPropertySources().addFirst(new MapPropertySource("databaseUrlOverrides", overrides));
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
