package com.riskmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing configuration.
 * Enables automatic population of @CreatedDate and @LastModifiedDate fields.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // JPA auditing is enabled via @EnableJpaAuditing on the main application class.
    // This config class exists for explicit clarity and potential future customization
    // (e.g., custom AuditorAware for tracking which user made changes).
}
