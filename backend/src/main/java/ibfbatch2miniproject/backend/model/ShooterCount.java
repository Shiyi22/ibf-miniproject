package ibfbatch2miniproject.backend.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShooterCount {

    private String name; 
    private Integer qtrPlayedInGame; 
    private BigDecimal shootingPercent; 
    
}
