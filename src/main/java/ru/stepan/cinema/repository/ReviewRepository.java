package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.Review;

import java.util.UUID;

@RepositoryRestResource(path = "reviews", collectionResourceRel = "reviews")
public interface ReviewRepository extends CrudRepository<Review, UUID> {
}
