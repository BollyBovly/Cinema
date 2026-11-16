package ru.stepan.cinema.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepan.cinema.dto.ReportCreatedResponse;
import ru.stepan.cinema.entity.Report;
import ru.stepan.cinema.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private static final MediaType HTML = MediaType.parseMediaType("text/html;charset=UTF-8");
    private static final MediaType TEXT = MediaType.parseMediaType("text/plain;charset=UTF-8");

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportCreatedResponse> create() {
        Long id = reportService.createReport();
        reportService.generateReportAsync(id);
        return ResponseEntity.accepted().body(new ReportCreatedResponse(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable Long id) {
        Report report = reportService.getReport(id);
        return switch (report.getStatus()) {
            case COMPLETED -> ResponseEntity.ok().contentType(HTML).body(report.getContent());
            case CREATED -> ResponseEntity.accepted().contentType(TEXT).body("Отчет еще формируется");
            case ERROR -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(TEXT)
                    .body("Не удалось сформировать отчет");
        };
    }
}