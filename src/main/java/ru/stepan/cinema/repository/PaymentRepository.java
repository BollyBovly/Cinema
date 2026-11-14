package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import ru.stepan.cinema.entity.Payment;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {
}
