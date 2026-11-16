package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.Ticket;

import java.util.UUID;

@RepositoryRestResource(path = "tickets", collectionResourceRel = "tickets")
public interface TicketRepository extends CrudRepository<Ticket, UUID> {

}
