package ru.stepan.cinema.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {
    T save(T entity);

    void delete(ID id);

    Optional<T> update(T entity);

    List<T> findAll();

    Optional<T> findById(ID id);
}