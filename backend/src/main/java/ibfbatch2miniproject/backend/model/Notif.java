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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notif {
    
    private String imageUrl; 
    private String name;
    private String action;
    private Date date; 

    //
    public static JsonArray toJsonArray(List<Notif> notifs) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (Notif notif : notifs) {
            JsonObject jo = Json.createObjectBuilder()
                                .add("imageUrl", notif.getImageUrl())
                                .add("name", notif.getName())
                                .add("action", notif.getAction())
                                .add("date", notif.getDate().toString())
                                .build();
            arrBuilder.add(jo);
        }
        return arrBuilder.build();
    }
}
