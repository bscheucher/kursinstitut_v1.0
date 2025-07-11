// EnrollmentRequest.java
package com.bildungsinsitut.deutschkurse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentRequest {
    @NotNull(message = "Teilnehmer ID is required")
    @Positive(message = "Teilnehmer ID must be positive")
    private Integer teilnehmerId;

    @NotNull(message = "Kurs ID is required")
    @Positive(message = "Kurs ID must be positive")
    private Integer kursId;
}