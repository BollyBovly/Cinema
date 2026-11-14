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
@Table(name = "cinema_hall",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cinema_hall",
                columnNames = {"cinema_id", "name"}
        ))
public class CinemaHall {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private CinemaBuilding cinemaBuilding;

    @Column(name = "name", nullable = false)
    private String name;
}
