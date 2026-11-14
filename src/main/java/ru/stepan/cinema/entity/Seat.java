package ru.stepan.cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "seat",
        uniqueConstraints = @UniqueConstraint(
            name = "uk_seat",
            columnNames = { "cinema_hall_id", "row_num", "seat_num"}
        )
)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_hall_id", nullable = false)
    private CinemaHall cinemaHall;

    @Column(name = "row_num", nullable = false)
    private int rowNum;

    @Column(name = "seat_num", nullable = false)
    private int seatNum;
}
