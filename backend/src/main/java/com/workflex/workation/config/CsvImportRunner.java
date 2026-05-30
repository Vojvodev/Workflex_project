package com.workflex.workation.config;

import com.workflex.workation.repository.WorkationRepository;
import com.workflex.workation.service.CsvImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Imports the bundled CSV on application startup when
 * {@code workflex.csv.import-on-startup=true} (the default).
 *
 * <p>The import is skipped when the database already contains workations, so a
 * normal restart does not re-read the file. 
 * Use the {@code POST /workflex/workation/import} endpoint to force a re-import.
 */
@Component
@ConditionalOnProperty(name = "workflex.csv.import-on-startup", havingValue = "true", matchIfMissing = true)
public class CsvImportRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvImportRunner.class);

    private final CsvImportService csvImportService;
    private final WorkationRepository repository;
    private final String csvLocation;

    public CsvImportRunner(CsvImportService csvImportService,
                           WorkationRepository repository,
                           @Value("${workflex.csv.location}") String csvLocation) {
        this.csvImportService = csvImportService;
        this.repository = repository;
        this.csvLocation = csvLocation;
    }

    @Override
    public void run(ApplicationArguments args) {
        long existing = repository.count();
        if (existing > 0) {
            log.info("Skipping CSV import on startup: {} workation(s) already present", existing);
            return;
        }
        log.info("No workations found; importing from {}", csvLocation);
        csvImportService.importFromLocation(csvLocation);
    }
}
