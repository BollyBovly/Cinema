package ru.stepan.cinema.dto;

import ru.stepan.cinema.entity.Screening;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ScreeningResponse(UUID id, FilmResponse film, Instant startsAt, Instant endsAt, BigDecimal price) {
    public static ScreeningResponse from(Screening screening) {
        return new ScreeningResponse(screening.getId(), FilmResponse.from(screening.getFilm()), screening.getStartsAt(), screening.getEndsAt(), screening.getPrice());
    }
}
