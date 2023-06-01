package ibfbatch2miniproject.backend.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ibfbatch2miniproject.backend.model.PlayerProfile;

@Repository
public class SQLMemberRepository {

    @Autowired
    private JdbcTemplate template; 

    private final String GET_LIST_PLAYER_PROFILES_SQL = "select id, name, playerPhoto from playerInfo"; 
    private final String GET_PLAYER_POSITIONS_SQL = "select position from playerPosition where id = ?"; 

    public List<PlayerProfile> getPlayerProfiles() {
        List<PlayerProfile> profiles = template.query(GET_LIST_PLAYER_PROFILES_SQL, BeanPropertyRowMapper.newInstance(PlayerProfile.class)); 

        // list of positions for each member
        for (PlayerProfile player : profiles) {
            String userId = player.getId();
            List<String> positions = template.queryForList(GET_PLAYER_POSITIONS_SQL, String.class, userId);
            player.setPositions(positions);
        }

        // check if all fields are in place
        // System.out.printf(">>> Player Profile retrieved: %s\n", profiles); 
        return profiles; 
    }
    
}
