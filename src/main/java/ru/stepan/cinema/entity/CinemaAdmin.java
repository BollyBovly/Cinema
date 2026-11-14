package ru.stepan.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cinema_admin")
public class CinemaAdmin {
    @EmbeddedId
    private CinemaAdminId id = new CinemaAdminId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cinemaId")
    @JoinColumn(name = "cinema_id")
    private CinemaBuilding cinemaBuilding;
}
