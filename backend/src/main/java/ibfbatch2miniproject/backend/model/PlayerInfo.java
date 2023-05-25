package ibfbatch2miniproject.backend.model;

import java.sql.Date;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfo {

    private String name;
    private Integer weight;
    private Integer height;
    // private byte[] playerPhoto; 
    private String email;
    private Integer phoneNumber;
    private String DOB;
    private Integer emergencyContact;
    private String emergencyName;
    private String address;
    private String pastInjuries;
    private String role; // this is enum in SQL 
    private Integer yearJoined;

    private List<String> positions; 

    // to json 
    public JsonObject toJson() {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
        for (String pos : positions)
            arrBuilder.add(pos); 

        return Json.createObjectBuilder()
            .add("name", name)
            .add("weight", weight)
            .add("height", height)
            // .add("playerPhoto", playerPhoto)
            .add("email", email)
            .add("phoneNumber", phoneNumber)
            .add("DOB", DOB)
            .add("emergencyContact", emergencyContact)
            .add("emergencyName", emergencyName)
            .add("address", address)
            .add("pastInjuries", pastInjuries)
            .add("role", role)
            .add("yearJoined", yearJoined)
            .add("positions", arrBuilder.build()) // json array
            .build(); 
    }

}
