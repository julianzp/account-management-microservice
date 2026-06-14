package com.julian.account_movement_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CLIENT_EXCHANGE = "client.exchange";
    public static final String CLIENT_CREATED_QUEUE = "client.created.queue";
    public static final String CLIENT_UPDATED_QUEUE = "client.updated.queue";

    public static final String CLIENT_CREATED_ROUTING_KEY = "client.created";
    public static final String CLIENT_UPDATED_ROUTING_KEY = "client.updated";

    @Bean
    public DirectExchange clientExchange() {
        return new DirectExchange(CLIENT_EXCHANGE);
    }

    @Bean
    public Queue clientCreatedQueue() {
        return new Queue(CLIENT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue clientUpdatedQueue() {
        return new Queue(CLIENT_UPDATED_QUEUE, true);
    }

    @Bean
    public Binding clientCreatedBinding() {
        return BindingBuilder
                .bind(clientCreatedQueue())
                .to(clientExchange())
                .with(CLIENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding clientUpdatedBinding() {
        return BindingBuilder
                .bind(clientUpdatedQueue())
                .to(clientExchange())
                .with(CLIENT_UPDATED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}