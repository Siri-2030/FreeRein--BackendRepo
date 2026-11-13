package com.hr.freerein.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Adds the "Authorize" button to Swagger UI so you can paste a JWT and try protected endpoints. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI freeReinOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FreeRein API")
                        .version("1.1.0")
                        .description("Backend for the FreeRein domestic-violence support platform: "
                                + "authentication, resources, cases, private safety plans, "
                                + "support requests with chat, and an admin dashboard."))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
