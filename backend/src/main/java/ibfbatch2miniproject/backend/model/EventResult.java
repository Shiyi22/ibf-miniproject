package ibfbatch2miniproject.backend.model;

import java.util.List;


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResult {

    private String username;
    private String eventId;
    private String response;

    public static JsonArray toJsonArray(List<EventResult> results) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (EventResult res : results) {
            JsonObject jo = Json.createObjectBuilder().add("username", res.getUsername())
                                                        .add("eventId", res.getEventId())
                                                        .add("response", res.getResponse())
                                                        .build();
            arrBuilder.add(jo);
        }
        return arrBuilder.build();
    }
     
}
