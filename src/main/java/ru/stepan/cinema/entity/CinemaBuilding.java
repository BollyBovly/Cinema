package ru.stepan.cinema.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.stepan.cinema.enums.CinemaStatus;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cinema_building")
public class CinemaBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CinemaStatus status;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "name", nullable = false)
    private String name;
}
