package com.ninjaone.dundie_awards.pubsub;

import com.ninjaone.dundie_awards.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMqMessageProducer implements MessageProducer {

    private final Logger log = LoggerFactory.getLogger(RabbitMqMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public RabbitMqMessageProducer(RabbitTemplate rabbitTemplate, String exchangeName) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    @Override
    public void sendMessage(String destination, Object message) {
        rabbitTemplate.convertAndSend(exchangeName, RabbitMqConfig.ROUTING_KEY, message);
    }
}
