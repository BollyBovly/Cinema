package ru.stepan.cinema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.stepan.cinema.entity.Report;

@RepositoryRestResource(path = "reports", collectionResourceRel = "reports")
public interface ReportRepository extends CrudRepository<Report, Long> {
}
