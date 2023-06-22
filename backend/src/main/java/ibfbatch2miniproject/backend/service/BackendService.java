package ibfbatch2miniproject.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                                    .add("email", player.getEmail())
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
                                    .add("email", player.getEmail())
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
        if (!isSaved)
            return false; // early terminate 

        List<String> playersToAddCap = new LinkedList<>(); 
        Map<String, Integer> interceptionTotalCountInGame = new HashMap<>();
        List<ShooterCount> shooters = new LinkedList<>();
        
        // update indv stats by each quarter
        for (QuarterData qtr : fullGameData) {
            // get positions by quarter but update competition cap by game (not qtr)
            if (!playersToAddCap.contains(qtr.getGk()))
                playersToAddCap.add(qtr.getGk()); // add to the namelist
            if (!playersToAddCap.contains(qtr.getGd()))
                playersToAddCap.add(qtr.getGd()); 
            if (!playersToAddCap.contains(qtr.getWd()))
                playersToAddCap.add(qtr.getWd());
            if (!playersToAddCap.contains(qtr.getC()))
                playersToAddCap.add(qtr.getC());
            if (!playersToAddCap.contains(qtr.getWa()))
                playersToAddCap.add(qtr.getWa());
            if (!playersToAddCap.contains(qtr.getGa()))
                playersToAddCap.add(qtr.getGa());
            if (!playersToAddCap.contains(qtr.getGs()))
                playersToAddCap.add(qtr.getGs());

            // update interceptions to game total
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
                    // interception count goes by name instead of position in total game 
                    Integer prevCount = interceptionTotalCountInGame.get(name) != null? interceptionTotalCountInGame.get(name) : 0; // FIXED
                    interceptionTotalCountInGame.put(name, prevCount + entry.getValue());
                }
            }

            // update shooting percentage (shooters only) into HashMap to collate into full game % 
            String gaName = qtr.getGa(); BigDecimal gaPercent = BigDecimal.valueOf(-1); 
            String gsName = qtr.getGs(); BigDecimal gsPercent = BigDecimal.valueOf(-1); 

            if (qtr.getGaTotalShots() > 0) {
                if (qtr.getGaShotIn() > 0) 
                    gaPercent = BigDecimal.valueOf((qtr.getGaShotIn() * 100 / qtr.getGaTotalShots())); 
                else 
                    gaPercent = BigDecimal.valueOf(0); 
            }  
            if (qtr.getGsTotalShots() > 0) {
                if (qtr.getGsShotIn() > 0)
                    gsPercent = BigDecimal.valueOf((qtr.getGsShotIn() * 100/ qtr.getGsTotalShots()));
                else
                    gsPercent = BigDecimal.valueOf(0);
            }
            
            boolean gaFound = false;
            boolean gsFound = false; 
            if (shooters.size() == 0 && !gaPercent.equals(BigDecimal.valueOf(-1)) && !gsPercent.equals(BigDecimal.valueOf(-1))) {
                shooters.add(new ShooterCount(gaName, 1, gaPercent)); 
                shooters.add(new ShooterCount(gsName, 1, gsPercent));
            } else if (shooters.size() == 0 && !gaPercent.equals(BigDecimal.valueOf(-1))) {
                shooters.add(new ShooterCount(gaName, 1, gaPercent)); 
            } else if (shooters.size() == 0 && !gsPercent.equals(BigDecimal.valueOf(-1))) {
                shooters.add(new ShooterCount(gsName, 1, gsPercent));
            } else if (shooters.size() > 0) {
                for (ShooterCount shooter : shooters) {
                    // Goal Attack
                    if (shooter.getName().equals(gaName) && !gaPercent.equals(BigDecimal.valueOf(-1))) {
                        // set new shooting % 
                        BigDecimal prevPercentage = shooter.getShootingPercent(); 
                        BigDecimal prevQtrPlayedInGame = BigDecimal.valueOf(shooter.getQtrPlayedInGame()); 
                        shooter.setShootingPercent(prevPercentage.multiply(prevQtrPlayedInGame).add(gaPercent).divide(prevQtrPlayedInGame.add(BigDecimal.valueOf(1)), 2, RoundingMode.HALF_UP));
                        // set qtr played + 1
                        shooter.setQtrPlayedInGame(shooter.getQtrPlayedInGame() + 1);
                        gaFound = true; 
                    } 
                    // Goal Shooter
                    if (shooter.getName().equals(gsName) && !gsPercent.equals(BigDecimal.valueOf(-1))) {
                        // set new shooting % 
                        BigDecimal prevPercentage = shooter.getShootingPercent(); 
                        BigDecimal prevQtrPlayedInGame = BigDecimal.valueOf(shooter.getQtrPlayedInGame()); 
                        shooter.setShootingPercent(prevPercentage.multiply(prevQtrPlayedInGame).add(gsPercent).divide(prevQtrPlayedInGame.add(BigDecimal.valueOf(1)), 2, RoundingMode.HALF_UP));
                        // set qtr played + 1
                        shooter.setQtrPlayedInGame(shooter.getQtrPlayedInGame() + 1);
                        gsFound = true; 
                    } 
                }
                if (!gaFound && !gaPercent.equals(BigDecimal.valueOf(-1))) 
                    shooters.add(new ShooterCount(gaName, 1, gaPercent)); 
                if (!gsFound && !gsPercent.equals(BigDecimal.valueOf(-1)))
                    shooters.add(new ShooterCount(gsName, 1, gsPercent)); 
            }
        }
        
        // update shooting percentage by game 
        System.out.printf(">>> Shooting percentage update: %s\n", shooters.toString());
        for (ShooterCount shooter : shooters) {
            boolean shootingPercentUpdated = statsRepo.updateShootingPercentage(shooter);
            if (!shootingPercentUpdated)
                return false; // early terminate
        }

        // update avg interceptions percentage by game (all entries will have > 0 interceptions) // removed if entry.getValue() > 0 
        System.out.printf(">>> Total Interceptions by player in game: %s\n", interceptionTotalCountInGame); 
        for (Map.Entry<String, Integer> entry : interceptionTotalCountInGame.entrySet()) {
            // get userId and update player avgInterception if count > 0
            String userId = profileRepo.getPlayerId(entry.getKey());
            boolean interceptUpdated = statsRepo.updateInterception(userId, entry.getValue());
            if (!interceptUpdated)
                return false ; // early terminate 
        }

        // update cap and lastUpdate (date) last 
        System.out.printf(">>> All the players in this game to add cap: %s\n", playersToAddCap.toString());
        for (String name: playersToAddCap) {
            // update indv stats table
            String userId = profileRepo.getPlayerId(name);
            boolean capUpdated = statsRepo.updateCapAndDate(userId);
            if (!capUpdated)
                return false; // early terminate 
        }
        // if there is no early termination, return true [Success]
        return true; 
    }
}
