package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.event.DundieAwardGranted;

public interface AwardGrantService {

    /**
     * Give a Dundie Award for all current employees of the specified organization.
     * Also send an event on the message bus, and if the event processing fails (i.e. we get the event back
     * on a DLQ), roll back the awards.
     *
     * @param organizationId
     */
    void giveDundieAwards(long organizationId);

    /**
     * Roll back previously granted Dundie Awards.
     * This implementation is idempotent.
     *
     * @param event
     */
    void rollbackActivityForAwardGranted(DundieAwardGranted event);
}
