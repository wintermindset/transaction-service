package com.wintermindset.transaction_service.entity;

import java.time.Instant;
import java.util.UUID;

import com.wintermindset.transaction_service.enums.user.DeactivationReason;
import com.wintermindset.transaction_service.enums.user.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_users_username",
        columnNames = "username"
    )
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivation_reason", length = 50)
    private DeactivationReason deactivationReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivated_by", length = 100)
    private Role deactivatedBy;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    protected UserEntity() {
        // default constructor for JPA only
    }

    public UserEntity(
            String username,
            String passwordHash,
            Role role,
            Instant createdAt
    ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
        this.active = true;
    }

    /**
     * Safeguard only.
     */
    @PrePersist
    void onCreate() {
        validateState();
    }

    /**
     * Safeguard only.
     */
    @PreUpdate
    void onUpdate() {
        validateState();
    }

    private void validateState() {
        if (active) {
            if (deactivatedAt != null
                || deactivationReason != null
                || deactivatedBy != null) {
                throw new IllegalStateException(
                    "Active user must not have deactivation audit data"
                );
            }
        } else {
            if (deactivatedAt == null
                || deactivationReason == null
                || deactivatedBy == null) {
                throw new IllegalStateException(
                    "Inactive user must have full deactivation audit data"
                );
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    public DeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public Role getDeactivatedBy() {
        return deactivatedBy;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void deactivate(
            Instant occurredAt,
            DeactivationReason reason,
            Role deactivatedBy
    ) {
        if (!active) {
            throw new IllegalStateException("User already deactivated");
        }
        if (occurredAt == null || reason == null || deactivatedBy == null) {
            throw new IllegalArgumentException("Deactivation audit data is required");
        }
        active = false;
        deactivatedAt = occurredAt;
        deactivationReason = reason;
        this.deactivatedBy = deactivatedBy;
    }

    public void activate() {
        if (active) {
            return;
        }
        active = true;
        deactivatedAt = null;
        deactivationReason = null;
        deactivatedBy = null;
    }
}
