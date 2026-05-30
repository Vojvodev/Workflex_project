package com.workflex.workation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.workflex.workation.domain.Risk;
import com.workflex.workation.domain.Workation;
import com.workflex.workation.repository.WorkationRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CsvImportServiceTest {

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private WorkationRepository repository;

    @Test
    void importsAllRowsFromBundledCsv() {
        int imported = csvImportService.importFromLocation("classpath:workations.csv");

        assertThat(imported).isEqualTo(5);
        assertThat(repository.count()).isEqualTo(5);

        Workation w1 = repository.findById("w1").orElseThrow();
        assertThat(w1.getEmployee()).isEqualTo("Steffen Jacobs");
        assertThat(w1.getOrigin()).isEqualTo("Germany");
        assertThat(w1.getDestination()).isEqualTo("United States");
        assertThat(w1.getStart()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(w1.getEnd()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(w1.getWorkingDays()).isEqualTo(65);
        assertThat(w1.getRisk()).isEqualTo(Risk.HIGH);
    }

    @Test
    void mapsAllRiskLevels() {
        csvImportService.importFromLocation("classpath:workations.csv");

        assertThat(repository.findById("w4").orElseThrow().getRisk()).isEqualTo(Risk.LOW);
        assertThat(repository.findById("w5").orElseThrow().getRisk()).isEqualTo(Risk.NO);
    }

    @Test
    void reimportIsIdempotent() {
        csvImportService.importFromLocation("classpath:workations.csv");
        csvImportService.importFromLocation("classpath:workations.csv");

        assertThat(repository.count()).isEqualTo(5);
    }

    @Test
    void failsClearlyForUnknownResource() {
        assertThatThrownBy(() -> csvImportService.importFromLocation("classpath:missing.csv"))
                .isInstanceOf(CsvImportException.class)
                .hasMessageContaining("not found");
    }
}
