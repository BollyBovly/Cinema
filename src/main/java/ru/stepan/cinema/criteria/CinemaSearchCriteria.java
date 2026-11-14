package ru.stepan.cinema.criteria;

import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.entity.Screening;
import ru.stepan.cinema.enums.AgeRating;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface CinemaSearchCriteria {
    List<Film> findFilms(AgeRating ageRating, LocalDate from, LocalDate to);
    List<Screening> findScreeningByFilmTitle(String title, Instant from);
}
