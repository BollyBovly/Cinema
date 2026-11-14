package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.enums.AgeRating;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FilmRepository extends CrudRepository<Film, UUID>{
    List<Film> findByAgeRatingAndReleaseDateBetween(AgeRating ageRating, LocalDate from, LocalDate to);
}
