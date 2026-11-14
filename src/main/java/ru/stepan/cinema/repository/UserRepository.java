package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.*;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}