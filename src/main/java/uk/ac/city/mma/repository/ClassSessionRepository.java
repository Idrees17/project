package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.ClassSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassSessionRepository {

    public void createSession(ClassSession session) {

        String sql = "INSERT INTO class_sessions " +
                "(class_id, day_of_week, start_time, duration_minutes, coach_name, room, is_generated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, session.getClassId());
            stmt.setString(2, session.getDayOfWeek());
            stmt.setString(3, session.getStartTime());
            stmt.setInt(4, session.getDurationMinutes());
            stmt.setString(5, session.getCoachName());
            stmt.setString(6, session.getRoom());
            stmt.setBoolean(7, session.isGenerated());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ClassSession> getSessionsByClass(int classId) {

        List<ClassSession> sessions = new ArrayList<>();

        String sql = "SELECT cs.*, c.class_name \n" +
                "FROM class_sessions cs\n" +
                "JOIN classes c ON cs.class_id = c.class_id\n" +
                "WHERE cs.class_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                ClassSession session = new ClassSession(
                        rs.getInt("session_id"),
                        rs.getInt("class_id"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getInt("duration_minutes"),
                        rs.getString("coach_name"),
                        rs.getString("room")
                );

                sessions.add(session);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public List<ClassSession> getAllSessions() {

        List<ClassSession> sessions = new ArrayList<>();

        String sql = "SELECT cs.*, c.class_name FROM class_sessions cs " +
                "JOIN classes c ON cs.class_id = c.class_id " +
                "ORDER BY " +
                "CASE cs.day_of_week " +
                "WHEN 'Monday' THEN 1 " +
                "WHEN 'Tuesday' THEN 2 " +
                "WHEN 'Wednesday' THEN 3 " +
                "WHEN 'Thursday' THEN 4 " +
                "WHEN 'Friday' THEN 5 " +
                "WHEN 'Saturday' THEN 6 " +
                "WHEN 'Sunday' THEN 7 " +
                "ELSE 8 END, " +
                "cs.start_time";

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                ClassSession session = new ClassSession(
                        rs.getInt("session_id"),
                        rs.getInt("class_id"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getInt("duration_minutes"),
                        rs.getString("coach_name"),
                        rs.getString("room")
                );

                session.setClassName(rs.getString("class_name"));
                session.setGenerated(rs.getBoolean("is_generated"));

                sessions.add(session);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public List<ClassSession> getSessionsByDay(String day) {

        List<ClassSession> sessions = new ArrayList<>();

        String sql = "SELECT cs.*, c.class_name FROM class_sessions cs " +
                "JOIN classes c ON cs.class_id = c.class_id " +
                "WHERE cs.day_of_week = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, day);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ClassSession session = new ClassSession(
                        rs.getInt("session_id"),
                        rs.getInt("class_id"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getInt("duration_minutes"),
                        rs.getString("coach_name"),
                        rs.getString("room")
                );

                session.setClassName(rs.getString("class_name"));
                session.setGenerated(rs.getBoolean("is_generated"));
                sessions.add(session);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public void deleteSession(int sessionId){

        String sql = "DELETE FROM class_sessions WHERE session_id = ?";

        try(Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1,sessionId);
            stmt.executeUpdate();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateSession(int id, String day, String time,
                              int durationMinutes, String coach, String room) {

        String sql = "UPDATE class_sessions " +
                "SET day_of_week=?, start_time=?, duration_minutes=?, coach_name=?, room=? " +
                "WHERE session_id=?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, day);
            stmt.setString(2, time);
            stmt.setInt(3, durationMinutes);
            stmt.setString(4, coach);
            stmt.setString(5, room);
            stmt.setInt(6, id);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsAtTime(String day, String time) {

        String sql = "SELECT COUNT(*) FROM class_sessions WHERE day_of_week=? AND start_time=?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, day);
            stmt.setString(2, time);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public void deleteGeneratedSessions() {

        String sql = "DELETE FROM class_sessions WHERE is_generated = TRUE";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}