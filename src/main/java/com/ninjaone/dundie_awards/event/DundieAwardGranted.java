package com.ninjaone.dundie_awards.event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public record DundieAwardGranted(String idempotencyKey, long orgId, LocalDateTime occurredAt, Collection<Long> affectedEmpIds) {

    public DundieAwardGranted(long orgId, LocalDateTime occurredAt, Set<Long> affectedEmpIds) {
        this(UUID.randomUUID().toString(), orgId, occurredAt, Collections.unmodifiableSet(affectedEmpIds));
    }
}
