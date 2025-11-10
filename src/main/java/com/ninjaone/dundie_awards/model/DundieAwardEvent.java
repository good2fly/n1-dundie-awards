package com.ninjaone.dundie_awards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * A record to keep track of award grant events, and ensure idempotency of grants.
 */
@Entity
@Table(name = "dundie_award_events")
@EntityListeners(AuditingEntityListener.class)
public class DundieAwardEvent {

    /**
     * The ID of the record will be the idempotency key of the grant event.
     * Since IDs are unique by definition, this ensures that no event will be processed twice. If, for whatever reason,
     * the event is processed a second time, the DB will prevent this record created again with the same ID.
     */
    @Id
    private String id;

    /**
     * Audit field to keep track of creation time.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public DundieAwardEvent() {
    }

    public DundieAwardEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
