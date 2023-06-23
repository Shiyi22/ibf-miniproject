package ibfbatch2miniproject.backend.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
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

    // convert QuarterData to JsonArray
    public static JsonArray toJsonArray(List<QuarterData> qtrs) throws JsonProcessingException {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (QuarterData qtr : qtrs) {
            JsonArrayBuilder sequenceArrayBuilder = Json.createArrayBuilder();
            for (String[] sequence : qtr.getQuarterSequence()) {
                JsonArrayBuilder innerArrayBuilder = Json.createArrayBuilder();
                for (String item : sequence) {
                    innerArrayBuilder.add(item);
                }
                sequenceArrayBuilder.add(innerArrayBuilder.build());
            }
            JsonObject jo = Json.createObjectBuilder()
                                .add("gs", qtr.getGs())
                                .add("ga", qtr.getGa())
                                .add("wa", qtr.getWa())
                                .add("c", qtr.getC())
                                .add("wd", qtr.getWd())
                                .add("gd", qtr.getGd())
                                .add("gk", qtr.getGk())
                                .add("ownScore", qtr.getOwnScore())
                                .add("oppScore", qtr.getOppScore())
                                .add("gaShotIn", qtr.getGaShotIn())
                                .add("gsShotIn", qtr.getGsShotIn())
                                .add("gaTotalShots", qtr.getGaTotalShots())
                                .add("gsTotalShots", qtr.getGsTotalShots())
                                .add("ownCpCount", qtr.getOwnCpCount())
                                .add("oppCpCount", qtr.getOppCpCount())
                                .add("oppSelfError", qtr.getOppSelfError())
                                .add("goodTeamD", qtr.getGoodTeamD())
                                .add("oppMissShot", qtr.getOppMissShot())
                                .add("interceptions", new ObjectMapper().writeValueAsString(qtr.getInterceptions()))
                                .add("lostSelfError", qtr.getLostSelfError())
                                .add("lostByIntercept", qtr.getLostByIntercept())
                                .add("quarterSequence",  sequenceArrayBuilder.build())
                                .build(); 
            arrBuilder.add(jo);
        }
        return arrBuilder.build();
    }
}
