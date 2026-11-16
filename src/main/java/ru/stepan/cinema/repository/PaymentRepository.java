package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.Payment;

import java.util.UUID;
@RepositoryRestResource(path = "payments", collectionResourceRel = "payments")
public interface PaymentRepository extends CrudRepository<Payment, UUID> {
}
