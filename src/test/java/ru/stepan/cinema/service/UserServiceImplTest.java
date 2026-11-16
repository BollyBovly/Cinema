package ru.stepan.cinema.service;


import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.stepan.cinema.console.AppConfig;
import ru.stepan.cinema.entity.User;
import ru.stepan.cinema.enums.UserRole;
import ru.stepan.cinema.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private AppConfig appConfig;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl service;


    @Test
    void createUser_validData_savesNormalizedUserWithHashedPassword() {
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.createUser(" Ivan ", "Petrov", "  Ivan@Mail.RU ", "Moscow", "secret1");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Ivan");
        assertThat(saved.getEmail()).isEqualTo("ivan@mail.ru");
        assertThat(saved.getPasswordHash()).isEqualTo("hashed");
        assertThat(saved.getRole()).isEqualTo(UserRole.USER);
        assertThat(result).isSameAs(saved);
    }

    @Test
    void createUser_blankFirstName_throws() {
        assertRejectedWithoutSaving(
                () -> service.createUser("  ", "Petrov", "ivan@mail.ru", "City", "secret1"), "Имя");
    }

    @Test
    void createUser_digitsInSurname_throws() {
        assertRejectedWithoutSaving(
                () -> service.createUser("Ivan", "Petrov2", "ivan@mail.ru", "City", "secret1"), "Фамилия");
    }

    @Test
    void createUser_malformedEmail_throws() {
        assertRejectedWithoutSaving(
                () -> service.createUser("Ivan", "Petrov", "not-an-email", "City", "secret1"), "Email");
    }

    @Test
    void createUser_shortPassword_throws() {
        assertRejectedWithoutSaving(
                () -> service.createUser("Ivan", "Petrov", "ivan@mail.ru", "City", "123"), "Пароль");
    }

    @Test
    void createUser_nullPassword_throws() {
        assertRejectedWithoutSaving(
                () -> service.createUser("Ivan", "Petrov", "ivan@mail.ru", "City", null), "Пароль");
    }

    @Test
    void createUser_emailAlreadyTaken_throws() {
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(true);

        assertRejectedWithoutSaving(
                () -> service.createUser("Ivan", "Petrov", "ivan@mail.ru", "City", "secret1"), "уже существует");
    }


    @Test
    void updateUserEmail_freeEmail_updatesAndSaves() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ivan", "Petrov", "old@mail.ru", "City", UserRole.USER, "hash");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("new@mail.ru", id)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        Optional<User> result = service.updateUserEmail(id, " New@Mail.ru ");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("new@mail.ru");
        verify(userRepository).save(user);
    }

    @Test
    void updateUserEmail_userNotFound_returnsEmptyAndDoesNotSave() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(service.updateUserEmail(id, "new@mail.ru")).isEmpty();
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserEmail_emailTakenByAnotherUser_throws() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ivan", "Petrov", "old@mail.ru", "City", UserRole.USER, "hash");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("new@mail.ru", id)).thenReturn(true);

        assertThatThrownBy(() -> service.updateUserEmail(id, "new@mail.ru"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserEmail_invalidEmail_throwsBeforeTouchingRepository() {
        assertThatThrownBy(() -> service.updateUserEmail(UUID.randomUUID(), "bad"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(userRepository);
    }


    @Test
    void deleteUserById_existingUser_deletesAndReturnsTrue() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));

        assertThat(service.deleteUserById(id)).isTrue();
        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUserById_missingUser_returnsFalseAndDeletesNothing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(service.deleteUserById(id)).isFalse();
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getUserByEmail_normalizesEmailBeforeLookup() {
        User user = new User();
        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(Optional.of(user));

        assertThat(service.getUserByEmail("  Ivan@Mail.RU ")).containsSame(user);
    }

    @Test
    void getUserByEmail_blankEmail_returnsEmptyWithoutQuery() {
        assertThat(service.getUserByEmail("  ")).isEmpty();
        verifyNoInteractions(userRepository);
    }

    private void assertRejectedWithoutSaving(ThrowableAssert.ThrowingCallable call, String messagePart) {
        assertThatThrownBy(call)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(messagePart);
        verify(userRepository, never()).save(any());
    }
}
