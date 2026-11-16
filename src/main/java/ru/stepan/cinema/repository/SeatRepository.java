package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.Seat;

import java.util.UUID;

@RepositoryRestResource(path = "seats", collectionResourceRel = "seats")
public interface SeatRepository extends CrudRepository<Seat, UUID> {
}
