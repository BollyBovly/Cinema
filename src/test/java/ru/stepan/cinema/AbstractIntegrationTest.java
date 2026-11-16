package ru.stepan.cinema;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.stepan.cinema.entity.CinemaBuilding;
import ru.stepan.cinema.entity.CinemaHall;
import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.entity.Screening;
import ru.stepan.cinema.enums.AgeRating;
import ru.stepan.cinema.enums.CinemaStatus;
import ru.stepan.cinema.repository.CinemaBuildingRepository;
import ru.stepan.cinema.repository.CinemaHallRepository;
import ru.stepan.cinema.repository.FilmRepository;
import ru.stepan.cinema.repository.ScreeningRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    static {
        postgres.start();
    }

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    protected CinemaBuildingRepository cinemaRepository;
    @Autowired
    protected CinemaHallRepository hallRepository;
    @Autowired
    protected FilmRepository filmRepository;
    @Autowired
    protected ScreeningRepository screeningRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbc.execute("TRUNCATE TABLE users, cinema_building, cinema_admin, cinema_hall, seat, "
                + "film, screening, payment, ticket, review CASCADE");
    }

    protected CinemaBuilding saveCinema() {
        CinemaBuilding cinema = new CinemaBuilding();
        cinema.setName("Cinema");
        cinema.setCity("City");
        cinema.setAddress("Address");
        cinema.setStatus(CinemaStatus.ACTIVE);
        return cinemaRepository.save(cinema);
    }

    protected Film newFilm(String title, AgeRating rating, LocalDate releaseDate) {
        Film film = new Film();
        film.setTitle(title);
        film.setDescription("Description");
        film.setDurationMin(120);
        film.setReleaseDate(releaseDate);
        film.setAgeRating(rating);
        return film;
    }

    protected Screening saveScreening(String filmTitle, Instant startsAt) {
        CinemaHall hall = new CinemaHall();
        hall.setCinemaBuilding(saveCinema());
        hall.setName("Hall");
        hallRepository.save(hall);

        Film film = filmRepository.save(newFilm(filmTitle, AgeRating.AGE_16, LocalDate.of(2026, 5, 1)));

        Screening screening = new Screening();
        screening.setCinemaHall(hall);
        screening.setFilm(film);
        screening.setStartsAt(startsAt);
        screening.setEndsAt(startsAt.plus(film.getDurationMin(), ChronoUnit.MINUTES));
        screening.setPrice(new BigDecimal("350.00"));
        return screeningRepository.save(screening);
    }
}