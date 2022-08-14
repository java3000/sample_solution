package ru.vk.competition.minbenchmark.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vk.competition.minbenchmark.entity.Report;

import java.util.Optional;

@Repository
public interface ReportRepository  extends CrudRepository<Report, String> {

    Optional<Report> findByReportId(String id);
}
