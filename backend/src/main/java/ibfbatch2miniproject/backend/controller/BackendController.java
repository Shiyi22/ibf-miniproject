package ibfbatch2miniproject.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import ibfbatch2miniproject.backend.model.Login;
import ibfbatch2miniproject.backend.model.PlayerInfo;
import ibfbatch2miniproject.backend.repository.SQLRepository;
import ibfbatch2miniproject.backend.service.BackendService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Controller
public class BackendController {

    @Autowired
    private BackendService backendSvc;

    @Autowired
    private SQLRepository sqlRepo; 

    @PostMapping(path="/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginCred(@RequestBody Login login) {
        String response = backendSvc.getLoginCreds(login);
        System.out.printf(">>> JWT/message: %s\n", response);
        JsonObject json = Json.createObjectBuilder().add("message", response).build(); 
        return ResponseEntity.ok().body(json.toString());
    }

    @GetMapping(path="/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPlayerId(@PathVariable String username) {
        String userId = sqlRepo.getPlayerId(username);
        JsonObject json = Json.createObjectBuilder().add("userId", userId).build(); 
        return ResponseEntity.ok().body(json.toString());
    }

    @GetMapping(path="/playerInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPlayerInfo(@RequestParam String userId) {
        PlayerInfo info = sqlRepo.getPlayerInfo(userId); 
        return ResponseEntity.ok().body(info.toJson().toString());
    }

    @PostMapping(path="/updatePlayerInfo/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePlayerInfo(@RequestBody PlayerInfo info, @PathVariable String username) {
        // DOB is not sent across 
        System.out.printf(">>> DOB: %s\n", info.getDOB()); // this is null!
        String userId = sqlRepo.getPlayerId(username);
        boolean isUpdated = sqlRepo.updatePlayerInfo(info, userId);
        JsonObject jo = Json.createObjectBuilder().add("updated", isUpdated).build(); 
        return ResponseEntity.ok().body(jo.toString());
    }
    
}
