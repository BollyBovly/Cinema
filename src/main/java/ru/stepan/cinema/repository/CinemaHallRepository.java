package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.CinemaHall;

import java.util.UUID;

public interface CinemaHallRepository extends CrudRepository<CinemaHall, UUID> {
    boolean existsByCinemaBuildingIdAndName(UUID cinemaId, String name);
}
