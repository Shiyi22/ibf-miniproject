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
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private String eventId;
    private Date selectedDate;
    private String title;
    private String startTime;
    private String endTime;

    public static JsonArray toJsonArray(List<Event> events) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (Event evt : events) {
            JsonObject jo = Json.createObjectBuilder()
                                .add("eventId", evt.getEventId())
                                .add("selectedDate", evt.getSelectedDate().toString())
                                .add("title", evt.getTitle())
                                .add("startTime", evt.getStartTime())
                                .add("endTime", evt.getEndTime())
                                .build();
            arrBuilder.add(jo);
        }
        return arrBuilder.build();
    }
    
}
