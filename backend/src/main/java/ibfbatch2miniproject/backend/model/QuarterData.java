package ibfbatch2miniproject.backend.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuarterData {

    private String gs; 
    private String ga;
    private String wa;
    private String c;
    private String wd;
    private String gd;
    private String gk;

    private Integer ownScore; 
    private Integer oppScore;
    private Integer gaShotIn;
    private Integer gsShotIn;
    private Integer gaTotalShots; 
    private Integer gsTotalShots;
    private Integer ownCpCount;
    private Integer oppCpCount;
    private Integer oppSelfError;
    private Integer goodTeamD;
    private Integer oppMissShot;
    private Map<String, Integer> interceptions;
    private Integer lostSelfError;
    private Integer lostByIntercept;
    private String[][] quarterSequence; 
    
}
