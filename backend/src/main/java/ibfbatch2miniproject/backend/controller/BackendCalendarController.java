package ibfbatch2miniproject.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;

import ibfbatch2miniproject.backend.model.EmailRequest;
import ibfbatch2miniproject.backend.model.Event;
import ibfbatch2miniproject.backend.model.Notif;
import ibfbatch2miniproject.backend.repository.SQLCalendarRepository;
import ibfbatch2miniproject.backend.service.EmailService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@RestController
public class BackendCalendarController {

    @Autowired
    private SQLCalendarRepository calRepo;

    @Autowired
    private EmailService emailSvc;

    // retrieve list of events 
    @GetMapping("/events") 
    public ResponseEntity<String> getCalendarEvents() {
        List<Event> events = calRepo.getCalEvents(); 
        if (events.isEmpty()) {
            JsonObject jo = Json.createObjectBuilder().add("message", "empty").build();
            return ResponseEntity.ok().body(jo.toString());
        }
        return ResponseEntity.ok().body(Event.toJsonArray(events).toString()); 
    }

    // store event to list 
    @PostMapping("/storeEventToDB")
    public ResponseEntity<String> storeEventToDB(@RequestBody Event event) {
        boolean isAdded = calRepo.storeEventToDB(event);
        JsonObject jo = Json.createObjectBuilder().add("isAdded", isAdded).build();
        return ResponseEntity.ok().body(jo.toString());
    }

    // delete event from DB
    @DeleteMapping("/removeEventFromDB/{eventId}")
    public ResponseEntity<String> removeEvent(@PathVariable String eventId) {
        boolean isRemoved = calRepo.removeEvent(eventId);
        JsonObject jo = Json.createObjectBuilder().add("isRemoved", isRemoved).build();
        return ResponseEntity.ok().body(jo.toString());
    }

    // save notification 
    @PostMapping("/saveNotification")
    public ResponseEntity<String> saveNotif(@RequestBody Notif notif) {
        boolean isSaved = calRepo.saveNotif(notif);
        JsonObject jo = Json.createObjectBuilder().add("isSaved", isSaved).build();
        return ResponseEntity.ok().body(jo.toString());
    }
    
    // get notifications 
    @GetMapping("/getNotifications")
    public ResponseEntity<String> getNotif() {
        List<Notif> notifs = calRepo.getNotif();
        if (notifs.isEmpty()) {
            JsonObject jo = Json.createObjectBuilder().add("message", "empty").build();
            return ResponseEntity.ok().body(jo.toString());
        }
        return ResponseEntity.ok().body(Notif.toJsonArray(notifs).toString());
    }

    // Send Email
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest email) {
        try {
            emailSvc.sendEmail(email.getTo(), email.getSubject(), email.getBody());
            JsonObject jo = Json.createObjectBuilder().add("emailSent", true).build();
            return ResponseEntity.ok().body(jo.toString()); 
        } catch (MailException ex) {
            ex.printStackTrace();
            JsonObject jo = Json.createObjectBuilder().add("emailSent", false).build();
            return ResponseEntity.ok().body(jo.toString()); 
        }
    } 

    // Save Email to email list for new member sign up
    @PostMapping("/addEmailToList")
    public ResponseEntity<String> addEmailToList(@RequestBody String email) {
        boolean isSaved = calRepo.saveEmailToList(email);
        JsonObject jo = Json.createObjectBuilder().add("isSaved", isSaved).build();
        return ResponseEntity.ok().body(jo.toString());
    }

    // Get approved email list 
    @GetMapping("/getApprovedEmailList")
    public ResponseEntity<String> getApprovedEmails() {
        List<String> emails = calRepo.getApprovedEmailList();
        if (emails.isEmpty()) {
            JsonObject jo = Json.createObjectBuilder().add("message", "empty").build();
            return ResponseEntity.ok().body(jo.toString());
        }
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (String email : emails) {
            arrBuilder.add(email);
        }
        return ResponseEntity.ok().body(arrBuilder.build().toString());
    }
}
