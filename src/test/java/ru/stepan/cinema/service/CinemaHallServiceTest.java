package ru.stepan.cinema.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.stepan.cinema.AbstractIntegrationTest;
import ru.stepan.cinema.entity.CinemaBuilding;
import ru.stepan.cinema.repository.SeatRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

class CinemaHallServiceTest extends AbstractIntegrationTest {

    @Autowired
    private CinemaHallService service;

    @MockitoSpyBean
    private SeatRepository seatRepository;

    @Test
    void createHallWithSeats_savesHallAndAllSeats() {
        CinemaBuilding cinema = saveCinema();

        service.createHallWithSeats(cinema.getId(), "Hall 1", List.of(2, 3, 3));

        assertThat(hallRepository.count()).isEqualTo(1);
        assertThat(seatRepository.count()).isEqualTo(8);
    }

    @Test
    void createHallWithSeats_rollsBackHallWhenSeatsFail() {
        CinemaBuilding cinema = saveCinema();
        doThrow(new IllegalStateException("boom")).when(seatRepository).saveAll(any());

        assertThatThrownBy(() -> service.createHallWithSeats(cinema.getId(), "Hall 1", List.of(2, 3)))
                .isInstanceOf(IllegalStateException.class);

        assertThat(hallRepository.count()).isZero();
        assertThat(seatRepository.count()).isZero();
    }

    @Test
    void createHallWithSeats_rejectsEmptyRowAndSavesNothing() {
        CinemaBuilding cinema = saveCinema();

        assertThatThrownBy(() -> service.createHallWithSeats(cinema.getId(), "Hall 1", List.of(3, 0)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(hallRepository.count()).isZero();
    }
}
