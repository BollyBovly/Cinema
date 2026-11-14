package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.Seat;

import java.util.UUID;

public interface SeatRepository extends CrudRepository<Seat, UUID> {
}
