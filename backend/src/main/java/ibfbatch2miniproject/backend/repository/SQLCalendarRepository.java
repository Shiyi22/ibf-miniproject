package ibfbatch2miniproject.backend.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ibfbatch2miniproject.backend.model.Event;
import ibfbatch2miniproject.backend.model.Notif;

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
}
