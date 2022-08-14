package ru.vk.competition.minbenchmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.vk.competition.minbenchmark.entity.Report;
import ru.vk.competition.minbenchmark.service.ReportService;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportService reportService;

    @GetMapping("/get-report-by-id/{id}")
    public Mono<ResponseEntity<Report>> getReportById(String id) {
        return reportService.getReportById(id);
    }

    @PostMapping("/create-report")
    public Mono<ResponseEntity<Void>> createReport (Report report) {
        return reportService.createReport(report);
    }


}
