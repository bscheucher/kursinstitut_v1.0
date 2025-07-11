// StatusUpdateRequest.java
package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.TeilnehmerKursStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private TeilnehmerKursStatus status;

    private String bemerkungen;
}