package com.wintermindset.transaction_service.user.entity;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.wintermindset.transaction_service.user.enums.DeactivationReason;
import com.wintermindset.transaction_service.user.enums.DeactivationSource;
import com.wintermindset.transaction_service.user.enums.UserRole;
import com.wintermindset.transaction_service.user.factory.UserEntityTestFactory;

import static org.assertj.core.api.Assertions.*;

class UserEntityTest {

    /* ---------- creation ---------- */

    @Test
    void shouldCreateActiveUserWithCorrectDefaults() {
        UserProfileEntity user = UserEntityTestFactory.createActiveUser();

        assertThat(user.isActive()).isTrue();
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getDeactivatedAt()).isNull();
        assertThat(user.getDeactivationReason()).isNull();
        assertThat(user.getDeactivatedBy()).isNull();
    }

    /* ---------- deactivate ---------- */

    @Test
    void shouldDeactivateUserWithAuditData() {
        UserProfileEntity user = UserEntityTestFactory.createActiveUser();
        Instant now = Instant.now();

        user.deactivate(now, DeactivationReason.ADMIN_ACTION, DeactivationSource.ADMIN);

        assertThat(user.isActive()).isFalse();
        assertThat(user.getDeactivatedAt()).isEqualTo(now);
        assertThat(user.getDeactivationReason()).isEqualTo(DeactivationReason.ADMIN_ACTION);
        assertThat(user.getDeactivatedBy()).isEqualTo(DeactivationSource.ADMIN);
    }

    @Test
    void shouldThrowWhenDeactivatingAlreadyInactiveUser() {
        UserProfileEntity user = UserEntityTestFactory.createDeactivatedUser();

        assertThatThrownBy(() ->
                user.deactivate(
                        Instant.now(),
                        DeactivationReason.ADMIN_ACTION,
                        DeactivationSource.ADMIN
                )
        ).isInstanceOf(IllegalStateException.class)
         .hasMessage("User already deactivated");
    }

    @Test
    void shouldThrowWhenDeactivationAuditDataIsMissing() {
        UserProfileEntity user = UserEntityTestFactory.createActiveUser();

        assertThatThrownBy(() ->
                user.deactivate(null, DeactivationReason.ADMIN_ACTION, DeactivationSource.ADMIN)
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                user.deactivate(Instant.now(), null, DeactivationSource.ADMIN)
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    /* ---------- activate ---------- */

    @Test
    void shouldReactivatePreviouslyDeactivatedUser() {
        UserProfileEntity user = UserEntityTestFactory.createDeactivatedUser();

        user.activate(Instant.now());

        assertThat(user.isActive()).isTrue();
        assertThat(user.getDeactivatedAt()).isNull();
        assertThat(user.getDeactivationReason()).isNull();
        assertThat(user.getDeactivatedBy()).isNull();
    }

    @Test
    void activateShouldBeIdempotentForActiveUser() {
        UserProfileEntity user = UserEntityTestFactory.createActiveUser();

        user.activate(Instant.now());

        assertThat(user.isActive()).isTrue();
    }
}