package ru.stepan.cinema.service;

import org.springframework.stereotype.Service;
import ru.stepan.cinema.entity.User;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    User createUser(String firstName, String surname, String email, String city, String password);
    Optional<User> updateUserEmail(UUID uuid, String newEmail);
    boolean deleteUserById(UUID userId);
    Optional<User> getUserById(UUID userId);
}
