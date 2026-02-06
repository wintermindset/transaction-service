package com.wintermindset.transaction_service.user.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.wintermindset.transaction_service.user.enums.ContactType;
import com.wintermindset.transaction_service.user.enums.DeactivationReason;
import com.wintermindset.transaction_service.user.enums.DeactivationSource;

@Entity
@Table(
    name = "user_contacts",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_users_values",
        columnNames = "value"
    )
)
public class UserContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id", updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "user_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(
            name = "fk_user_contact_user"
        )
    )
    private UserProfileEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false, length = 50)
    private ContactType contactType;

    @Column(name = "value", nullable = false, length = 50)
    private String value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_update_at", nullable = false)
    private Instant lastUpdateAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivated_by", length = 50)
    private DeactivationSource deactivatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivation_reason", length = 50)
    private DeactivationReason deactivationReason;

    public UserContactEntity(
        UserProfileEntity user,
        ContactType contactType,
        String value,
        Instant createdAt
    ) {
        this.user = user;
        this.contactType = contactType;
        this.value = value;
        this.createdAt = createdAt;
        this.lastUpdateAt = createdAt;
        this.isActive = true;
    }

    public Long getId() {
        return id;
    }

    public UserProfileEntity getUser() {
        return user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    public DeactivationSource getDeactivatedBy() {
        return deactivatedBy;
    }

    public DeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public void deactivate(
        Instant occurredAt,
        DeactivationReason reason,
        DeactivationSource deactivatedBy
    ) {
        if (!isActive) {
            throw new IllegalStateException("User already deactivated");
        }
        if (occurredAt == null || reason == null || deactivatedBy == null) {
            throw new IllegalArgumentException("Deactivation audit data is required");
        }
        lastUpdateAt = occurredAt;
        isActive = false;
        deactivatedAt = occurredAt;
        deactivationReason = reason;
        this.deactivatedBy = deactivatedBy;
    }

    public void activate(Instant occurredAt) {
        if (isActive) {
            return;
        }
        lastUpdateAt = occurredAt;
        isActive = true;
        deactivatedAt = null;
        deactivationReason = null;
        deactivatedBy = null;
    }

    public void changeContact(
        ContactType contactType,
        String value,
        Instant occurredAt
    ) {
        this.contactType = contactType;
        this.value = value;
        lastUpdateAt = occurredAt;
    }
}