package ibfbatch2miniproject.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ibfbatch2miniproject.backend.model.Login;
import ibfbatch2miniproject.backend.model.PlayerInfo;

@Repository
public class SQLRepository {

    @Autowired
    private JdbcTemplate template;

    private final String GET_CREDS_SQL = "select * from logincreds where username = ?";
    private final String GET_ID_SQL = "select id from logincreds where username = ?";
    private final String GET_PLAYER_INFO_SQL = "select * from playerInfo where id = ?";
    private final String GET_PLAYER_POSITION_SQL = "select position from playerPosition where id = ?";
    
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
        PlayerInfo info = template.queryForObject(GET_PLAYER_INFO_SQL, BeanPropertyRowMapper.newInstance(PlayerInfo.class), userId);
        // get list of positions and add to playerinfo
        List<String> positions = template.queryForList(GET_PLAYER_POSITION_SQL, String.class, userId); 
        info.setPositions(positions);
        // TODO: get list of block out dates 

        return info; 
    }

    // save user credentials, use hash algo to store hashed passwords eg. bcrypt
    // create a login cred 8 digit id as well (UUID)
    public void saveLoginCreds(Login login) {

    }

    
}
