package ru.stepan.cinema.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.stepan.cinema.dto.FilmResponse;
import ru.stepan.cinema.dto.ScreeningResponse;
import ru.stepan.cinema.enums.AgeRating;
import ru.stepan.cinema.exception.ResourceNotFoundException;
import ru.stepan.cinema.repository.FilmRepository;
import ru.stepan.cinema.repository.ScreeningRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CinemaSearchController {
    private final FilmRepository filmRepository;
    private final ScreeningRepository screeningRepository;

    @GetMapping("/films/search")
    public List<FilmResponse> searchFilms (
            @RequestParam AgeRating ageRating,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
            ) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Дата from не может быть позже даты to");
        }
        return filmRepository.findByAgeRatingAndReleaseDateBetween(ageRating, from, to)
                .stream().map(FilmResponse::from).toList();
    }

    @GetMapping("/screenings/search")
    public List<ScreeningResponse> searchScreenings(
            @RequestParam String title,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from) {
        Instant start = from != null ? from : Instant.now();
        return screeningRepository.searchByFilmTitle(title, start)
                .stream().map(ScreeningResponse::from).toList();
    }

    @GetMapping("/films/{id}")
    public FilmResponse getFilm(@PathVariable UUID id) {
        return filmRepository.findById(id)
                .map(FilmResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм не найден: " + id));
    }
}
