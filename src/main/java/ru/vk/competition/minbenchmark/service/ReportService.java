package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.Columns;
import ru.vk.competition.minbenchmark.entity.Report;
import ru.vk.competition.minbenchmark.repository.ReportRepository;
import ru.vk.competition.minbenchmark.repository.TableRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final TableRepository tableRepository;

    public Mono<ResponseEntity<Report>> getReportById(String id) {
        return Mono.fromCallable(() -> {
            try {
                if (!reportRepository.findByReportId(id).isPresent()) {
                    return new ResponseEntity<>((Report) null, HttpStatus.NOT_ACCEPTABLE);
                }

                Report report = reportRepository.findByReportId(id).get();
                return new ResponseEntity<>(report, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>((Report) null, HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> createReport(Report report) {
        return Mono.fromCallable(() -> {
            if (report.getReportId() == null || Integer.parseInt(report.getReportId()) <= 0) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (report.getTableAmount() == null || report.getTables() == null) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (reportRepository.findByReportId(report.getReportId()).isPresent()) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (Integer.parseInt(report.getTableAmount()) != tableRepository.count()) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (report.getTables() != null) {
                for (var table : report.getTables()) {
                    if (tableRepository.findByTableName(table.getTableName()).isEmpty()) {
                        return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                    }

                    var tab = tableRepository.findByTableName(table.getTableName()).get();
                    var colsInMem = tab.getColumnInfos();
                    var colsInRep = table.getColumnInfos();

                    Set<Columns> result = colsInMem.stream()
                            .distinct()
                            .filter(colsInRep::contains)
                            .collect(Collectors.toSet());

                    if (result.isEmpty()) return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                try {
                    reportRepository.save(report);
                    return new ResponseEntity<Void>(HttpStatus.CREATED);
                } catch (Exception e) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }
            }
            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
        }).publishOn(Schedulers.boundedElastic());
    }


}
