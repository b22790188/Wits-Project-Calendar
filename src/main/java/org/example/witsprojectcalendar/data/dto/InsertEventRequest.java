package org.example.witsprojectcalendar.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InsertEventRequest {
    private String newSummary;
    private String newDescription;
    private String newStart;
    private String newEnd;
    private boolean isAllDay;
}
