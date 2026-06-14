package com.julian.client_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ClientEventsMessagingProperties.class)
public class RabbitMQConfig {

    private final ClientEventsMessagingProperties properties;

    @Bean
    public DirectExchange clientExchange() {
        return new DirectExchange(properties.exchange());
    }

    @Bean
    public Queue clientCreatedQueue() {
        return new Queue(properties.createdQueue(), true);
    }

    @Bean
    public Queue clientUpdatedQueue() {
        return new Queue(properties.updatedQueue(), true);
    }

    @Bean
    public Binding clientCreatedBinding() {
        return BindingBuilder
                .bind(clientCreatedQueue())
                .to(clientExchange())
                .with(properties.createdRoutingKey());
    }

    @Bean
    public Binding clientUpdatedBinding() {
        return BindingBuilder
                .bind(clientUpdatedQueue())
                .to(clientExchange())
                .with(properties.updatedRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}