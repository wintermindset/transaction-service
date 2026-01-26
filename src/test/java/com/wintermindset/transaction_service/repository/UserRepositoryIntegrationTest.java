package com.wintermindset.transaction_service.repository;

import com.wintermindset.transaction_service.entity.UserEntity;
import com.wintermindset.transaction_service.entity.UserEntityTestFactory;
import com.wintermindset.transaction_service.enums.user.DeactivationReason;
import com.wintermindset.transaction_service.enums.user.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

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
        UserEntity user = UserEntityTestFactory.createActiveUser(username);

        UserEntity savedUser = userRepository.save(user);
        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser)
                .isPresent()
                .get()
                .extracting(UserEntity::getUsername, UserEntity::isActive)
                .containsExactly(username, true);
    }

    @Test
    void saveAndFindById_whenUserIsDeactivated() {
        String username = uniqueUsername("deactivated");
        UserEntity user = UserEntityTestFactory.createDeactivatedUser(username);
        UserEntity savedUser = userRepository.save(user);

        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());
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
        Optional<UserEntity> foundUser = userRepository.findById(nonExistentId);
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByUsername_caseSensitive() {
        String username = uniqueUsername("TestUser");
        UserEntity user = UserEntityTestFactory.createActiveUser(username);
        userRepository.save(user);

        assertThat(userRepository.findByUsername(username)).isPresent();
        assertThat(userRepository.findByUsername(username.toLowerCase())).isEmpty();
    }

    @Test
    void existsByUsername_checksCorrectly() {
        String username = uniqueUsername("jane.doe");
        UserEntity user = UserEntityTestFactory.createActiveUser(username);
        userRepository.save(user);

        assertThat(userRepository.existsByUsername(username)).isTrue();
        assertThat(userRepository.existsByUsername("unknown")).isFalse();
    }

    @Test
    void uniqueConstraintOnUsername_preventsDuplicateUsernames() {
        String username = uniqueUsername("unique.user");
        UserEntity user1 = UserEntityTestFactory.createActiveUser(username);
        userRepository.saveAndFlush(user1);

        UserEntity user2 = UserEntityTestFactory.createActiveUser(username);
        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void updatePassword_works() {
        String username = uniqueUsername("user.with.password");
        UserEntity user = UserEntityTestFactory.createActiveUser(username);
        UserEntity savedUser = userRepository.save(user);

        savedUser.setPasswordHash("new-hashed-password");
        userRepository.save(savedUser);

        assertThat(userRepository.findById(savedUser.getId()))
                .isPresent()
                .get()
                .extracting(UserEntity::getPasswordHash)
                .isEqualTo("new-hashed-password");
    }

    @Test
    void deleteUser_works() {
        String username = uniqueUsername("to.delete");
        UserEntity user = UserEntityTestFactory.createActiveUser(username);
        UserEntity savedUser = userRepository.save(user);

        userRepository.delete(savedUser);

        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    void findAll_andSaveAll_works() {
        List<UserEntity> users = List.of(
                UserEntityTestFactory.createActiveUser(uniqueUsername("user1")),
                UserEntityTestFactory.createActiveUser(uniqueUsername("user2")),
                UserEntityTestFactory.createActiveUser(uniqueUsername("user3"))
        );
        userRepository.saveAll(users);

        List<UserEntity> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(3);
    }

    @Test
    void deactivateAndActivateUser_works() {
        String username = uniqueUsername("state.change");
        UserEntity user = UserEntityTestFactory.createActiveUser(username);
        UserEntity savedUser = userRepository.save(user);

        savedUser.deactivate(Instant.now(), DeactivationReason.USER_REQUEST, Role.ADMIN);
        userRepository.save(savedUser);

        Optional<UserEntity> deactivatedUser = userRepository.findById(savedUser.getId());
        assertThat(deactivatedUser).isPresent();
        assertThat(deactivatedUser.get().isActive()).isFalse();
        assertThat(deactivatedUser.get().getDeactivationReason()).isEqualTo(DeactivationReason.USER_REQUEST);

        deactivatedUser.get().activate();
        userRepository.save(deactivatedUser.get());

        Optional<UserEntity> activatedUser = userRepository.findById(savedUser.getId());
        assertThat(activatedUser).isPresent();
        assertThat(activatedUser.get().isActive()).isTrue();
        assertThat(activatedUser.get().getDeactivationReason()).isNull();
    }

    @Test
    void userWithDifferentRoles_works() {
        String username = uniqueUsername("admin.user");
        UserEntity user = UserEntityTestFactory.createActiveUser(username, Role.ADMIN);
        UserEntity savedUser = userRepository.save(user);

        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRole()).isEqualTo(Role.ADMIN);
    }
}
