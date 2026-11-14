package ru.stepan.cinema.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.stepan.cinema.entity.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepository implements BaseUserRepository {
    private final Map<UUID, User> users;

    @Override
    public User save(User entity) {
        entity.setId(UUID.randomUUID());
        users.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void delete(UUID uuid) {
        users.remove(uuid);
    }

    @Override
    public Optional<User> update(User entity) {
        if (users.containsKey(entity.getId())) {
            users.replace(entity.getId(), entity);
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> findById(UUID uuid) {
        return Optional.ofNullable(users.get(uuid));
    }


    @Override
    public boolean isEmailTaken(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean isEmailTakenByOther(UUID uuid, String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email)
                        && !user.getId().equals(uuid));
    }
}
