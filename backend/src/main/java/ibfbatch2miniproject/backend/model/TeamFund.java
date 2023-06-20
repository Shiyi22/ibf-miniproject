package ibfbatch2miniproject.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamFund {
    
    private String id;
    private String name;
    private boolean paid; 

    // public JsonArray toJsonArray(List<TeamFund> funds) {
    //     JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
    //     for (TeamFund indv : funds) {
    //         JsonObject jo = Json.createObjectBuilder().add("id", indv.getId())
    //                                                     .add("name", indv.getName())
    //                                                     .add("paid", indv.isPaid())
    //                                                     .build();
    //         arrBuilder.add(jo);
    //     }
    //     return arrBuilder.build();
    // }

}
