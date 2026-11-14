package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.Review;

import java.util.UUID;

public interface ReviewRepository extends CrudRepository<Review, UUID> {
}
