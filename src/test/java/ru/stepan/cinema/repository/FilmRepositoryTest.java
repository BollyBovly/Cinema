package ru.stepan.cinema.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.stepan.cinema.AbstractIntegrationTest;
import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.enums.AgeRating;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FilmRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private FilmRepository filmRepository;

    @Test
    void findByAgeRatingAndReleaseDateBetween_returnsOnlyMatchingFilms() {
        filmRepository.save(film("Match", AgeRating.AGE_16, LocalDate.of(2026, 5, 1)));
        filmRepository.save(film("Wrong age", AgeRating.AGE_18, LocalDate.of(2026, 5, 1)));
        filmRepository.save(film("Wrong date", AgeRating.AGE_16, LocalDate.of(2025, 5, 1)));

        List<Film> result = filmRepository.findByAgeRatingAndReleaseDateBetween(
                AgeRating.AGE_16, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertThat(result).extracting(Film::getTitle).containsExactly("Match");
    }

    private Film film(String title, AgeRating rating, LocalDate releaseDate) {
        Film film = new Film();
        film.setTitle(title);
        film.setDescription("Description");
        film.setDurationMin(120);
        film.setReleaseDate(releaseDate);
        film.setAgeRating(rating);
        return film;
    }
}