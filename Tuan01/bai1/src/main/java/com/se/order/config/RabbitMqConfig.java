package com.se.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.queue.hello}")
    private String queueName;
    
    public static final String EXCHANGE_NAME = "order-exchange";
    public static final String ROUTING_KEY = "order.routing.key";

    @Bean
    public Queue helloQueue() {
        return new Queue(queueName, true);
    }
    
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }
    
    @Bean
    public Binding binding(Queue helloQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(helloQueue)
                .to(orderExchange)
                .with(ROUTING_KEY);
    }
}

