package ibfbatch2miniproject.backend.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import ibfbatch2miniproject.backend.model.Login;
import ibfbatch2miniproject.backend.model.PlayerInfo;
import ibfbatch2miniproject.backend.model.PlayerInfo2;
import ibfbatch2miniproject.backend.repository.SQLProfileRepository;
import ibfbatch2miniproject.backend.service.BackendService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Controller
public class BackendProfileController {

    @Autowired
    private BackendService backendSvc;

    @Autowired
    private SQLProfileRepository profileRepo; 

    // LOGIN 
    @PostMapping(path="/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginCred(@RequestBody Login login) {
        String response = backendSvc.getLoginCreds(login);
        System.out.printf(">>> JWT/message: %s\n", response);
        JsonObject json = Json.createObjectBuilder().add("message", response).build(); 
        return ResponseEntity.ok().body(json.toString());
    }

    // save login creds
    @PostMapping(path="/saveLoginCreds")
    public ResponseEntity<String> saveLoginCreds(@RequestBody Login signup) {
        boolean isSaved = profileRepo.saveLoginCreds(signup);
        JsonObject jo = Json.createObjectBuilder().add("isSaved", isSaved).build();
        return ResponseEntity.ok().body(jo.toString());
    }

    // PROFILE 
    @GetMapping(path="/getId/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPlayerId(@PathVariable String username) {
        String userId = profileRepo.getPlayerId(username);
        JsonObject json = Json.createObjectBuilder().add("userId", userId).build(); 
        return ResponseEntity.ok().body(json.toString());
    }

    // save player profile from sign up 
    @PostMapping("/saveProfile")
    public ResponseEntity<String> savePlayerInfo(@ModelAttribute PlayerInfo2 info) throws IOException {
        System.out.printf(">>> Player Info sent from Angular to Backend: %s\n", info.toString()); 
        String userId = profileRepo.getPlayerId(info.getUsername());
        boolean isSaved = profileRepo.savePlayerInfo(info, userId);
        JsonObject jo = Json.createObjectBuilder().add("isSaved", isSaved).build(); 
        return ResponseEntity.ok().body(jo.toString());
    }
    
    @GetMapping(path="/playerInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPlayerInfo(@RequestParam String userId) {
        PlayerInfo info = profileRepo.getPlayerInfo(userId); 
        if (info.getPlayerPhoto() != null) {
            String encodedString = Base64.getEncoder().encodeToString(info.getPlayerPhoto());
            return ResponseEntity.ok().body(info.toJson(encodedString).toString());
        }
        return ResponseEntity.ok().body(info.toJson().toString()); // no playerPhoto sent across 
    }

    @PostMapping(path="/updatePlayerInfo/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePlayerInfo(@RequestBody PlayerInfo info, @PathVariable String username) {
        System.out.printf(">>> Player Info sent from Angular to Backend: %s\n", info.toString()); // dob has to be small letters 
        String userId = profileRepo.getPlayerId(username);
        boolean isUpdated = profileRepo.updatePlayerInfo(info, userId);
        JsonObject jo = Json.createObjectBuilder().add("updated", isUpdated).build(); 
        return ResponseEntity.ok().body(jo.toString());
    }

    @PutMapping(path="/updatePhoto/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePhoto(@RequestPart MultipartFile picture, @PathVariable String username) throws IOException {
        String userId = profileRepo.getPlayerId(username); 
        boolean isUpdated = profileRepo.updatePhoto(picture, userId); 
        JsonObject jo = Json.createObjectBuilder().add("updated", isUpdated).build(); 
        return ResponseEntity.ok().body(jo.toString());
    }

}
