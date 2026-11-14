package ru.stepan.cinema.console;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.stepan.cinema.criteria.CinemaSearchCriteria;
import ru.stepan.cinema.criteria.CinemaSearchCriteriaImpl;
import ru.stepan.cinema.entity.*;
import ru.stepan.cinema.enums.AgeRating;
import ru.stepan.cinema.enums.CinemaStatus;
import ru.stepan.cinema.repository.CinemaBuildingRepository;
import ru.stepan.cinema.repository.CinemaHallRepository;
import ru.stepan.cinema.repository.FilmRepository;
import ru.stepan.cinema.repository.ScreenRepository;
import ru.stepan.cinema.service.UserService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {
    private final CinemaBuildingRepository cinemaRepository;
    private final CinemaHallRepository hallRepository;
    private final FilmRepository filmRepository;
    private final ScreenRepository screenRepository;
    private final CinemaSearchCriteria criteria;

    @Override
    public void run(String... args) {
        CinemaBuilding cinema = new CinemaBuilding();
        cinema.setName("Greenvich");
        cinema.setCity("Ekaterinburg");
        cinema.setAddress("Test Address");
        cinema.setStatus(CinemaStatus.ACTIVE);
        cinemaRepository.save(cinema);

        CinemaHall hall = new CinemaHall();
        hall.setCinemaBuilding(cinema);
        hall.setName("1");
        hallRepository.save(hall);

        Film film = new Film();
        film.setTitle("Odyssey");
        film.setDescription("lorem ipsum");
        film.setReleaseDate(LocalDate.of(2026, 6, 1));
        film.setDurationMin(120);
        film.setAgeRating(AgeRating.AGE_16);
        filmRepository.save(film);

        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        Screening screening = new Screening();
        screening.setCinemaHall(hall);
        screening.setFilm(film);
        screening.setStartsAt(start);
        screening.setEndsAt(start.plus(film.getDurationMin(), ChronoUnit.MINUTES));
        screening.setPrice(new BigDecimal("350.00"));
        screenRepository.save(screening);

        System.out.println("Criteria");
        filmRepository.findByAgeRatingAndReleaseDateBetween(
                        AgeRating.AGE_16, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
                .forEach(f -> System.out.println(f.getTitle()));

        System.out.println("--- Criteria");
        screenRepository.searchByFilmTitle("Odyssey", Instant.now())
                .forEach(s -> System.out.println(s.getFilm().getTitle() + " в " + s.getStartsAt()));

    }
}
