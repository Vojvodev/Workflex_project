package com.workflex.workation.service;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import com.workflex.workation.domain.Risk;
import com.workflex.workation.domain.Workation;
import com.workflex.workation.repository.WorkationRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Imports workations from a CSV resource into the database.
 *
 * <p>Expected header: {@code workationId,employee,origin,destination,start,end,workingDays,risk}.
 * Import is idempotent because rows are keyed by {@code workationId}.
 */
@Service
public class CsvImportService {

    private static final Logger log = LoggerFactory.getLogger(CsvImportService.class);

    private final WorkationRepository repository;
    private final ResourceLoader resourceLoader;

    public CsvImportService(WorkationRepository repository, ResourceLoader resourceLoader) {
        this.repository = repository;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Imports the CSV located at the given (Spring) resource location, e.g.
     * {@code classpath:workations.csv}.
     *
     * @return the number of workations imported
     */
    @Transactional
    public int importFromLocation(String location) {
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            throw new CsvImportException("CSV resource not found: " + location);
        }
        try (InputStream in = resource.getInputStream()) {
            return importFromStream(in);
        } catch (IOException e) {
            throw new CsvImportException("Failed to read CSV resource: " + location, e);
        }
    }

    /** Parses the given stream and persists every row. */
    @Transactional
    public int importFromStream(InputStream in) {
        List<Workation> parsed = parse(in);
        repository.saveAll(parsed);
        log.info("Imported {} workation(s) from CSV", parsed.size());
        return parsed.size();
    }

    private List<Workation> parse(InputStream in) {
        List<Workation> result = new ArrayList<>();
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(reader)) {

            Map<String, String> row;
            int lineNumber = 1; // header consumed
            while ((row = csvReader.readMap()) != null) {
                lineNumber++;
                result.add(toWorkation(row, lineNumber));
            }
        } catch (IOException | CsvValidationException e) {
            throw new CsvImportException("Failed to parse CSV", e);
        }
        return result;
    }

    private Workation toWorkation(Map<String, String> row, int lineNumber) {
        try {
            return Workation.builder()
                    .workationId(required(row, "workationId", lineNumber))
                    .employee(required(row, "employee", lineNumber))
                    .origin(required(row, "origin", lineNumber))
                    .destination(required(row, "destination", lineNumber))
                    .start(parseDate(required(row, "start", lineNumber), lineNumber))
                    .end(parseDate(required(row, "end", lineNumber), lineNumber))
                    .workingDays(parseInt(required(row, "workingDays", lineNumber), lineNumber))
                    .risk(parseRisk(required(row, "risk", lineNumber), lineNumber))
                    .build();
        } catch (CsvImportException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new CsvImportException("Invalid CSV row at line " + lineNumber, e);
        }
    }

    private static String required(Map<String, String> row, String column, int lineNumber) {
        String value = row.get(column);
        if (value == null || value.isBlank()) {
            throw new CsvImportException(
                    "Missing required column '" + column + "' at line " + lineNumber);
        }
        return value.trim();
    }

    private static LocalDate parseDate(String value, int lineNumber) {
        try {
            return LocalDate.parse(value); // ISO-8601 (yyyy-MM-dd) as found in the CSV
        } catch (DateTimeParseException e) {
            throw new CsvImportException(
                    "Invalid date '" + value + "' at line " + lineNumber, e);
        }
    }

    private static int parseInt(String value, int lineNumber) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new CsvImportException(
                    "Invalid integer '" + value + "' at line " + lineNumber, e);
        }
    }

    private static Risk parseRisk(String value, int lineNumber) {
        try {
            return Risk.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CsvImportException(
                    "Unknown risk value '" + value + "' at line " + lineNumber, e);
        }
    }
}
