package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.CinemaHall;

import java.util.UUID;

@RepositoryRestResource(path = "halls", collectionResourceRel = "halls")
public interface CinemaHallRepository extends CrudRepository<CinemaHall, UUID> {
    boolean existsByCinemaBuildingIdAndName(UUID cinemaId, String name);
}
