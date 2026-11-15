package ru.stepan.cinema.console;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.stepan.cinema.entity.User;
import ru.stepan.cinema.enums.UserRole;
import ru.stepan.cinema.repository.UserRepository;

import java.util.Locale;

@Component
@Profile("!test")
@Order(1)
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:}")
    private String email;
    @Value("${ADMIN_PASSWORD:}")
    private String password;

    @Override
    public void run(String... args) {
        String login = email.trim().toLowerCase(Locale.ROOT);
        if (login.isBlank() || password.isBlank() || userRepository.existsByEmail(login)) {
            return;
        }
        userRepository.save(new User(null, "Admin", "Admin", login, "-",
                UserRole.ADMIN, passwordEncoder.encode(password)));
    }
}
