package com.workflex.workation.controller;

import com.workflex.workation.dto.WorkationResponse;
import com.workflex.workation.service.CsvImportService;
import com.workflex.workation.service.WorkationService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflex/workation")
public class WorkationController {

    private final WorkationService workationService;
    private final CsvImportService csvImportService;
    private final String csvLocation;

    public WorkationController(WorkationService workationService,
                               CsvImportService csvImportService,
                               @Value("${workflex.csv.location}") String csvLocation) {
        this.workationService = workationService;
        this.csvImportService = csvImportService;
        this.csvLocation = csvLocation;
    }

    /** Lists all workations currently in the system. */
    @GetMapping
    public List<WorkationResponse> list() {
        return workationService.findAll();
    }

    /**
     * Imports workations from the configured CSV file ({@code workflex.csv.location}).
     * Idempotent: rows are keyed by workationId, so re-importing updates instead of duplicating.
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importCsv() {
        int imported = csvImportService.importFromLocation(csvLocation);
        return ResponseEntity.ok(Map.of(
                "imported", imported,
                "source", csvLocation
        ));
    }
}
