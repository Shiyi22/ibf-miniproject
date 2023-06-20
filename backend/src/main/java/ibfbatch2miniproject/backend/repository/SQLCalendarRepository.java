package ibfbatch2miniproject.backend.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ibfbatch2miniproject.backend.model.Event;
import ibfbatch2miniproject.backend.model.EventData;
import ibfbatch2miniproject.backend.model.Notif;
import ibfbatch2miniproject.backend.model.PlayerProfile;
import ibfbatch2miniproject.backend.model.TeamFund;

@Repository
public class SQLCalendarRepository {

    @Autowired
    private JdbcTemplate template;

    private String GET_LIST_EVENTS_SQL = "select * from CalendarEvent"; 
    private String GET_LIST_NOTIF_SQL = "select * from Notifications";
    private String INSERT_EVENT_SQL = "insert into CalendarEvent values (?, ?, ?, ?, ?)";
    private String INSERT_NOTIF_SQL = "insert into Notifications (imageUrl, name, action, date) values (?, ?, ?, ?)";
    private String DELETE_EVENT_SQL = "delete from CalendarEvent where eventId = ?"; 
    private String INSERT_EMAIL_SQL = "insert into emailTable (email) values (?)"; 
    private String GET_EMAIL_LIST_SQL = "select email from emailTable";
    private String GET_TEAM_FUNDS_LIST_SQL = "select * from teamFunds";
    private String DELETE_FUNDS_LIST_SQL = "delete from teamFunds"; 
    private String REPOPULATE_FUNDS_LIST_SQL = "insert into teamFunds values (?, ?, ?)";
    private String UPDATE_FUNDS_LIST_SQL = "update teamFunds set paid = true where id = ?"; 
    private String SAVE_ATTENDANCE_SQL = "insert into eventResponse (userId, eventId, attending) values (?, ?, ?)"; 

    // retrieve list of events 
    public List<Event> getCalEvents() {
        return template.query(GET_LIST_EVENTS_SQL, BeanPropertyRowMapper.newInstance(Event.class)); 
    } 

    // store event to DB
    public boolean storeEventToDB(Event event) {
        Integer rowsAffected = template.update(INSERT_EVENT_SQL, event.getEventId(), event.getSelectedDate(), event.getTitle(), event.getStartTime(), event.getEndTime());
        if (rowsAffected > 0)
            return true; 
        return false; 
    }

    // remove event from DB
    public boolean removeEvent(String eventId) {
        Integer rowsAffected = template.update(DELETE_EVENT_SQL, eventId);
        if (rowsAffected > 0)
            return true; 
        return false; 
    }

    // save notification 
    public boolean saveNotif(Notif notif) {
        Integer rowsAffected = template.update(INSERT_NOTIF_SQL, notif.getImageUrl(), notif.getName(), notif.getAction(), notif.getDate());
        if (rowsAffected > 0)
            return true;
        return false; 
    }
    
    // get notifs 
    public List<Notif> getNotif() {
        return template.query(GET_LIST_NOTIF_SQL, BeanPropertyRowMapper.newInstance(Notif.class)); 
    }

    // save email to new member sign up list 
    public boolean saveEmailToList(String email) {
        Integer rowsAffected = template.update(INSERT_EMAIL_SQL, email);
        if (rowsAffected > 0)
            return true; 
        return false; 
    }

    // get approved email list 
    public List<String> getApprovedEmailList() {
        return template.queryForList(GET_EMAIL_LIST_SQL, String.class); 
    }

    // get team funds list 
    public List<TeamFund> getTeamFunds() {
        return template.query(GET_TEAM_FUNDS_LIST_SQL, BeanPropertyRowMapper.newInstance(TeamFund.class)); 
    }

    // repopulate funds list
    public boolean repopulateList(PlayerProfile[] players) {
        // delete list 
        Integer rowsAffected = template.update(DELETE_FUNDS_LIST_SQL);
    
        // batch update
        int[] arrAffected = template.batchUpdate(REPOPULATE_FUNDS_LIST_SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, players[i].getId());
                ps.setString(2, players[i].getName());
                ps.setBoolean(3, false);
            }

            @Override
            public int getBatchSize() {
                return players.length; 
            }
        });
        
        if (rowsAffected > 0 && arrAffected.length > 0)
            return true;
        return false;
    }

    // update team fund
    public boolean updateFund(TeamFund tf) {
        Integer rowsAffected = template.update(UPDATE_FUNDS_LIST_SQL, tf.getId());
        if (rowsAffected > 0)
            return true; 
        return false; 
    }

    // save attendance
    public boolean saveAttendance(EventData data, String userId) {

        int[] arrAffected = template.batchUpdate(SAVE_ATTENDANCE_SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, userId);
                ps.setString(2, data.getResults().get(i).getEventId());
                ps.setString(3, data.getResults().get(i).getResponse());
            }

            @Override
            public int getBatchSize() {
                return data.getResults().size();
            }
        });
        
        if (arrAffected.length > 0)
            return true;
        return false; 
    }

}
