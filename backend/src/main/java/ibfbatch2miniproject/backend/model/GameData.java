package ibfbatch2miniproject.backend.model;

import java.sql.Date;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
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
    private List<String> photoUrl;
    private List<String> videoUrl;


    // convert GameData to JsonObject 
    public static JsonObject toJson(GameData data) {
        JsonArrayBuilder arrPhotoBuilder = Json.createArrayBuilder(); 
        JsonArrayBuilder arrVideoBuilder = Json.createArrayBuilder(); 
        if (data.getPhotoUrl() != null) {
                for (String photo : data.getPhotoUrl())
                    arrPhotoBuilder.add(photo); 
            } 
            if (data.getVideoUrl() != null) {
                for (String video : data.getVideoUrl())
                    arrVideoBuilder.add(video);
            }

            return Json.createObjectBuilder()
                                .add("game_id", data.getGame_id())
                                .add("label", data.getLabel())
                                .add("against", data.getAgainst())
                                .add("date", data.getDate().toString())
                                .add("photoUrl", data.getPhotoUrl() != null ? arrPhotoBuilder.build() : JsonValue.EMPTY_JSON_ARRAY)
                                .add("videoUrl", data.getVideoUrl() != null ? arrVideoBuilder.build() : JsonValue.EMPTY_JSON_ARRAY)
                                .build(); 
    }

    // convert List<GameData> to jsonarray
    public static JsonArray toJsonArray(List<GameData> list) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
        for (GameData data : list) {
            arrBuilder.add(toJson(data)); 
        }
        return arrBuilder.build(); 
    }
    
}
