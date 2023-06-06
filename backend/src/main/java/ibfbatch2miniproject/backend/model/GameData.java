package ibfbatch2miniproject.backend.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class GameData {

    private String label;
    private String against;
    private Date date; 
    
}
