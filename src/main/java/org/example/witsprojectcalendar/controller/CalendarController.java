package org.example.witsprojectcalendar.controller;

import com.google.api.services.calendar.model.Event;
import lombok.extern.log4j.Log4j2;
import org.example.witsprojectcalendar.data.dto.ErrorResponseDTO;
import org.example.witsprojectcalendar.data.dto.UpdateEventRequest;
import org.example.witsprojectcalendar.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<?> quickAddEvents(@RequestParam String eventTitle) throws GeneralSecurityException, IOException {
        boolean isCreated = calendarService.quickAddEvent(eventTitle);
        return isCreated ? ResponseEntity.ok("Create event success")
            : ResponseEntity.badRequest().body(new ErrorResponseDTO("Create event failed"));
    }

    @DeleteMapping("/events")
    public ResponseEntity<?> deleteEvents(@RequestParam String eventId) throws GeneralSecurityException, IOException {
        boolean isDeleted = calendarService.deleteEvent(eventId);
        return isDeleted ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().body(new ErrorResponseDTO("Delete event failed"));
    }

    @GetMapping("/events")
    public ResponseEntity<?> getEvents() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = authentication.getCredentials().toString();

        log.info(accessToken);

        return ResponseEntity.ok(calendarService.getEvents());
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") String eventId, @RequestBody UpdateEventRequest request) throws IOException {
        Event updateEvent = calendarService.updateEvent(eventId, request);
        return ResponseEntity.ok(updateEvent);
    }

    @PostMapping("/testLogin")
    public ResponseEntity<?> testAccessToken(@RequestParam String accessToken) throws IOException, GeneralSecurityException {
        return ResponseEntity.ok(calendarService.testCalendarService(accessToken));
    }
}
