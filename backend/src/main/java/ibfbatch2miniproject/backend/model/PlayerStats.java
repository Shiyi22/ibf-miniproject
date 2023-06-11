package ibfbatch2miniproject.backend.model;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStats {

    private Integer cap; 
    private BigDecimal avgShootingPercent; 
    private BigDecimal avgInterceptionPerGame;
    private Date lastUpdated; 

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                    .add("cap", cap != null ? cap : 0)
                    .add("avgShootingPercent", avgShootingPercent != null? avgShootingPercent.toString() : "-")
                    .add("avgInterceptionPerGame", avgInterceptionPerGame != null ? avgInterceptionPerGame.toString() : "-")
                    .build();
    }
}
