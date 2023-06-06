package ibfbatch2miniproject.backend.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ibfbatch2miniproject.backend.model.GameData;
import ibfbatch2miniproject.backend.model.PlayerProfile;
import ibfbatch2miniproject.backend.model.QuarterData;

@Repository
public class SQLStatisticsRepository {

    @Autowired
    private JdbcTemplate template; 

    private Integer gameId; 

    private final String GET_LIST_PLAYER_PROFILES_SQL = "select id, name, playerPhoto from playerInfo"; 
    private final String GET_PLAYER_POSITIONS_SQL = "select position from playerPosition where id = ?"; 
    private final String SAVE_GAME_DATA_SQL = "insert into GameData (label, against, date) values (?, ?, ?)"; 

    private final String SAVE_FULL_GAME_DATA_SQL = "insert into FullGameData (game_id, gs, ga, wa, c, wd, gd, gk, ownScore, oppScore, gaShotIn, gsShotIn, gaTotalShots, gsTotalShots, ownCpCount, oppCpCount, oppSelfError, goodTeamD, oppMissShot, interceptions, lostSelfError, lostByIntercept, quarterSequence) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    // public Integer saveGameData(GameData gameData) {
    //     return template.update(SAVE_GAME_DATA_SQL, gameData.getLabel(), gameData.getAgainst(), gameData.getDate());
    // }

    public Integer saveGameData(GameData gameData) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(SAVE_GAME_DATA_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, gameData.getLabel());
            ps.setString(2, gameData.getAgainst());
            ps.setDate(3, gameData.getDate());
            return ps;
        }, keyHolder);
    
        // Retrieve the generated game_id
        gameId = keyHolder.getKey().intValue();
        return gameId;
    }
    

    public boolean saveFullGameData(QuarterData[] fullGameData) {
        int[] arrRowsAffected = template.batchUpdate(SAVE_FULL_GAME_DATA_SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                QuarterData qtrData = fullGameData[i];
                ps.setInt(1, gameId);
                // set player positions
                ps.setString(2, qtrData.getGs());
                ps.setString(3, qtrData.getGa());
                ps.setString(4, qtrData.getWa());
                ps.setString(5, qtrData.getC());
                ps.setString(6, qtrData.getWd());
                ps.setString(7, qtrData.getGd());
                ps.setString(8, qtrData.getGk());
                // set stats 
                ps.setInt(9, qtrData.getOwnScore());
                ps.setInt(10, qtrData.getOppScore());
                ps.setInt(11, qtrData.getGaShotIn());
                ps.setInt(12, qtrData.getGsShotIn());
                ps.setInt(13, qtrData.getGaTotalShots());
                ps.setInt(14, qtrData.getGsTotalShots());
                ps.setInt(15, qtrData.getOwnCpCount());
                ps.setInt(16, qtrData.getOppCpCount());
                ps.setInt(17, qtrData.getOppSelfError());
                ps.setInt(18, qtrData.getGoodTeamD());
                ps.setInt(19, qtrData.getOppMissShot());
                try {
                    ps.setString(20, new ObjectMapper().writeValueAsString(qtrData.getInterceptions()));
                    ps.setString(23, new ObjectMapper().writeValueAsString(qtrData.getQuarterSequence()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return; 
                }
                ps.setInt(21, qtrData.getLostSelfError());
                ps.setInt(22, qtrData.getLostByIntercept());
            }

            @Override
            public int getBatchSize() {
                return fullGameData.length;
            }
        });
        
        if (arrRowsAffected.length < fullGameData.length)
            return false; // ended prematurely due to error 
        return true; 
    }
    
}
