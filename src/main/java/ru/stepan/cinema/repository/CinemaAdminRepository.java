package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.CinemaAdmin;
import ru.stepan.cinema.entity.CinemaAdminId;

public interface CinemaAdminRepository extends CrudRepository<CinemaAdmin, CinemaAdminId> {

}
