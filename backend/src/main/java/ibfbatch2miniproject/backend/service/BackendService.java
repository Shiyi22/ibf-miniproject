package ibfbatch2miniproject.backend.service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ibfbatch2miniproject.backend.model.Login;
import ibfbatch2miniproject.backend.model.PlayerProfile;
import ibfbatch2miniproject.backend.model.QuarterData;
import ibfbatch2miniproject.backend.model.ShooterCount;
import ibfbatch2miniproject.backend.repository.SQLProfileRepository;
import ibfbatch2miniproject.backend.repository.SQLStatisticsRepository;
import ibfbatch2miniproject.backend.utils.JWTUtils;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Service
public class BackendService {

    @Autowired
    private SQLProfileRepository profileRepo; 

    @Autowired
    private JWTUtils utils;

    @Autowired
    private SQLStatisticsRepository statsRepo; 

    private static final String BASE64_PREFIX = "data:image/jpg;base64,";
    
    public String getLoginCreds(Login login) {
        Optional<Boolean> opt = profileRepo.getLoginCreds(login); 
        if (opt.isEmpty())
            return "Invalid username"; 

        // wrong password 
        if (opt.get() == false)
            return "Invalid password";

        // correct credentials, send jwt
        return utils.generateToken(login);
    }

    public JsonArray getPlayerProfiles() {
        List<PlayerProfile> profiles = statsRepo.getPlayerProfiles();
        JsonArrayBuilder arrB = Json.createArrayBuilder(); 
        
        for (PlayerProfile player: profiles) {
            if (player.getPlayerPhoto() == null) {
                JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
                for (String pos : player.getPositions())
                    arrBuilder.add(pos); 
            
                JsonObject jo = Json.createObjectBuilder()
                                    .add("id", player.getId())
                                    .add("name", player.getName())
                                    .add("positions", arrBuilder.build())
                                    .build();
                arrB.add(jo); 
            } else {
                String encodedString = Base64.getEncoder().encodeToString(player.getPlayerPhoto());

                JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
                for (String pos : player.getPositions())
                    arrBuilder.add(pos); 
                
                JsonObject jo = Json.createObjectBuilder()
                                    .add("id", player.getId())
                                    .add("name", player.getName())
                                    .add("playerPhoto", BASE64_PREFIX + encodedString)
                                    .add("positions", arrBuilder.build())
                                    .build();
                arrB.add(jo); 
            }
        }
        return arrB.build();
    }

    // For every game stats saved, update individual player stats 
    public boolean saveFullGameAndUpdateIndvStats(QuarterData[] fullGameData) {
        boolean isSaved = statsRepo.saveFullGameData(fullGameData); 

        List<String> playersToAddCap = new LinkedList<>(); 
        List<ShooterCount> shooters = new LinkedList<>();
        
        // update indv stats by each quarter
        for (QuarterData qtr : fullGameData) {
            // get positions by quarter but update competition cap by game (not qtr)
            if (!playersToAddCap.contains(qtr.getGk()))
                playersToAddCap.add(qtr.getGk()); // add to the namelist
            else if (!playersToAddCap.contains(qtr.getGd()))
                playersToAddCap.add(qtr.getGd()); 
            else if (!playersToAddCap.contains(qtr.getWd()))
                playersToAddCap.add(qtr.getWd());
            else if (!playersToAddCap.contains(qtr.getC()))
                playersToAddCap.add(qtr.getC());
            else if (!playersToAddCap.contains(qtr.getWa()))
                playersToAddCap.add(qtr.getWa());
            else if (!playersToAddCap.contains(qtr.getGa()))
                playersToAddCap.add(qtr.getGa());
            else if (!playersToAddCap.contains(qtr.getGs()))
                playersToAddCap.add(qtr.getGs());

            // update interceptions per game
            Map<String, Integer> interceptions = qtr.getInterceptions();
            for (Map.Entry<String, Integer> entry : interceptions.entrySet()) {
                if (entry.getValue() > 0) {
                    String position = entry.getKey();
                    String name = ""; // find name 
                    switch (position) {
                        case "GS": 
                            name = qtr.getGs(); 
                            break; 
                        case "GA": 
                            name = qtr.getGa(); 
                            break; 
                        case "WA": 
                            name = qtr.getWa(); 
                            break; 
                        case "C": 
                            name = qtr.getC(); 
                            break; 
                        case "WD": 
                            name = qtr.getWd(); 
                            break; 
                        case "GD": 
                            name = qtr.getGd(); 
                            break;    
                        case "GK": 
                            name = qtr.getGk(); 
                            break;                          
                    }
                    // get userId and update interceptions per game
                    String userId = profileRepo.getPlayerId(name); 
                    boolean interceptUpdated = statsRepo.updateInterception(userId, entry.getValue());
                    if (!interceptUpdated)
                        return false ; // early terminate 
                } 
            }

            // update shooting percentage (shooters only) into HashMap
            String gaName = qtr.getGa();
            String gsName = qtr.getGs();
            BigDecimal gaPercent = BigDecimal.valueOf(qtr.getGaShotIn() / qtr.getGaTotalShots() * 100);
            BigDecimal gsPercent = BigDecimal.valueOf(qtr.getGsShotIn() / qtr.getGsTotalShots() * 100);

            for (ShooterCount shooter : shooters) {
                // Goal Attack
                if (shooter.getName().equals(gaName)) {
                    // set new shooting % 
                    BigDecimal prevPercentage = shooter.getShootingPercent(); 
                    BigDecimal prevQtrPlayedInGame = BigDecimal.valueOf(shooter.getQtrPlayedInGame()); 
                    shooter.setShootingPercent(prevPercentage.multiply(prevQtrPlayedInGame).add(gaPercent).divide(prevQtrPlayedInGame.add(BigDecimal.valueOf(1))));
                    // set qtr played + 1
                    shooter.setQtrPlayedInGame(shooter.getQtrPlayedInGame() + 1);
                } else { // add to the list 
                    shooters.add(new ShooterCount(gaName, 1, gaPercent)); 
                }
                
                // Goal Shooter
                if (shooter.getName().equals(gsName)) {
                    // set new shooting % 
                    BigDecimal prevPercentage = shooter.getShootingPercent(); 
                    BigDecimal prevQtrPlayedInGame = BigDecimal.valueOf(shooter.getQtrPlayedInGame()); 
                    shooter.setShootingPercent(prevPercentage.multiply(prevQtrPlayedInGame).add(gsPercent).divide(prevQtrPlayedInGame.add(BigDecimal.valueOf(1))));
                    // set qtr played + 1
                    shooter.setQtrPlayedInGame(shooter.getQtrPlayedInGame() + 1);
                } else { // add to the list 
                    shooters.add(new ShooterCount(gsName, 1, gsPercent)); 
                }
            }

            // TODO: date saved 

        }
        // update shooting percentage by game 
        for (ShooterCount shooter : shooters) {
            statsRepo.updateShootingPercentage(shooter);
        }

        // cap updated last 
        for (String name: playersToAddCap) {
            // update indv stats table
            String userId = profileRepo.getPlayerId(name);
            boolean capUpdated = statsRepo.updateCap(userId);
            if (!capUpdated)
                return false; // early terminate 
        }

    }
}
