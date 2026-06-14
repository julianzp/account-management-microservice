package com.julian.account_movement_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientEventsMessagingProperties.class)
public class RabbitMQConfig {

    private final ClientEventsMessagingProperties clientEventsProperties;

    public RabbitMQConfig(ClientEventsMessagingProperties clientEventsProperties) {
        this.clientEventsProperties = clientEventsProperties;
    }

    @Bean
    public DirectExchange clientExchange() {
        return new DirectExchange(clientEventsProperties.exchange());
    }

    @Bean
    public Queue clientCreatedQueue() {
        return new Queue(clientEventsProperties.createdQueue(), true);
    }

    @Bean
    public Queue clientUpdatedQueue() {
        return new Queue(clientEventsProperties.updatedQueue(), true);
    }

    @Bean
    public Binding clientCreatedBinding() {
        return BindingBuilder
                .bind(clientCreatedQueue())
                .to(clientExchange())
                .with(clientEventsProperties.createdRoutingKey());
    }

    @Bean
    public Binding clientUpdatedBinding() {
        return BindingBuilder
                .bind(clientUpdatedQueue())
                .to(clientExchange())
                .with(clientEventsProperties.updatedRoutingKey());
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