package com.riskmanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tradeRiskOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trade Risk Monitoring & Analytics Platform API")
                        .description("""
                                Enterprise-grade REST API for trade risk monitoring and analytics.
                                
                                Simulates investment banking risk management systems used at firms
                                like Nomura, JPMorgan, Goldman Sachs, and Morgan Stanley.
                                
                                **Core Capabilities:**
                                - Trade capture and lifecycle management
                                - Real-time position aggregation
                                - Risk exposure and VaR calculation
                                - PnL monitoring (mark-to-market)
                                - Risk limit breach detection and alerting
                                - Regulatory-style reporting
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Risk Technology Team")
                                .email("risk-tech@bank.com"))
                        .license(new License()
                                .name("Internal Use Only")))
                .servers(List.of(
                        new Server().url("/api/v1").description("API v1")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")));
    }
}
