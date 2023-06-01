package ibfbatch2miniproject.backend.model;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final String BASE64_PREFIX = "data:image/jpg;base64,";

    private String name;
    private Integer weight;
    private Integer height;
    private byte[] playerPhoto; 
    private String email;
    private Integer phoneNumber;
    private String dob;
    private Integer emergencyContact;
    private String emergencyName;
    private String address;
    private String pastInjuries;
    private String role; // this is enum in SQL 
    private Integer yearJoined;

    private List<String> positions; 

    // populate from resut set 
    public static PlayerInfo populate(ResultSet rs) throws SQLException {
        final PlayerInfo info = new PlayerInfo(); 
        info.setName(rs.getString("name"));
        info.setWeight(rs.getInt("weight"));
        info.setHeight(rs.getInt("height"));
        info.setPlayerPhoto(rs.getBytes("playerPhoto")); // may be null 
        info.setEmail(rs.getString("email"));
        info.setPhoneNumber(rs.getInt("phoneNumber"));
        info.setDob(rs.getString("dob"));
        info.setEmergencyContact(rs.getInt("emergencyContact"));
        info.setEmergencyName(rs.getString("emergencyName"));
        info.setAddress(rs.getString("address"));
        info.setPastInjuries(rs.getString("pastInjuries"));
        info.setRole(rs.getString("role"));
        info.setYearJoined(rs.getInt("yearJoined"));
        return info; 
    }

    // to json 
    public JsonObject toJson(String encodedString) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
        for (String pos : positions)
            arrBuilder.add(pos); 

        return Json.createObjectBuilder()
            .add("name", name)
            .add("weight", weight)
            .add("height", height)
            .add("playerPhoto", BASE64_PREFIX + encodedString)
            .add("email", email)
            .add("phoneNumber", phoneNumber)
            .add("dob", dob)
            .add("emergencyContact", emergencyContact)
            .add("emergencyName", emergencyName)
            .add("address", address)
            .add("pastInjuries", pastInjuries)
            .add("role", role)
            .add("yearJoined", yearJoined)
            .add("positions", arrBuilder.build()) // json array
            .build(); 
    }

    // no photo
    public JsonObject toJson() {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
        for (String pos : positions)
            arrBuilder.add(pos); 

        return Json.createObjectBuilder()
            .add("name", name)
            .add("weight", weight)
            .add("height", height)
            .add("email", email)
            .add("phoneNumber", phoneNumber)
            .add("dob", dob)
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
