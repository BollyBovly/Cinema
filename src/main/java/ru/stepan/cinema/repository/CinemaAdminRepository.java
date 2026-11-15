package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import ru.stepan.cinema.entity.CinemaAdmin;
import ru.stepan.cinema.entity.CinemaAdminId;

@RepositoryRestResource(path = "cinema-admins", collectionResourceRel = "cinema-admins")
public interface CinemaAdminRepository extends CrudRepository<CinemaAdmin, CinemaAdminId> {

}
