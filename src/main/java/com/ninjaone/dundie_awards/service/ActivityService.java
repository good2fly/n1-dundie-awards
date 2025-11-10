package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.event.DundieAwardGranted;

public interface ActivityService {

    void createActivityForAwardGranted(DundieAwardGranted event);
}
