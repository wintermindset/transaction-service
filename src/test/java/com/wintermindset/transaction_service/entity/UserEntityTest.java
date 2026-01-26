package com.wintermindset.transaction_service.entity;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.wintermindset.transaction_service.enums.user.DeactivationReason;
import com.wintermindset.transaction_service.enums.user.Role;

import static org.assertj.core.api.Assertions.*;

class UserEntityTest {

    /* ---------- creation ---------- */

    @Test
    void shouldCreateActiveUserWithCorrectDefaults() {
        UserEntity user = UserEntityTestFactory.createActiveUser();

        assertThat(user.isActive()).isTrue();
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getDeactivatedAt()).isNull();
        assertThat(user.getDeactivationReason()).isNull();
        assertThat(user.getDeactivatedBy()).isNull();
    }

    /* ---------- deactivate ---------- */

    @Test
    void shouldDeactivateUserWithAuditData() {
        UserEntity user = UserEntityTestFactory.createActiveUser();
        Instant now = Instant.now();

        user.deactivate(now, DeactivationReason.ADMIN_ACTION, Role.ADMIN);

        assertThat(user.isActive()).isFalse();
        assertThat(user.getDeactivatedAt()).isEqualTo(now);
        assertThat(user.getDeactivationReason()).isEqualTo(DeactivationReason.ADMIN_ACTION);
        assertThat(user.getDeactivatedBy()).isEqualTo(Role.ADMIN);
    }

    @Test
    void shouldThrowWhenDeactivatingAlreadyInactiveUser() {
        UserEntity user = UserEntityTestFactory.createDeactivatedUser();

        assertThatThrownBy(() ->
                user.deactivate(
                        Instant.now(),
                        DeactivationReason.ADMIN_ACTION,
                        Role.ADMIN
                )
        ).isInstanceOf(IllegalStateException.class)
         .hasMessage("User already deactivated");
    }

    @Test
    void shouldThrowWhenDeactivationAuditDataIsMissing() {
        UserEntity user = UserEntityTestFactory.createActiveUser();

        assertThatThrownBy(() ->
                user.deactivate(null, DeactivationReason.ADMIN_ACTION, Role.ADMIN)
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                user.deactivate(Instant.now(), null, Role.ADMIN)
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    /* ---------- activate ---------- */

    @Test
    void shouldReactivatePreviouslyDeactivatedUser() {
        UserEntity user = UserEntityTestFactory.createDeactivatedUser();

        user.activate();

        assertThat(user.isActive()).isTrue();
        assertThat(user.getDeactivatedAt()).isNull();
        assertThat(user.getDeactivationReason()).isNull();
        assertThat(user.getDeactivatedBy()).isNull();
    }

    @Test
    void activateShouldBeIdempotentForActiveUser() {
        UserEntity user = UserEntityTestFactory.createActiveUser();

        user.activate();

        assertThat(user.isActive()).isTrue();
    }

    /* ---------- validateState (JPA safeguards) ---------- */

    @Test
    void validateState_shouldPassForValidActiveUser() {
        UserEntity user = UserEntityTestFactory.createActiveUser();

        assertThatCode(user::validateState)
                .doesNotThrowAnyException();
    }

    @Test
    void validateState_shouldPassForValiedDeactivatedUser() {
        UserEntity user = UserEntityTestFactory.createDeactivatedUser();

        assertThatCode(user::validateState)
                .doesNotThrowAnyException();
    }
}
