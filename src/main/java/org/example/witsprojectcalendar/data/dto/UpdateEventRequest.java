package org.example.witsprojectcalendar.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateEventRequest {
    private String newSummary;
    private String newStart;
    private String newEnd;
}
