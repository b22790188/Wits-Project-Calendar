package org.example.witsprojectcalendar.controller;

import com.google.api.services.calendar.model.Event;
import lombok.extern.log4j.Log4j2;
import org.example.witsprojectcalendar.data.dto.ErrorResponseDTO;
import org.example.witsprojectcalendar.data.dto.InsertEventRequest;
import org.example.witsprojectcalendar.data.dto.UpdateEventRequest;
import org.example.witsprojectcalendar.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Controller
@Log4j2
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/events")
    public ResponseEntity<?> InsertEvents(@RequestBody InsertEventRequest request) throws GeneralSecurityException, IOException {
        log.info("Received event creation request: {}", request);
        Event isCreated = calendarService.insertEvent(request);
        return ResponseEntity.ok(isCreated);

    }

    @DeleteMapping("/events")
    public ResponseEntity<?> deleteEvents(@RequestParam String eventId) throws GeneralSecurityException, IOException {
        boolean isDeleted = calendarService.deleteEvent(eventId);
        return isDeleted ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().body(new ErrorResponseDTO("Delete event failed"));
    }

    @GetMapping("/events")
    public ResponseEntity<?> getEvents() throws IOException {
        return ResponseEntity.ok(calendarService.getEvents());
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") String eventId, @RequestBody UpdateEventRequest request) throws IOException {
        Event updateEvent = calendarService.updateEvent(eventId, request);
        return ResponseEntity.ok(updateEvent);
    }

}
