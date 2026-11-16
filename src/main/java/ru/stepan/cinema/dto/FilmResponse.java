package ru.stepan.cinema.dto;

import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.enums.AgeRating;

import java.time.LocalDate;
import java.util.UUID;

public record FilmResponse(UUID id, String title, String description, LocalDate releaseDate, int durationMin, AgeRating ageRating, String coverKey) {
    public static FilmResponse from(Film film) {
        return new FilmResponse(film.getId(), film.getTitle(), film.getDescription(), film.getReleaseDate(), film.getDurationMin(), film.getAgeRating(), film.getCoverKey());
    }

}
