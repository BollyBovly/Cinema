package ru.stepan.cinema.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import ru.stepan.cinema.AbstractIntegrationTest;
import ru.stepan.cinema.entity.Screening;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

class ScreeningRepositoryTest extends AbstractIntegrationTest {

    @Test
    void searchByFilmTitle_returnsOnlyFutureScreeningsOfThatFilm() {
        Instant now = Instant.now();
        saveScreening("Odyssey", now.plus(1, ChronoUnit.DAYS));
        saveScreening("Odyssey", now.minus(1, ChronoUnit.DAYS));
        saveScreening("Other", now.plus(1, ChronoUnit.DAYS));

        List<Screening> result = screeningRepository.searchByFilmTitle("Odyssey", now);

        assertThat(result).hasSize(1);
        assertThat(Hibernate.isInitialized(result.get(0).getFilm())).isTrue();
        assertThat(result.get(0).getFilm().getTitle()).isEqualTo("Odyssey");
    }
}