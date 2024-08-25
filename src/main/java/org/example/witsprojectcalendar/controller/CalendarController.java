package org.example.witsprojectcalendar.controller;

import org.example.witsprojectcalendar.data.dto.ErrorResponseDTO;
import org.example.witsprojectcalendar.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Controller
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

}
