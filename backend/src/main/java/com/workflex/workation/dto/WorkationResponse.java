package com.workflex.workation.dto;

import com.workflex.workation.domain.Risk;
import com.workflex.workation.domain.Workation;
import java.time.LocalDate;

/**
 * API representation of a {@link Workation}. Dates are serialised as (yyyy-MM-dd); 
 * the frontend is responsible for displaying them as dd/MM/yyyy.
 */
public record WorkationResponse(
        String workationId,
        String employee,
        String origin,
        String destination,
        LocalDate start,
        LocalDate end,
        int workingDays,
        Risk risk
) {
    public static WorkationResponse from(Workation w) {
        return new WorkationResponse(
                w.getWorkationId(),
                w.getEmployee(),
                w.getOrigin(),
                w.getDestination(),
                w.getStart(),
                w.getEnd(),
                w.getWorkingDays(),
                w.getRisk()
        );
    }
}
