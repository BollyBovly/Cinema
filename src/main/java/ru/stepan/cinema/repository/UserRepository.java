package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.*;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "users", collectionResourceRel = "users")
public interface UserRepository extends CrudRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID id);
}