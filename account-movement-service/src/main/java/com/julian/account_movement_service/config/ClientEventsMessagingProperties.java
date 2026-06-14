package com.julian.account_movement_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging.client-events")
public record ClientEventsMessagingProperties(
        String exchange,
        String createdQueue,
        String updatedQueue,
        String createdRoutingKey,
        String updatedRoutingKey
) {
}