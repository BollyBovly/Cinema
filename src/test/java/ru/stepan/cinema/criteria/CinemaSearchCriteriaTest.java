package ru.stepan.cinema.criteria;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.stepan.cinema.AbstractIntegrationTest;
import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.entity.Screening;
import ru.stepan.cinema.enums.AgeRating;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

class CinemaSearchCriteriaTest extends AbstractIntegrationTest {

    @Autowired
    private CinemaSearchCriteria criteria;

    @Test
    void findFilms_returnsOnlyMatchingFilms() {
        filmRepository.save(newFilm("Match", AgeRating.AGE_16, LocalDate.of(2026, 5, 1)));
        filmRepository.save(newFilm("Wrong age", AgeRating.AGE_18, LocalDate.of(2026, 5, 1)));
        filmRepository.save(newFilm("Wrong date", AgeRating.AGE_16, LocalDate.of(2025, 5, 1)));

        List<Film> result = criteria.findFilms(
                AgeRating.AGE_16, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertThat(result).extracting(Film::getTitle).containsExactly("Match");
    }

    @Test
    void findScreeningsByFilmTitle_returnsOnlyFutureScreeningsOfThatFilm() {
        Instant now = Instant.now();
        saveScreening("Odyssey", now.plus(1, ChronoUnit.DAYS));
        saveScreening("Odyssey", now.minus(1, ChronoUnit.DAYS));
        saveScreening("Other", now.plus(1, ChronoUnit.DAYS));

        List<Screening> result = criteria.findScreeningByFilmTitle("Odyssey", now);

        assertThat(result).hasSize(1);
        assertThat(Hibernate.isInitialized(result.get(0).getFilm())).isTrue();
    }
}
