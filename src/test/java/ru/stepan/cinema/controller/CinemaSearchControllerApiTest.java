package ru.stepan.cinema.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.stepan.cinema.AbstractIntegrationTest;
import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.entity.User;
import ru.stepan.cinema.enums.AgeRating;
import ru.stepan.cinema.enums.UserRole;
import ru.stepan.cinema.repository.UserRepository;
import ru.stepan.cinema.service.UserService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CinemaSearchControllerApiTest extends AbstractIntegrationTest {

    private static final String PASSWORD = "secret123";
    private static final String USER_EMAIL = "api.user@example.com";
    private static final String ADMIN_EMAIL = "api.admin@example.com";

    @LocalServerPort
    private int port;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        userService.createUser("Api", "User", USER_EMAIL, "City", PASSWORD);
        userRepository.save(new User(null, "Api", "Admin", ADMIN_EMAIL, "City",
                UserRole.ADMIN, passwordEncoder.encode(PASSWORD)));
    }

    private RequestSpecification as(String email) {
        return given().auth().preemptive().basic(email, PASSWORD).accept(ContentType.JSON);
    }


    @Test
    void searchFilms_returnsOnlyMatchingFilms() {
        filmRepository.save(newFilm("Match", AgeRating.AGE_16, LocalDate.of(2026, 5, 1)));
        filmRepository.save(newFilm("Wrong age", AgeRating.AGE_18, LocalDate.of(2026, 5, 1)));

        as(USER_EMAIL)
                .queryParam("ageRating", "AGE_16")
                .queryParam("from", "2026-01-01")
                .queryParam("to", "2026-12-31")
                .when().get("/api/films/search")
                .then().statusCode(200)
                .body("size()", is(1))
                .body("[0].title", equalTo("Match"));
    }

    @Test
    void searchFilms_boundaryDatesAreInclusive() {
        filmRepository.save(newFilm("On start", AgeRating.AGE_16, LocalDate.of(2026, 1, 1)));
        filmRepository.save(newFilm("On end", AgeRating.AGE_16, LocalDate.of(2026, 12, 31)));
        filmRepository.save(newFilm("Day before", AgeRating.AGE_16, LocalDate.of(2025, 12, 31)));

        as(USER_EMAIL)
                .queryParam("ageRating", "AGE_16")
                .queryParam("from", "2026-01-01")
                .queryParam("to", "2026-12-31")
                .when().get("/api/films/search")
                .then().statusCode(200)
                .body("title", containsInAnyOrder("On start", "On end"));
    }

    @Test
    void searchFilms_noMatches_returnsEmptyList() {
        as(USER_EMAIL)
                .queryParam("ageRating", "AGE_16")
                .queryParam("from", "2026-01-01")
                .queryParam("to", "2026-12-31")
                .when().get("/api/films/search")
                .then().statusCode(200).body("size()", is(0));
    }

    @Test
    void searchFilms_fromAfterTo_returns400() {
        as(USER_EMAIL)
                .queryParam("ageRating", "AGE_16")
                .queryParam("from", "2026-12-31")
                .queryParam("to", "2026-01-01")
                .when().get("/api/films/search")
                .then().statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void searchFilms_missingParameter_returns400() {
        as(USER_EMAIL)
                .queryParam("from", "2026-01-01")
                .queryParam("to", "2026-12-31")
                .when().get("/api/films/search")
                .then().statusCode(400);
    }

    @Test
    void searchFilms_unknownAgeRating_returns400() {
        as(USER_EMAIL)
                .queryParam("ageRating", "ABC")
                .queryParam("from", "2026-01-01")
                .queryParam("to", "2026-12-31")
                .when().get("/api/films/search")
                .then().statusCode(400);
    }

    @Test
    void searchFilms_malformedDate_returns400() {
        as(USER_EMAIL)
                .queryParam("ageRating", "AGE_16")
                .queryParam("from", "01.01.2026")
                .queryParam("to", "2026-12-31")
                .when().get("/api/films/search")
                .then().statusCode(400);
    }


    @Test
    void getFilm_existing_returns200() {
        Film film = filmRepository.save(newFilm("Odyssey", AgeRating.AGE_16, LocalDate.of(2026, 5, 1)));

        as(USER_EMAIL)
                .when().get("/api/films/{id}", film.getId())
                .then().statusCode(200)
                .body("title", equalTo("Odyssey"));
    }

    @Test
    void getFilm_unknownId_returns404() {
        as(USER_EMAIL)
                .when().get("/api/films/{id}", UUID.randomUUID())
                .then().statusCode(404);
    }

    @Test
    void getFilm_malformedId_returns400() {
        as(USER_EMAIL)
                .when().get("/api/films/{id}", "not-a-uuid")
                .then().statusCode(400);
    }


    @Test
    void searchScreenings_withoutFrom_returnsOnlyFutureScreenings() {
        saveScreening("Odyssey", Instant.now().plus(1, ChronoUnit.DAYS));
        saveScreening("Odyssey", Instant.now().minus(1, ChronoUnit.DAYS));

        as(USER_EMAIL)
                .queryParam("title", "Odyssey")
                .when().get("/api/screenings/search")
                .then().statusCode(200)
                .body("size()", is(1))
                .body("[0].film.title", equalTo("Odyssey"));
    }

    @Test
    void searchScreenings_missingTitle_returns400() {
        as(USER_EMAIL)
                .when().get("/api/screenings/search")
                .then().statusCode(400);
    }


    @Test
    void anyEndpoint_withoutCredentials_isRejected() {
        given().accept(ContentType.JSON).redirects().follow(false)
                .when().get("/api/films/{id}", UUID.randomUUID())
                .then().statusCode(401);
    }

    @Test
    void anyEndpoint_withWrongPassword_returns401() {
        given().auth().preemptive().basic(USER_EMAIL, "wrong").accept(ContentType.JSON)
                .when().get("/api/films/{id}", UUID.randomUUID())
                .then().statusCode(401);
    }

    @Test
    void postAsRegularUser_returns403() {
        as(USER_EMAIL)
                .when().post("/api/films/search")
                .then().statusCode(403);
    }

    @Test
    void postAsAdmin_toGetOnlyEndpoint_returns405() {
        as(ADMIN_EMAIL)
                .when().post("/api/films/search")
                .then().statusCode(405);
    }
}
