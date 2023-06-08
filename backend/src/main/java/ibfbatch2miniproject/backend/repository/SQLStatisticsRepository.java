package ibfbatch2miniproject.backend.repository;

import java.math.BigDecimal;
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
import ibfbatch2miniproject.backend.model.ShooterCount;

@Repository
public class SQLStatisticsRepository {

    @Autowired
    private JdbcTemplate template; 
    
    @Autowired
    private SQLProfileRepository profileRepo; 

    private Integer gameId; 

    private final String GET_LIST_PLAYER_PROFILES_SQL = "select id, name, playerPhoto from playerInfo"; 
    private final String GET_PLAYER_POSITIONS_SQL = "select position from playerPosition where id = ?"; 
    private final String SAVE_GAME_DATA_SQL = "insert into GameData (label, against, date) values (?, ?, ?)"; 

    private final String SAVE_FULL_GAME_DATA_SQL = "insert into FullGameData (game_id, gs, ga, wa, c, wd, gd, gk, ownScore, oppScore, gaShotIn, gsShotIn, gaTotalShots, gsTotalShots, ownCpCount, oppCpCount, oppSelfError, goodTeamD, oppMissShot, interceptions, lostSelfError, lostByIntercept, quarterSequence) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String UPDATE_CAP_SQL = "update playerStats set cap = cap + 1 where id = ?"; 
    private final String GET_AVG_INTERCEPT_SQL = "select avgInterceptionPerGame from playerStats where id = ?";
    private final String GET_CAP_SQL = "select cap from playerStats where id = ?";
    private final String UPDATE_INTERCEPT_SQL = "update playerStats set avgInterceptionPerGame = ? where id = ?"; 
    private final String GET_AVG_SHOOT_PERCENT_SQL = "select avgShootingPercent from playerStats where id = ?";
    private final String UPDATE_SHOOT_PERCENT_SQL = "update playerStats set avgShootingPercent = ? where id = ?"; 

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
        System.out.printf(">>> Game Id retrieved: %s\n", gameId); 
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
    
    public boolean updateCap(String userId) {
        Integer rowsAffected = template.update(UPDATE_CAP_SQL, userId);
        if (rowsAffected == 1)
            return true; 
        return false;  
    }

    public boolean updateInterception(String userId, Integer intercepts) {
        // get avg intercept first 
        BigDecimal prevAvgInterceptionPerGame = template.queryForObject(GET_AVG_INTERCEPT_SQL, BigDecimal.class, userId); 
        Integer prevCap = template.queryForObject(GET_CAP_SQL, Integer.class, userId); // cap is updated last 
        
        // update with new interception
        if (prevCap == null) {
            Integer rowsAffected = template.update(UPDATE_INTERCEPT_SQL, intercepts, userId); // if start from null, just add first intercept stats
            if (rowsAffected > 0)
                return true; 
            return false; 
        }
        BigDecimal newAvgInterceptionPerGame = ((prevAvgInterceptionPerGame.multiply(BigDecimal.valueOf(prevCap)))
                                                .add(BigDecimal.valueOf(intercepts))).divide(BigDecimal.valueOf(prevCap+1));
        Integer rowsAffected = template.update(UPDATE_INTERCEPT_SQL, newAvgInterceptionPerGame, userId);
        if (rowsAffected > 0)
            return true;
        return false;
    }

    public boolean updateShootingPercentage(ShooterCount shooterCount) {
        String userId = profileRepo.getPlayerId(shooterCount.getName()); 

        // get prev shooting percent and cap
        BigDecimal prevAvgShootingPercent = template.queryForObject(GET_AVG_SHOOT_PERCENT_SQL, BigDecimal.class, userId);
        Integer prevCap = template.queryForObject(GET_CAP_SQL, Integer.class, userId); 

        // calc new shooting percent: ((prevAvg%*prevCap)+new%)/newCap
        BigDecimal newAvgShootingPercent = prevAvgShootingPercent.multiply(BigDecimal.valueOf(prevCap)).add(shooterCount.getShootingPercent()).divide(BigDecimal.valueOf(prevCap+1)); 

        // update SQL table (just the shooting %)
        Integer rowsAffected = template.update(UPDATE_SHOOT_PERCENT_SQL, newAvgShootingPercent, userId); 
        if (rowsAffected > 0)
            return true;
        return false; 
    }   
}
