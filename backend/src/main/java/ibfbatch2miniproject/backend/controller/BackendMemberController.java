package ibfbatch2miniproject.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ibfbatch2miniproject.backend.service.BackendService;
import jakarta.json.JsonArray;

@Controller
public class BackendMemberController {

    @Autowired
    private BackendService backendSvc;
    
    // MEMBERS
    @GetMapping(path="/playerProfiles")
    public ResponseEntity<String> getPlayerProfiles() {
        JsonArray arr = backendSvc.getPlayerProfiles(); 
        // System.out.printf(">>> JsonArray sent from Backend: %s\n", arr.toString());
        return ResponseEntity.ok().body(arr.toString()); 
    }
    
}
