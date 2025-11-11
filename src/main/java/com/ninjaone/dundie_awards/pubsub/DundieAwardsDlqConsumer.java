package com.ninjaone.dundie_awards.pubsub;

import com.ninjaone.dundie_awards.config.RabbitMqConfig;
import com.ninjaone.dundie_awards.event.DundieAwardGranted;
import com.ninjaone.dundie_awards.service.ActivityService;
import com.ninjaone.dundie_awards.service.AwardGrantService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer to listen on the DLQ for failed award-granted events and roll back the corresponding awards.
 */
@Component
public class DundieAwardsDlqConsumer {

    private final AwardGrantService awardGrantService;

    public DundieAwardsDlqConsumer(AwardGrantService awardGrantService) {
        this.awardGrantService = awardGrantService;
    }

    @RabbitListener(queues = RabbitMqConfig.DEAD_LETTER_QUEUE_NAME)
    public void handleDundieAwardsGranted(DundieAwardGranted event) {

        awardGrantService.rollbackActivityForAwardGranted(event);
    }
}
