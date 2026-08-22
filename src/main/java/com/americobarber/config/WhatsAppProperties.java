package com.americobarber.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "whatsapp")
public record WhatsAppProperties(
        boolean enabled,
        String apiBaseUrl,
        String apiKey,
        String instanceName
) {
}
