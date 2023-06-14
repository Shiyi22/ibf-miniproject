package ibfbatch2miniproject.backend.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ibfbatch2miniproject.backend.model.Event;

@Repository
public class SQLCalendarRepository {

    @Autowired
    private JdbcTemplate template;

    private String GET_LIST_EVENTS_SQL = "select * from CalendarEvent"; 
    private String INSERT_EVENT_SQL = "insert into CalendarEvent values (?, ?, ?, ?, ?)";

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
    
}
