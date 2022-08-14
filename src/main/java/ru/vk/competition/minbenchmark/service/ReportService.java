package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.Report;
import ru.vk.competition.minbenchmark.repository.ReportRepository;
import ru.vk.competition.minbenchmark.repository.TableRepository;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ReportService {

    Logger logger = Logger.getLogger("table");

    private final ReportRepository reportRepository;
    private final TableRepository tableRepository;

    public Mono<ResponseEntity<Report>> getReportById(String id) {
        logger.info("getReportById id: " + id);
        return Mono.fromCallable(() -> {
            try {
                if (!reportRepository.findByReportId(id).isPresent()) {
                    logger.info("getReportById no report with id: " + id);
                    return new ResponseEntity<>((Report) null, HttpStatus.NOT_ACCEPTABLE);
                }

                Report report = reportRepository.findByReportId(id).get();
                logger.info("getReportById report present - give it " + id);
                return new ResponseEntity<>(report, HttpStatus.CREATED);
            } catch (Exception e) {
                logger.info("getReportById exception: " + e.getMessage());
                return new ResponseEntity<>((Report) null, HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> createReport(Report report) {
        logger.info(String.format("createReport NEW: %n id: %s%n table amount: %s%n tables: %s%n",
                report.getReportId(),
                report.getTableAmount(),
                report.getTables()));

        return Mono.fromCallable(() -> {
            if (report.getReportId() == null) {
                logger.info("createReport id is NULL " + report.getReportId());
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (Integer.parseInt(report.getReportId()) <= 0) {
                logger.info("createReport incorrect id: " + report.getReportId());
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (report.getTableAmount() == null || report.getTables() == null) {
                logger.info("createReport table amount or tables are NULL: " + report.getReportId());
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (reportRepository.findByReportId(report.getReportId()).isPresent()) {
                logger.info("createReport report already present: " + report.getReportId());
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (Integer.parseInt(report.getTableAmount()) != tableRepository.count()) {
                logger.info(String.format("createReport table amount not equal tables count: amount: %s%n count %s%n",
                        report.getTableAmount(), tableRepository.count()));
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else {
                try {
                    reportRepository.save(report);
                    return new ResponseEntity<Void>(HttpStatus.CREATED);
                } catch (Exception e) {
                    logger.info("createReport exception: " + e.getMessage());
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }
            }
        }).publishOn(Schedulers.boundedElastic());
    }


}
