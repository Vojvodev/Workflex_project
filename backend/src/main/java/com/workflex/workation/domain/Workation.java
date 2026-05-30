package com.workflex.workation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A workation: a period during which an employee works from a destination country
 * other than their origin country.
 *
 */
@Entity
@Table(name = "workations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workation {

    @Id
    @Column(name = "workation_id", nullable = false, updatable = false)
    private String workationId;

    @Column(nullable = false)
    private String employee;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(name = "start", nullable = false)
    private LocalDate start;

    // "end" is a reserved word in MySQL, so the column name must be quoted.
    @Column(name = "`end`", nullable = false)
    private LocalDate end;

    @Column(name = "working_days", nullable = false)
    private int workingDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Risk risk;
}
