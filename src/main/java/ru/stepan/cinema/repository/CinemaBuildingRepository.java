package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.CinemaBuilding;

import java.util.UUID;

@RepositoryRestResource(path = "cinemas", collectionResourceRel = "cinemas")
public interface CinemaBuildingRepository extends CrudRepository<CinemaBuilding, UUID> {
}
