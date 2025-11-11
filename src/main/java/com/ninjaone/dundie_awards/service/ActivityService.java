package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.event.DundieAwardGranted;

public interface ActivityService {

    /**
     * Create an activity record for the specified Dundie Awards grant.
     * This implementation is idempotent.
     *
     * @param event
     */
    void createActivityForAwardGranted(DundieAwardGranted event);
}
