package ru.stepan.cinema.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.Screening;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RepositoryRestResource(path = "screenings", collectionResourceRel = "screenings")
public interface ScreeningRepository extends CrudRepository<Screening, UUID> {
    @Query("select s from Screening s join fetch s.film f where f.title = :title and s.startsAt >= :from")
    List<Screening> searchByFilmTitle(@Param("title") String title, @Param("from")Instant from);
}
