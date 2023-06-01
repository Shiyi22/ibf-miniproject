package ibfbatch2miniproject.backend.service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ibfbatch2miniproject.backend.model.Login;
import ibfbatch2miniproject.backend.model.PlayerProfile;
import ibfbatch2miniproject.backend.repository.SQLMemberRepository;
import ibfbatch2miniproject.backend.repository.SQLProfileRepository;
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
    private SQLMemberRepository memberRepo; 
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
        List<PlayerProfile> profiles = memberRepo.getPlayerProfiles();
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

}
