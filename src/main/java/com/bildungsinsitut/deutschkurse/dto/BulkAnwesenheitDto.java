package com.bildungsinsitut.deutschkurse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BulkAnwesenheitDto {

    @NotNull(message = "Kurs ID is required")
    private Integer kursId;

    @NotNull(message = "Datum is required")
    private LocalDate datum;

    @NotNull(message = "Attendance list is required")
    @Size(min = 1, message = "At least one attendance record is required")
    private List<AttendanceRecord> attendanceRecords;

    @Getter
    @Setter
    public static class AttendanceRecord {
        @NotNull(message = "Teilnehmer ID is required")
        private Integer teilnehmerId;

        private Boolean anwesend = true;

        private Boolean entschuldigt = false;

        private String bemerkung;
    }
}