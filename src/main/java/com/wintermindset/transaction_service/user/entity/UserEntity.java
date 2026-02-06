package com.wintermindset.transaction_service.user.entity;

import java.time.Instant;
import java.util.UUID;

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

import com.wintermindset.transaction_service.user.enums.DeactivationReason;
import com.wintermindset.transaction_service.user.enums.UserRole;

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
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 50)
    private UserRole userRole;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivation_reason", length = 50)
    private DeactivationReason deactivationReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivated_by", length = 100)
    private UserRole deactivatedBy;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    protected UserEntity() {
        // default constructor for JPA only
    }

    public UserEntity(
        String username,
        String passwordHash,
        UserRole userRole,
        Instant createdAt
    ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.createdAt = createdAt;
        this.isActive = true;
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

    public UserRole getUserRole() {
        return userRole;
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

    public DeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public UserRole getDeactivatedBy() {
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
        UserRole deactivatedBy
    ) {
        if (!isActive) {
            throw new IllegalStateException("User already deactivated");
        }
        if (occurredAt == null || reason == null || deactivatedBy == null) {
            throw new IllegalArgumentException("Deactivation audit data is required");
        }
        isActive = false;
        deactivatedAt = occurredAt;
        deactivationReason = reason;
        this.deactivatedBy = deactivatedBy;
    }

    public void activate() {
        if (isActive) {
            return;
        }
        isActive = true;
        deactivatedAt = null;
        deactivationReason = null;
        deactivatedBy = null;
    }

    /**
     * Safeguard only.
     */
    @PrePersist
    @PreUpdate
    public void validateState() {
        if (isActive) {
            if (deactivatedAt != null
                || deactivationReason != null
                || deactivatedBy != null
            ) {
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
}
