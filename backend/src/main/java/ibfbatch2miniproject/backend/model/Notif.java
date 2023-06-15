package ibfbatch2miniproject.backend.model;

import java.sql.Date;

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
}
