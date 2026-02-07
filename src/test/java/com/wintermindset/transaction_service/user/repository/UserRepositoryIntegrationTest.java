package com.wintermindset.transaction_service.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wintermindset.transaction_service.user.enums.DeactivationReason;
import com.wintermindset.transaction_service.user.enums.DeactivationSource;
import com.wintermindset.transaction_service.user.enums.UserRole;
import com.wintermindset.transaction_service.user.entity.UserProfileEntity;
import com.wintermindset.transaction_service.user.factory.UserEntityTestFactory;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserProfileRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    private String uniqueUsername(String base) {
        return base + "-" + UUID.randomUUID();
    }

    @Test
    void saveAndFindById() {
        String username = uniqueUsername("testuser");
        UserProfileEntity user = UserEntityTestFactory.createActiveUser(username);

        UserProfileEntity savedUser = userRepository.save(user);
        Optional<UserProfileEntity> foundUser = userRepository.findById(savedUser.getUserId());

        assertThat(foundUser)
                .isPresent()
                .get()
                .extracting(UserProfileEntity::getUsername, UserProfileEntity::isActive)
                .containsExactly(username, true);
    }

    @Test
    void saveAndFindById_whenUserIsDeactivated() {
        String username = uniqueUsername("deactivated");
        UserProfileEntity user = UserEntityTestFactory.createDeactivatedUser(username);
        UserProfileEntity savedUser = userRepository.save(user);

        Optional<UserProfileEntity> foundUser = userRepository.findById(savedUser.getUserId());
        assertThat(foundUser)
                .isPresent()
                .get()
                .satisfies(u -> {
                    assertThat(u.isActive()).isFalse();
                    assertThat(u.getDeactivationReason()).isEqualTo(DeactivationReason.ADMIN_ACTION);
                });
    }

    @Test
    void findById_whenUserDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<UserProfileEntity> foundUser = userRepository.findById(nonExistentId);
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByUsername_caseSensitive() {
        String username = uniqueUsername("TestUser");
        UserProfileEntity user = UserEntityTestFactory.createActiveUser(username);
        userRepository.save(user);

        assertThat(userRepository.findByUsername(username)).isPresent();
        assertThat(userRepository.findByUsername(username.toLowerCase())).isEmpty();
    }

    @Test
    void existsByUsername_checksCorrectly() {
        String username = uniqueUsername("jane.doe");
        UserProfileEntity user = UserEntityTestFactory.createActiveUser(username);
        userRepository.save(user);

        assertThat(userRepository.existsByUsername(username)).isTrue();
        assertThat(userRepository.existsByUsername("unknown")).isFalse();
    }

    @Test
    void uniqueConstraintOnUsername_preventsDuplicateUsernames() {
        String username = uniqueUsername("unique.user");
        UserProfileEntity user1 = UserEntityTestFactory.createActiveUser(username);
        userRepository.saveAndFlush(user1);

        UserProfileEntity user2 = UserEntityTestFactory.createActiveUser(username);
        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void updatePassword_works() {
        String username = uniqueUsername("user.with.password");
        UserProfileEntity user = UserEntityTestFactory.createActiveUser(username);
        UserProfileEntity savedUser = userRepository.save(user);

        savedUser.changePasswordHash("new-hashed-password", Instant.now());
        userRepository.save(savedUser);

        assertThat(userRepository.findById(savedUser.getUserId()))
                .isPresent()
                .get()
                .extracting(UserProfileEntity::getPasswordHash)
                .isEqualTo("new-hashed-password");
    }

    @Test
    void deleteUser_works() {
        String username = uniqueUsername("to.delete");
        UserProfileEntity user = UserEntityTestFactory.createActiveUser(username);
        UserProfileEntity savedUser = userRepository.save(user);

        userRepository.delete(savedUser);

        assertThat(userRepository.findById(savedUser.getUserId())).isEmpty();
    }

    @Test
    void findAll_andSaveAll_works() {
        List<UserProfileEntity> users = List.of(
                UserEntityTestFactory.createActiveUser(uniqueUsername("user1")),
                UserEntityTestFactory.createActiveUser(uniqueUsername("user2")),
                UserEntityTestFactory.createActiveUser(uniqueUsername("user3"))
        );
        userRepository.saveAll(users);

        List<UserProfileEntity> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(3);
    }

    @Test
    void deactivateAndActivateUser_works() {
        String username = uniqueUsername("state.change");
        UserProfileEntity user = UserEntityTestFactory.createActiveUser(username);
        UserProfileEntity savedUser = userRepository.save(user);

        savedUser.deactivate(Instant.now(), DeactivationReason.USER_REQUEST, DeactivationSource.ADMIN);
        userRepository.save(savedUser);

        Optional<UserProfileEntity> deactivatedUser = userRepository.findById(savedUser.getUserId());
        assertThat(deactivatedUser).isPresent();
        assertThat(deactivatedUser.get().isActive()).isFalse();
        assertThat(deactivatedUser.get().getDeactivationReason()).isEqualTo(DeactivationReason.USER_REQUEST);

        deactivatedUser.get().activate(Instant.now());
        userRepository.save(deactivatedUser.get());

        Optional<UserProfileEntity> activatedUser = userRepository.findById(savedUser.getUserId());
        assertThat(activatedUser).isPresent();
        assertThat(activatedUser.get().isActive()).isTrue();
        assertThat(activatedUser.get().getDeactivationReason()).isNull();
    }

    @Test
    void userWithDifferentRoles_works() {
        String username = uniqueUsername("admin.user");
        UserProfileEntity user = UserEntityTestFactory.createActiveUser(username, UserRole.ADMIN);
        UserProfileEntity savedUser = userRepository.save(user);

        Optional<UserProfileEntity> foundUser = userRepository.findById(savedUser.getUserId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserRole()).isEqualTo(UserRole.ADMIN);
    }
}
