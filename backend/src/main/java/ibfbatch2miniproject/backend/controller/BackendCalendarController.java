package ibfbatch2miniproject.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ibfbatch2miniproject.backend.model.Event;
import ibfbatch2miniproject.backend.repository.SQLCalendarRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
public class BackendCalendarController {

    @Autowired
    private SQLCalendarRepository calRepo;

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

    // save notification 
    @PostMapping("/saveNotifications")
    public ResponseEntity<String> saveNotif(@RequestBody Notif notif) {

    }

    
}
