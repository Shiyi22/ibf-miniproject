package ibfbatch2miniproject.backend.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventData {

    private List<EventResult> results;
    private String username; 
}
