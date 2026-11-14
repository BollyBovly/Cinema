package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.CinemaBuilding;

import java.util.UUID;

public interface CinemaBuildingRepository extends CrudRepository<CinemaBuilding, UUID> {
}
