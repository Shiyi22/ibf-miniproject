package ibfbatch2miniproject.backend.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfo2 {

    private String name;
    private Integer weight;
    private Integer height;
    private MultipartFile playerPhoto; 
    private String email;
    private Integer phoneNumber;
    private String dob;
    private Integer emergencyContact;
    private String emergencyName;
    private String address;
    private String pastInjuries;
    private String role; // this is enum in SQL 
    private Integer yearJoined;

    private List<String> positions; 

    private String username;
    
}
