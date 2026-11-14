package ru.stepan.cinema.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.stepan.cinema.console.AppConfig;
import ru.stepan.cinema.entity.User;
import ru.stepan.cinema.enums.UserRole;
import ru.stepan.cinema.repository.UserRepository;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Zа-яА-ЯёЁ]+$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MIN_PASSWORD_LENGTH = 5;
    private static final String TAKEN_EMAIL_MESSAGE = "Пользователь с таким email уже существует";
    private final UserRepository userRepository;

    private final AppConfig appConfig;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(String firstName, String surname, String email, String city, String password) {
        String cleanFirstName = validateName(firstName, "Имя");
        String cleanSurname = validateName(surname, "Фамилия");
        String cleanEmail = validateEmail(email, "Email");

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException(TAKEN_EMAIL_MESSAGE);
        }

        validatePassword(password);

        User user = new User(null, cleanFirstName, cleanSurname, cleanEmail, city, UserRole.USER, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Пароль должен содержать минимум " + MIN_PASSWORD_LENGTH + " символов");
        }
    }

    private String validateName(String name, String fieldName) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
        String clean = name.trim();
        if (!NAME_PATTERN.matcher(clean).matches()) {
            throw new IllegalArgumentException(fieldName + " должно состоять только из букв");
        }
        return clean;
    }

    private String validateEmail(String email, String fieldName) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }

        String clean = email.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(clean).matches()) {
            throw new IllegalArgumentException(fieldName + " не корректно введен");
        }

        return clean;
    }

    @Override
    public Optional<User> updateUserEmail(UUID uuid, String newEmail) {
        String cleanNewEmail = validateEmail(newEmail, "Email");
        Optional<User> found = userRepository.findById(uuid);

        if (found.isEmpty()) {
            return Optional.empty();
        }

        if (userRepository.existsByEmailAndIdNot(cleanNewEmail, uuid)) {
            throw new IllegalArgumentException(TAKEN_EMAIL_MESSAGE);
        }
        User user = found.get();
        user.setEmail(cleanNewEmail);

        return Optional.of(userRepository.save(user));

    }

    @Override
    public boolean deleteUserById(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            return false;
        }
        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @PostConstruct
    public void printAppInfo() {
        System.out.println(appConfig.getName() + " v" + appConfig.getVersion());
    }
}
