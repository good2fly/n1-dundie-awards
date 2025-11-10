package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.event.DundieAwardGranted;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.DundieAwardEvent;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.DundieAwardEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DundieAwardEventRepository dundieAwardEventRepository;
    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(DundieAwardEventRepository dundieAwardEventRepository,
                               ActivityRepository activityRepository) {
        this.dundieAwardEventRepository = dundieAwardEventRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    @Override
    public void createActivityForAwardGranted(DundieAwardGranted event) {

        logger.info("createActivityForAwardGranted: creating activity for award granted event: {}", event);
        DundieAwardEvent dundieAwardEvent = new DundieAwardEvent(event.idempotencyKey());
        dundieAwardEventRepository.save(dundieAwardEvent); // make sure we haven't handled this award grant yet
        // TODO clean these idempotency records up after some configurable time

        Activity activity = new Activity();
        activity.setEvent("Dundie Award was granted to " + event.affectedEmpIds().size() + " employees at organization ID=" + event.orgId());
        activity.setOccurredAt(event.occurredAt());
        activityRepository.save(activity);
    }
}
