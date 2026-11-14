package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.Ticket;

import java.util.UUID;

public interface TicketRepository extends CrudRepository<Ticket, UUID> {
}
