package com.ninjaone.dundie_awards.pubsub;

import com.ninjaone.dundie_awards.config.RabbitMqConfig;
import com.ninjaone.dundie_awards.event.DundieAwardGranted;
import com.ninjaone.dundie_awards.service.ActivityService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DundieAwardsConsumer {

    private final ActivityService activityService;

    public DundieAwardsConsumer(ActivityService activityService) {
        this.activityService = activityService;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void handleDundieAwardsGranted(DundieAwardGranted event) {
        activityService.createActivityForAwardGranted(event);
    }
}
