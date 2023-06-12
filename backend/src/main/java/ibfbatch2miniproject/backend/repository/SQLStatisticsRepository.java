package ibfbatch2miniproject.backend.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ibfbatch2miniproject.backend.model.GameData;
import ibfbatch2miniproject.backend.model.PlayerProfile;
import ibfbatch2miniproject.backend.model.PlayerStats;
import ibfbatch2miniproject.backend.model.QuarterData;
import ibfbatch2miniproject.backend.model.ShooterCount;

@Repository
public class SQLStatisticsRepository {

    @Autowired
    private JdbcTemplate template; 
    
    @Autowired
    private SQLProfileRepository profileRepo; 

    private Integer gameId; 
    private Date lastUpdated; 

    private final String GET_LIST_PLAYER_PROFILES_SQL = "select id, name, playerPhoto from playerInfo"; 
    private final String GET_PLAYER_POSITIONS_SQL = "select position from playerPosition where id = ?"; 
    private final String SAVE_GAME_DATA_SQL = "insert into GameData (label, against, date) values (?, ?, ?)"; 
    private final String GET_GAME_DATA_LIST_SQL = "select * from GameData"; 
    private final String GET_GAME_DATA_BY_ID_SQL = "select * from GameData where game_id = ?";

    private final String SAVE_FULL_GAME_DATA_SQL = "insert into FullGameData (game_id, gs, ga, wa, c, wd, gd, gk, ownScore, oppScore, gaShotIn, gsShotIn, gaTotalShots, gsTotalShots, ownCpCount, oppCpCount, oppSelfError, goodTeamD, oppMissShot, interceptions, lostSelfError, lostByIntercept, quarterSequence) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String GET_FULL_GAME_DATA_SQL = "select * from FullGameData where game_id = ?"; 

    private final String UPDATE_CAP_SQL = "update playerStats set cap = COALESCE(cap, 0) + 1, lastUpdated = ? where id = ?"; // FIX
    private final String GET_AVG_INTERCEPT_SQL = "select avgInterceptionPerGame from playerStats where id = ?";
    private final String GET_CAP_SQL = "select cap from playerStats where id = ?";
    private final String UPDATE_INTERCEPT_SQL = "update playerStats set avgInterceptionPerGame = ? where id = ?"; 
    private final String GET_AVG_SHOOT_PERCENT_SQL = "select avgShootingPercent from playerStats where id = ?";
    private final String UPDATE_SHOOT_PERCENT_SQL = "update playerStats set avgShootingPercent = ? where id = ?"; 
    private final String GET_INDV_STATS_SQL = "select * from playerStats where id = ?"; 

    private final String GET_LIST_OF_PHOTOURL_SQL = "select photoUrl from GameData where game_id = ?"; 
    private final String GET_LIST_OF_VIDEOURL_SQL = "select videoUrl from GameData where game_id = ?";
    private final String ADD_PHOTOURL_SQL = "update GameData set photoUrl = ? where game_id = ?"; 
    private final String ADD_VIDEOURL_SQL = "update GameData set videoUrl = ? where game_id = ?"; 

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
        lastUpdated = gameData.getDate(); 
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
    
    public boolean updateCapAndDate(String userId) {
        Integer rowsAffected = template.update(UPDATE_CAP_SQL, lastUpdated, userId);
        if (rowsAffected == 1)
            return true; 
        return false;  
    }

