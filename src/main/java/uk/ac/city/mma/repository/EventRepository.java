package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    public void createEvent(Event event) {

        String sql = "INSERT INTO events (event_name, event_date, location, status, format, allowed_martial_arts) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getEventDate());
            stmt.setString(3, event.getLocation());
            stmt.setString(4, event.getStatus());
            stmt.setString(5, event.getFormat());
            stmt.setString(6, event.getAllowedMartialArts());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Event> getAllEvents() {

        List<Event> events = new ArrayList<>();

        String sql = "SELECT * FROM events ORDER BY event_date";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getString("event_date"),
                        rs.getString("location"),
                        rs.getString("status"),
                        rs.getString("allowed_martial_arts")
                ));
                events.get(events.size() - 1).setFormat(rs.getString("format"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    public Event getEventById(int eventId) {

        String sql = "SELECT * FROM events WHERE event_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Event event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getString("event_date"),
                        rs.getString("location"),
                        rs.getString("status"),
                        rs.getString("allowed_martial_arts")
                );
                event.setFormat(rs.getString("format"));
                return event;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateEvent(Event event) {

        String sql = "UPDATE events SET event_name = ?, event_date = ?, location = ?, status = ?, format = ?, allowed_martial_arts = ? WHERE event_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getEventDate());
            stmt.setString(3, event.getLocation());
            stmt.setString(4, event.getStatus());
            stmt.setString(5, event.getFormat());
            stmt.setString(6, event.getAllowedMartialArts());
            stmt.setInt(7, event.getEventId());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(int eventId) {

        String sql = "DELETE FROM events WHERE event_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}