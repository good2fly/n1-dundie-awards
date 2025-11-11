package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.config.RabbitMqConfig;
import com.ninjaone.dundie_awards.event.DundieAwardGranted;
import com.ninjaone.dundie_awards.pubsub.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AwardGrantEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MessageProducer messageProducer;
    private final AwardGrantService awardGrantService;

    public AwardGrantEventListener(MessageProducer messageProducer,
                                   AwardGrantService awardGrantService) {
        this.messageProducer = messageProducer;
        this.awardGrantService = awardGrantService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void giveDundieAwardsAfterCommit(DundieAwardGranted event) {

        // TODO add retry logic for this (e.g. using @Retryable)
        try {
            logger.info("Gave dundie awards after commit: sending event {}", event);
            messageProducer.sendMessage(RabbitMqConfig.ROUTING_KEY, event);
        } catch (Exception e) {
            awardGrantService.rollbackActivityForAwardGranted(event);
        }
    }

}
