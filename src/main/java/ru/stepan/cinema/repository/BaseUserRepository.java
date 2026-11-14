package ru.stepan.cinema.repository;

import ru.stepan.cinema.entity.User;

import java.util.UUID;

public interface BaseUserRepository extends BaseRepository<User, UUID> {
    boolean isEmailTaken(String email);

    boolean isEmailTakenByOther(UUID uuid, String email);
}
