package ibfbatch2miniproject.backend.model;

import java.sql.Date;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class GameData {

    private Integer game_id; 
    private String label;
    private String against;
    private Date date; 

    // convert List<GameData> to jsonarray
    public static JsonArray toJsonArray(List<GameData> list) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
        for (GameData data : list) {
            JsonObject jo = Json.createObjectBuilder()
                                .add("game_id", data.getGame_id())
                                .add("label", data.getLabel())
                                .add("against", data.getAgainst())
                                .add("date", data.getDate().toString())
                                .build(); 

            arrBuilder.add(jo); 
        }
        return arrBuilder.build(); 
    }
    
}
