package com.wintermindset.transaction_service.user.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.wintermindset.transaction_service.user.enums.DeactivationReason;
import com.wintermindset.transaction_service.user.enums.DeactivationSource;
import com.wintermindset.transaction_service.user.enums.UserRole;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_users_username",
        columnNames = "username"
    )
)
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false)
    private UUID userId;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 50)
    private UserRole userRole;

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

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    protected UserProfileEntity() {
        // default constructor for JPA only
    }

    public UserProfileEntity(
        String username,
        String passwordHash,
        String fullName,
        LocalDate birthday,
        UserRole userRole,
        Instant createdAt
    ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.birthday = birthday;
        this.userRole = userRole;
        this.createdAt = createdAt;
        this.lastUpdateAt = createdAt;
        this.isActive = true;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username, Instant occurredAt) {
        this.username = username;
        lastUpdateAt = occurredAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void changePasswordHash(String passwordHash, Instant occurredAt) {
        this.passwordHash = passwordHash;
        lastUpdateAt = occurredAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName, Instant occurredAt) {
        this.fullName = fullName;
        lastUpdateAt = occurredAt;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday, Instant occurredAt) {
        this.birthday = birthday;
        lastUpdateAt = occurredAt;
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

    public DeactivationSource getDeactivatedBy() {
        return deactivatedBy;
    }

    public DeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
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
        isActive = false;
        deactivatedAt = occurredAt;
        deactivationReason = reason;
        this.deactivatedBy = deactivatedBy;
        lastUpdateAt = occurredAt;
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
}