    public boolean updateInterception(String userId, Integer intercepts) {
        // get avg intercept first 
        BigDecimal prevAvgInterceptionPerGame = template.queryForObject(GET_AVG_INTERCEPT_SQL, BigDecimal.class, userId); 
        Integer prevCap = template.queryForObject(GET_CAP_SQL, Integer.class, userId); // cap is updated last 
        
        // update with new interception
        if (prevCap == null || prevAvgInterceptionPerGame == null) {
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
        if (prevCap == null) {
            Integer rowsAffected = template.update(UPDATE_SHOOT_PERCENT_SQL, shooterCount.getShootingPercent(), userId);
            if (rowsAffected > 0)
                return true; 
            return false; 
        }
        // else prevCap and shooting % is not null 
        BigDecimal newAvgShootingPercent = prevAvgShootingPercent.multiply(BigDecimal.valueOf(prevCap)).add(shooterCount.getShootingPercent()).divide(BigDecimal.valueOf(prevCap+1), 2, RoundingMode.HALF_UP); 

        // update SQL table (just the shooting %)
        Integer rowsAffected = template.update(UPDATE_SHOOT_PERCENT_SQL, newAvgShootingPercent, userId); 
        if (rowsAffected > 0)
            return true;
        return false; 
    }   

    public Optional<List<GameData>> getGameDataList() {
        List<GameData> list = template.query(GET_GAME_DATA_LIST_SQL, BeanPropertyRowMapper.newInstance(GameData.class)); 
        if (list == null)
            return Optional.empty();
        return Optional.of(list);  
    }

    public GameData getGameDataById(Integer gameId) {
        return template.query(GET_GAME_DATA_BY_ID_SQL, new ResultSetExtractor<GameData>() {

            @Override
            public GameData extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    GameData game = new GameData(); 
                    game.setGame_id(rs.getInt("game_id"));
                    game.setLabel(rs.getString("label"));
                    game.setAgainst(rs.getString("against"));
                    game.setDate(rs.getDate("date"));

                    String photoUrlString = rs.getString("photoUrl"); 
                    List<String> photoUrl = new ArrayList<>(); 
                    if (photoUrlString != null) {
                        String[] photoUrls = photoUrlString.split(", ");
                        for (String url : photoUrls)
                            photoUrl.add(url);
                    }
                    game.setPhotoUrl(photoUrl);

                    String videoUrlString = rs.getString("videoUrl"); 
                    List<String> videoUrl = new ArrayList<>(); 
                    if (videoUrlString != null) {
                        String[] videoUrls = videoUrlString.split(", ");
                        for (String url : videoUrls)
                            videoUrl.add(url);
                    }
                    game.setVideoUrl(videoUrl);
                    return game; 
                } else {
                    return null; 
                }
            }
        }, gameId);
    }

    public List<QuarterData> getFullGameData(Integer gameId) {
        return template.query(GET_FULL_GAME_DATA_SQL, new ResultSetExtractor<List<QuarterData>>() {

            @Override
            public List<QuarterData> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<QuarterData> fullGameData = new ArrayList<>(); 
                while (rs.next()) {
                    QuarterData qtr = new QuarterData(); 

                    // retrieve player pos 
                    qtr.setGs(rs.getString("gs"));
                    qtr.setGa(rs.getString("ga"));
                    qtr.setWa(rs.getString("wa"));
                    qtr.setC(rs.getString("c"));
                    qtr.setWd(rs.getString("wd"));
                    qtr.setGd(rs.getString("gd"));
                    qtr.setGk(rs.getString("gk"));

                    // retrieve stats 
                    qtr.setOwnScore(rs.getInt("ownScore"));
                    qtr.setOppScore(rs.getInt("oppScore"));
                    qtr.setGaShotIn(rs.getInt("gaShotIn"));
                    qtr.setGsShotIn(rs.getInt("gsShotIn"));
                    qtr.setGaTotalShots(rs.getInt("gaTotalShots"));
                    qtr.setGsTotalShots(rs.getInt("gsTotalShots"));
                    qtr.setOwnCpCount(rs.getInt("ownCpCount"));
                    qtr.setOppCpCount(rs.getInt("oppCpCount"));
                    qtr.setOppSelfError(rs.getInt("oppSelfError"));
                    qtr.setGoodTeamD(rs.getInt("goodTeamD"));
                    qtr.setOppMissShot(rs.getInt("oppMissShot"));
                    qtr.setLostSelfError(rs.getInt("lostSelfError"));
                    qtr.setLostByIntercept(rs.getInt("lostByIntercept"));

                    // retrieve interceptions as Map<String, Integer>
                    String interceptionsJson = rs.getString("interceptions");
                     ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        Map<String, Integer> interceptions = objectMapper.readValue(interceptionsJson, new TypeReference<Map<String, Integer>>() {});
                        qtr.setInterceptions(interceptions);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    // retrieve quarter sequence as String[][]
                    String quarterSequenceJson = rs.getString("quarterSequence");
                    try {
                        String[][] quarterSequence = objectMapper.readValue(quarterSequenceJson, String[][].class);
                        qtr.setQuarterSequence(quarterSequence);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    // Add qtr to list 
                    fullGameData.add(qtr);
                }
                return fullGameData; 
            }
        }, gameId);
    }

    // get player stats 
    public PlayerStats getPlayerStats(String userId) {
        return template.queryForObject(GET_INDV_STATS_SQL, BeanPropertyRowMapper.newInstance(PlayerStats.class), userId);
    }

    // update s3url to List<String> 
    public boolean updateS3UrlToSql(String s3Url, String mediaTypeToUpload, Integer gameId) {
        // get the existing list of url 
        String UrlsRetrieved = "nil"; 
        if (mediaTypeToUpload == "photo")
            UrlsRetrieved = template.queryForObject(GET_LIST_OF_PHOTOURL_SQL, String.class, gameId);
        else if (mediaTypeToUpload == "video") 
            UrlsRetrieved = template.queryForObject(GET_LIST_OF_VIDEOURL_SQL, String.class, gameId);

        if (UrlsRetrieved.equals("nil")) {
            // add the first url to the list 
            Integer rowsAffected = 0; 
            if (mediaTypeToUpload.equals("photo")) {
                System.out.printf(">>> Url retrieved = null and mediatype to upload is photo!");
                rowsAffected = template.update(ADD_PHOTOURL_SQL, s3Url, gameId);
            } else if (mediaTypeToUpload.equals("video")) {
                rowsAffected = template.update(ADD_VIDEOURL_SQL, s3Url, gameId);
            }
            if (rowsAffected > 0)
                return true;
            return false; 
        } else {
            System.out.printf(">>> Url retrieved is not empty");
            String newUrlToAdd = UrlsRetrieved + ", " + s3Url;
            Integer rowsAffected = 0;
            if (mediaTypeToUpload == "photo")
                template.update(ADD_PHOTOURL_SQL, newUrlToAdd, gameId);
            else if (mediaTypeToUpload == "video") 
                template.update(ADD_VIDEOURL_SQL, newUrlToAdd, gameId);
            
            if (rowsAffected > 0)
                return true;
            return false; 
        }
    }

}
