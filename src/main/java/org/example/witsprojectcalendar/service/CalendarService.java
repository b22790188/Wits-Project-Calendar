package org.example.witsprojectcalendar.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.log4j.Log4j2;
import org.example.witsprojectcalendar.data.dto.InsertEventRequest;
import org.example.witsprojectcalendar.data.dto.UpdateEventRequest;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class CalendarService {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
        Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";


    private final Cache<String, Calendar> calendarCache;

//    private final Calendar service;

    public CalendarService() {
        this.calendarCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(55, TimeUnit.MINUTES)
            .build();

    }

    private Calendar getCalendarService(String accessToken) throws IOException {
        try {
            return calendarCache.get(accessToken, () -> createCalendarService(accessToken));
        } catch (ExecutionException e) {
            throw new IOException("Failed to create Calendar service", e);
        }
    }

    private Calendar createCalendarService(String accessToken) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
            .setAccessToken(accessToken);

        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME).build();
    }

//    public CalendarService() throws IOException, GeneralSecurityException {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        this.service =
//            new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }

    public boolean quickAddEvent(String accessToken, String title) throws IOException {
        Calendar service = getCalendarService(accessToken);
        Event quickAdd = service.events().quickAdd("primary", title).execute();
        log.info("quickAdd :{} ", quickAdd.toString());
        return true;
    }

    public boolean deleteEvent(String accessToken, String id) throws IOException {
        Calendar service = getCalendarService(accessToken);

        service.events().delete("primary", id).execute();
        return true;
    }

    public Event insertEvent(String accessToken, InsertEventRequest request) throws IOException {
        Calendar service = getCalendarService(accessToken);
        Event event = new Event()
            .setSummary(request.getNewSummary())
            .setDescription(request.getNewDescription());

        if (request.isAllDay()) {
            LocalDate startDate = parseDate(request.getNewStart());
            LocalDate endDate = parseDate(request.getNewEnd());

            event.setStart(new EventDateTime().setDate(new DateTime(startDate.toString())))
                .setEnd(new EventDateTime().setDate(new DateTime(endDate.toString())));
        } else {
            Instant startInstant = parseDateTime(request.getNewStart());
            Instant endInstant = parseDateTime(request.getNewEnd());

            event.setStart(new EventDateTime().setDateTime(new DateTime(startInstant.toEpochMilli())))
                .setEnd(new EventDateTime().setDateTime(new DateTime(endInstant.toEpochMilli())));
        }

        Event createdEvent = service.events().insert("primary", event).execute();
        log.info("Event created: {} ({})", createdEvent.getSummary(), createdEvent.getId());
        return createdEvent;
    }


    public List<Event> getEvents(String accessToken, String startDate, String endDate) throws IOException {
        Calendar service = getCalendarService(accessToken);

        DateTime minTime = convertUtcToTimezone(startDate, "Asia/Taipei");
        DateTime maxTime = convertUtcToTimezone(endDate, "Asia/Taipei");

        Events events = service.events().list("primary")
            .setTimeMin(minTime)
            .setTimeMax(maxTime)
            .setOrderBy("startTime").setSingleEvents(true).execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
            }
        }
        return items;
    }

    public Event updateEvent(String accessToken, String eventId, UpdateEventRequest request) throws IOException {
        Calendar service = getCalendarService(accessToken);
        log.info("Attempting to update event with ID: {}", eventId);


        Event event = new Event()
            .setSummary(request.getNewSummary())
            .setDescription(request.getNewDescription());

        if (request.isAllDay()) {
            LocalDate startDate = parseDate(request.getNewStart());
            LocalDate endDate = parseDate(request.getNewEnd());

            event.setStart(new EventDateTime().setDate(new DateTime(startDate.toString())))
                .setEnd(new EventDateTime().setDate(new DateTime(endDate.toString())));
        } else {
            Instant startInstant = parseDateTime(request.getNewStart());
            Instant endInstant = parseDateTime(request.getNewEnd());

            event.setStart(new EventDateTime().setDateTime(new DateTime(startInstant.toEpochMilli())))
                .setEnd(new EventDateTime().setDateTime(new DateTime(endInstant.toEpochMilli())));
        }

        Event updatedEvent = service.events().update("primary", eventId, event).execute();
        log.info("Event updated successfully: {} ({})", updatedEvent.getSummary(), updatedEvent.getUpdated());
        return updatedEvent;
    }


    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
        throws IOException {
        // Load client secrets.
        InputStream in = CalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            return Instant.parse(dateString).atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    private Instant parseDateTime(String dateTimeString) {
        return Instant.parse(dateTimeString);
    }

    private DateTime convertUtcToTimezone(String utcDateTimeString, String targetTimezone) {
        Instant instant = Instant.parse(utcDateTimeString);

        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(targetTimezone));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        String dateTimeString = zonedDateTime.format(formatter);

        return new DateTime(dateTimeString);
    }

    public List<Event> testCalendarService(String accessToken) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);

        Calendar testService =
            new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = testService.events().list("primary").setMaxResults(10).setTimeMin(now).setOrderBy("startTime").setSingleEvents(true).execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
        return items;
    }
}

