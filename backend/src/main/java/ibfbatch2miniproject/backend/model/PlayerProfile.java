package ibfbatch2miniproject.backend.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfile {

    private String id;
    private String name;
    private byte[] playerPhoto; 
    private List<String> positions; 
    
}
