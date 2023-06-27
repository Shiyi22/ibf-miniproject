package ibfbatch2miniproject.backend.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import ibfbatch2miniproject.backend.model.Login;
import ibfbatch2miniproject.backend.model.PlayerInfo;
import ibfbatch2miniproject.backend.model.PlayerInfo2;

@Repository
public class SQLProfileRepository {

    @Autowired
    private JdbcTemplate template;

    private final String GET_CREDS_SQL = "select * from logincreds where username = ?";
    private final String SAVE_CREDS_SQL = "insert into logincreds values (?, ?, ?)";
    private final String GET_ID_SQL = "select id from playerInfo where name = ?";
    private final String GET_PLAYER_INFO_SQL = "select * from playerInfo where id = ?";
    private final String GET_PLAYER_POSITION_SQL = "select position from playerPosition where id = ?";
    private final String SAVE_PLAYER_INFO_SQL = "insert into playerInfo values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
    private final String UPDATE_PLAYER_INFO_SQL = """
        update playerInfo set name = ?, weight = ?, height = ?, email = ?, phoneNumber = ?, DOB = ?, emergencyContact = ?, 
        emergencyName = ?, address = ?, pastInjuries = ?, role = ?, yearJoined = ? where id = ?
            """;
    private final String DELETE_PLAYER_POSITION_SQL = "delete from playerPosition where id = ?"; 
    private final String INSERT_PLAYER_POSITION_SQL = "insert into playerPosition (id, position) values (?, ?)"; 
    private final String UPDATE_PLAYER_PHOTO_SQL = "update playerInfo set playerPhoto = ? where id = ?"; 
    private final String INSERT_PLAYER_STATS_SQL = "insert into playerStats (id) values (?)";
    
    // save user credentials, use hash algo to store hashed passwords eg. bcrypt
    public boolean saveLoginCreds(Login signup) {
        String idToAdd = UUID.randomUUID().toString().substring(0, 8);
        Integer rowsAffected = template.update(SAVE_CREDS_SQL, idToAdd, signup.getUsername(), signup.getPassword());
        if (rowsAffected > 0) 
            return true;
        return false; 
    }

    // get login credentials 
    public Optional<Boolean> getLoginCreds(Login login) {
        try {
            Login fromSQL = template.queryForObject(GET_CREDS_SQL, BeanPropertyRowMapper.newInstance(Login.class), login.getUsername());
            // if creds are valid 
            if (login.getPassword().equals(fromSQL.getPassword()))
                return Optional.of(true); 
            // if password is invalid
            return Optional.of(false);   

        } catch (EmptyResultDataAccessException ex) {
            // ex.printStackTrace();
            return Optional.empty(); 
        }
    }

    // get user id from username (there will definitely be this user since already signed in)
    public String getPlayerId(String username) {
        return template.queryForObject(GET_ID_SQL, String.class, username);
    }

    // get player info from userId
    public PlayerInfo getPlayerInfo(String userId) {

        AtomicReference<PlayerInfo> infoWrapper = new AtomicReference<>(new PlayerInfo());

        // get info with photo 
        // Previously: PlayerInfo info = template.queryForObject(GET_PLAYER_INFO_SQL, BeanPropertyRowMapper.newInstance(PlayerInfo.class), userId);
        template.query(GET_PLAYER_INFO_SQL, (ResultSet rs) -> {
            infoWrapper.set(PlayerInfo.populate(rs)); 
        }, userId);
        
        PlayerInfo info = infoWrapper.get();
        // get list of positions and add to playerinfo
        List<String> positions = template.queryForList(GET_PLAYER_POSITION_SQL, String.class, userId); 
        info.setPositions(positions);

        // TODO: get list of block out dates 

        return info; 
    }

    // save player info 
    public boolean savePlayerInfo(PlayerInfo2 info, String userId) throws IOException {
        // change photo from encoded string to InputStream
        InputStream is = info.getPlayerPhoto().getInputStream();

        // change string date to date 
        Date dob = Date.valueOf(info.getDob());

        // add to player info table 
        Integer rowsAffected = template.update(SAVE_PLAYER_INFO_SQL, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, userId);
                ps.setString(2, info.getName());
                ps.setInt(3, info.getWeight());
                ps.setInt(4, info.getHeight());
                ps.setBinaryStream(5, is, info.getPlayerPhoto().getSize());
                ps.setString(6, info.getEmail());
                ps.setInt(7, info.getPhoneNumber());
                ps.setDate(8, dob);
                ps.setInt(9, info.getEmergencyContact());
                ps.setString(10, info.getEmergencyName());
                ps.setString(11, info.getAddress());
                ps.setString(12, info.getPastInjuries());
                ps.setString(13, info.getRole());
                ps.setInt(14, info.getYearJoined());
            }            
        }); 
    
        // add to positions table 
        int[] arrAffected = template.batchUpdate(INSERT_PLAYER_POSITION_SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, userId);
                ps.setString(2, info.getPositions().get(i));
            }
            @Override
            public int getBatchSize() {
                return info.getPositions().size();
            }
        });

        // add to player stats table 
        Integer rowsAffected2 = template.update(INSERT_PLAYER_STATS_SQL, userId);

        // return boolean 
        if (rowsAffected > 0 && arrAffected.length > 0 && rowsAffected2 > 0) 
            return true; 
        return false;
    
    }

    // update player info 
    public boolean updatePlayerInfo(PlayerInfo info, String userId) {
        Integer rowsAffected1 =  template.update(UPDATE_PLAYER_INFO_SQL, info.getName(), info.getWeight(), info.getHeight(), info.getEmail(), info.getPhoneNumber(),
        info.getDob(), info.getEmergencyContact(), info.getEmergencyName(), info.getAddress(), info.getPastInjuries(), info.getRole(), info.getYearJoined(), userId);
   
        // delete previously saved positions, then add new positions 
        Integer rowsAffected2 = template.update(DELETE_PLAYER_POSITION_SQL, userId); 

        // Positions comes in as 
        // batch update positions
        int[] arrAffected = template.batchUpdate(INSERT_PLAYER_POSITION_SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, userId);
                ps.setString(2, info.getPositions().get(i));
            }
            @Override
            public int getBatchSize() {
                return info.getPositions().size();
            }
        });
        if (rowsAffected1 > 0 || rowsAffected2 > 0 || arrAffected.length > 0)
            return true;
        return false; 
    }

    // upload player photo
    public boolean updatePhoto(MultipartFile picture, String userId) throws IOException {
        Integer rowsAffected = template.update(UPDATE_PLAYER_PHOTO_SQL, new PreparedStatementSetter() {

            InputStream is = picture.getInputStream(); 
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setBinaryStream(1, is, picture.getSize());
                ps.setString(2, userId);
            }            
        }); 
        if (rowsAffected > 0)
            return true; 
        return false; 
    }

    
}
