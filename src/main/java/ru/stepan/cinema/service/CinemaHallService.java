package ru.stepan.cinema.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.stepan.cinema.entity.CinemaBuilding;
import ru.stepan.cinema.entity.CinemaHall;
import ru.stepan.cinema.entity.Seat;
import ru.stepan.cinema.repository.CinemaBuildingRepository;
import ru.stepan.cinema.repository.CinemaHallRepository;
import ru.stepan.cinema.repository.SeatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaHallService {
    private static final int MAX_ROWS = 30;
    private static final int MAX_SEATS_PER_ROW = 30;

    private final CinemaBuildingRepository cinemaBuildingRepository;
    private final SeatRepository seatRepository;
    private final CinemaHallRepository hallRepository;

    @Transactional
    public CinemaHall createHallWithSeats(UUID cinemaID, String name, List<Integer> seatsPerRows) {
        validate(name, seatsPerRows);

        CinemaBuilding cinema = cinemaBuildingRepository.findById(cinemaID)
                .orElseThrow(() -> new IllegalArgumentException("Кинотеар не найден"));

        String cleanName = name.trim();
        if (hallRepository.existsByCinemaBuildingIdAndName(cinemaID, cleanName)) {
            throw new IllegalArgumentException("Зал с таким навзанием уже есть в этом кинотеатре");
        }

        CinemaHall hall = new CinemaHall();
        hall.setCinemaBuilding(cinema);
        hall.setName(cleanName);
        hallRepository.save(hall);

        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= seatsPerRows.size(); row++) {
            for (int seatNum = 1; seatNum <= seatsPerRows.get(row - 1); seatNum++) {
                Seat seat = new Seat();
                seat.setCinemaHall(hall);
                seat.setRowNum(row);
                seat.setSeatNum(seatNum);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
        return hall;

    }

    private void validate(String name, List<Integer> seatsPerRows) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название зала не может бытьпустым");
        }
        if (seatsPerRows == null || seatsPerRows.isEmpty() || seatsPerRows.size() > MAX_ROWS) {
            throw new IllegalArgumentException("рядов должно быть от 1 до " + MAX_ROWS);
        }
        for (Integer seatPerRow : seatsPerRows) {
            if (seatPerRow == null || seatPerRow < 1 || seatPerRow > MAX_SEATS_PER_ROW) {
                throw new IllegalArgumentException("Мест в ряду должно быть от 1 до " + MAX_SEATS_PER_ROW);
            }
        }
    }
}
