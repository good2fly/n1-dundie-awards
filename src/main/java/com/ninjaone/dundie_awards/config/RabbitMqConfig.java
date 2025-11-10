package com.ninjaone.dundie_awards.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjaone.dundie_awards.pubsub.MessageProducer;
import com.ninjaone.dundie_awards.pubsub.RabbitMqMessageProducer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring runtime configuration for RabbitMQ.
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "dundieawards.exchange";
    public static final String QUEUE_NAME = "dundieawards.activity.queue";
    public static final String ROUTING_KEY = "dundieawards.granted";

    public static final String DEAD_LETTER_QUEUE_NAME = "dundieawards.activity.dlq";
    public static final String DEAD_LETTER_EXCHANGE_NAME = "dundieawards.activity.dlx"; // Dead Letter Exchange

    @Bean
    public TopicExchange dlqExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE_NAME);
    }

    @Bean
    public Queue dlQueue() {
        // Note: in a real app the queue would be durable
        return QueueBuilder.nonDurable(DEAD_LETTER_QUEUE_NAME).build();
    }

    @Bean
    public Binding dlqBinding(Queue dlQueue, TopicExchange dlqExchange) {
        return BindingBuilder.bind(dlQueue).to(dlqExchange).with(ROUTING_KEY);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        // Note: in a real app the queue would be durable
        return QueueBuilder.nonDurable(QUEUE_NAME)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE_NAME)
                .deadLetterRoutingKey(ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageProducer dundieAwardsRabbitMqProducer(RabbitTemplate rabbitTemplate) {
        return new RabbitMqMessageProducer(rabbitTemplate, EXCHANGE_NAME);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,  MessageConverter jsonMessageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
