package ibfbatch2miniproject.backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import ibfbatch2miniproject.backend.model.GameData;
import ibfbatch2miniproject.backend.model.PlayerStats;
import ibfbatch2miniproject.backend.model.QuarterData;
import ibfbatch2miniproject.backend.repository.S3Repository;
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

    @Autowired
    private S3Repository s3Repo; 
    
    // get list of player profiles
    @GetMapping(path="/playerProfiles")
    public ResponseEntity<String> getPlayerProfiles() {
        JsonArray arr = backendSvc.getPlayerProfiles(); 
        // System.out.printf(">>> JsonArray sent from Backend: %s\n", arr.toString());
        return ResponseEntity.ok().body(arr.toString()); 
    }

    // get individual statistics when clicked on profile 
    @GetMapping(path="/{userId}/stats")
    public ResponseEntity<String> getPlayerStats(@PathVariable String userId) {
        PlayerStats stats = statsRepo.getPlayerStats(userId); 
        return ResponseEntity.ok().body(stats.toJson().toString());
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
        
        boolean isSavedAndUpdatedStats = backendSvc.saveFullGameAndUpdateIndvStats(fullGameData); 
        JsonObject jo = Json.createObjectBuilder().add("isSaved", isSavedAndUpdatedStats).build(); 
        return ResponseEntity.ok().body(jo.toString());
    }

    // get list of game data for display
    @GetMapping(path="/getGameList")
    public ResponseEntity<String> getGameDataList() {
        Optional<List<GameData>> opt = statsRepo.getGameDataList();
        if (opt.isEmpty()) {
            JsonObject jo = Json.createObjectBuilder().add("message", "no game records").build(); 
            return ResponseEntity.ok().body(jo.toString());
        }
        List<GameData> list = opt.get();
        return ResponseEntity.ok().body(GameData.toJsonArray(list).toString());
    }

    // get game data using game id 
    @GetMapping(path="/getGameData/{gameId}")
    public ResponseEntity<String> getGameDataById(@PathVariable Integer gameId) {
        GameData data = statsRepo.getGameDataById(gameId);
        return ResponseEntity.ok().body(GameData.toJson(data).toString()); 
    }

    // get full game data using game id
    @GetMapping(path="/getFullGameData/{gameId}")
    public ResponseEntity<String> getFullGameData(@PathVariable Integer gameId) throws JsonProcessingException {
        List<QuarterData> qtrs = statsRepo.getFullGameData(gameId); 
        // convert QuarterData[] to jsonArray 
        return ResponseEntity.ok().body(QuarterData.toJsonArray(qtrs).toString());
    }

    // upload media to S3 
    @PostMapping(path="/uploadToS3")
    public ResponseEntity<String> uploadToS3(@RequestParam("game_id") Integer gameId, @RequestPart MultipartFile media) throws IOException {
        String s3url = s3Repo.uploadToS3(gameId, media);
        JsonObject jo = Json.createObjectBuilder().add("s3url", s3url).build();
        return ResponseEntity.ok().body(jo.toString()); 
    }

    // update s3 url to sql 
    @PostMapping(path="/updateS3UrlToSql/{mediaTypeToUpload}/{gameId}")
    public ResponseEntity<String> updateS3UrlToSql(@RequestBody String s3Url, @PathVariable String mediaTypeToUpload, @PathVariable Integer gameId) {
        System.out.printf(">>> Parameters: s3Url: %s, mediaTypeToUpload: %s, gameId: %d\n", s3Url, mediaTypeToUpload, gameId);
        boolean isUpdated = statsRepo.updateS3UrlToSql(s3Url, mediaTypeToUpload, gameId);
        JsonObject jo = Json.createObjectBuilder().add("isUrlUpdated", isUpdated).build();
        return ResponseEntity.ok().body(jo.toString());
    }
}
