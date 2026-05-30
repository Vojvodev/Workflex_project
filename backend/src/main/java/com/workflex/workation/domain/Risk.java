package com.workflex.workation.domain;

/**
 * Risk level of a workation as stored in the CSV / database.
 *
 * <p>Note: both {@link #LOW} and {@link #NO} are presented as "No risk" in the UI,
 * but with different colours (LOW = yellow, NO = green), while {@link #HIGH} is the
 * red "High risk" state. The visual mapping is intentionally kept in the frontend.
 */
public enum Risk {
    HIGH,
    LOW,
    NO
}
