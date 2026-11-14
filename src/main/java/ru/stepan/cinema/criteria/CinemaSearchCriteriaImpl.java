package ru.stepan.cinema.criteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.stepan.cinema.entity.CinemaBuilding;
import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.entity.Screening;
import ru.stepan.cinema.enums.AgeRating;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CinemaSearchCriteriaImpl implements CinemaSearchCriteria {
    private final EntityManager em;

    @Override
    public List<Film> findFilms(AgeRating ageRating, LocalDate from, LocalDate to) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Film> cq = cb.createQuery(Film.class);
        Root<Film> film = cq.from(Film.class);

        cq.select(film).where(
                cb.equal(film.get("ageRating"), ageRating),
                cb.between(film.<LocalDate>get("releaseDate"), from, to)
        );
        return em.createQuery(cq).getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Screening> findScreeningByFilmTitle(String title, Instant from) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Screening> cq = cb.createQuery(Screening.class);
        Root<Screening> screening = cq.from(Screening.class);

        Fetch<Screening, Film> filmFetch = screening.fetch("film");
        Join<Screening, Film> film = (Join<Screening, Film>) filmFetch;

        cq.select(screening).where(
                cb.equal(film.get("title"), title),
                cb.greaterThanOrEqualTo(screening.<Instant>get("startsAt"), from)
        );
        return em.createQuery(cq).getResultList();

    }
}
