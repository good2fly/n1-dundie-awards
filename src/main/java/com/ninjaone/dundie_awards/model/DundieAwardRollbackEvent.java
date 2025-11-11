package com.ninjaone.dundie_awards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * A record to keep track of award rollback events, and ensure idempotency of rollbacks.
 */
@Entity
@Table(name = "dundie_award_rollback_events")
@EntityListeners(AuditingEntityListener.class)
public class DundieAwardRollbackEvent { // TODO could share a base class w/ DundieAwardEvent to avoid code duplication

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique idempotency key of the grant event.
     * If, for whatever reason, the event is processed a second time, the DB will prevent this record created again with
     * the same idempotency key, thereby rolling back the whole transaction.
     */
    @Column(name = "idempotency_key",  nullable = false, unique = true, updatable = false)
    private String idempotencyKey;

    /**
     * Audit field to keep track of creation time.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public DundieAwardRollbackEvent() {
    }

    public DundieAwardRollbackEvent(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String id) {
        this.idempotencyKey = idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
