package ibfbatch2miniproject.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ibfbatch2miniproject.backend.model.GameData;
import ibfbatch2miniproject.backend.model.QuarterData;
import ibfbatch2miniproject.backend.repository.SQLStatisticsRepository;
import ibfbatch2miniproject.backend.service.BackendService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@Controller
public class BackendStatisticsController {

    @Autowired
    private BackendService backendSvc;

    @Autowired
    private SQLStatisticsRepository statsRepo; 
    
    // get list of player profiles
    @GetMapping(path="/playerProfiles")
    public ResponseEntity<String> getPlayerProfiles() {
        JsonArray arr = backendSvc.getPlayerProfiles(); 
        // System.out.printf(">>> JsonArray sent from Backend: %s\n", arr.toString());
        return ResponseEntity.ok().body(arr.toString()); 
    }

    // TODO: get individual statistics when clicked on profile 
    @GetMapping(path="/{name}/stats")
    public ResponseEntity<String> getPlayerStats() {
        return ResponseEntity.ok().body("");
    }

    //save game data to SQL 
    @PostMapping(path="/saveGameData")
    public ResponseEntity<String> saveGameData(@RequestBody GameData gameData) {
        Integer gameId = statsRepo.saveGameData(gameData);
        JsonObject jo = Json.createObjectBuilder().add("game_id", gameId.toString()).build();
        return ResponseEntity.ok().body(jo.toString());
    }
    
    // save full game data to SQL
    @PostMapping(path="/saveFullGameData")
    public ResponseEntity<String> saveFullGameData(@RequestBody QuarterData[] fullGameData) {
        for (QuarterData qtr : fullGameData)
            System.out.printf(">>> Full Game Data from Angular: %s\n", qtr); 
        
        // TODO: Change to backendSvc
        boolean isSaved = statsRepo.saveFullGameData(fullGameData); 
        JsonObject jo = Json.createObjectBuilder().add("is saved", isSaved).build(); 
        return ResponseEntity.ok().body(jo.toString());
    }
}
