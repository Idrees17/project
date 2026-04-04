package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.MemberProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRegistrationRepository {

    public void registerMember(int eventId, int memberId) {

        String sql = "INSERT INTO event_registrations (event_id, member_id) VALUES (?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isMemberRegistered(int eventId, int memberId) {

        String sql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, memberId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<MemberProfile> getEntrantsForEvent(int eventId) {

        List<MemberProfile> entrants = new ArrayList<>();

        String sql = "SELECT mp.* FROM event_registrations er " +
                "JOIN member_profiles mp ON er.member_id = mp.member_id " +
                "WHERE er.event_id = ? " +
                "ORDER BY mp.last_name, mp.first_name";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                entrants.add(new MemberProfile(
                        rs.getInt("member_id"),
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("age"),
                        rs.getInt("height_cm"),
                        rs.getDouble("weight_kg"),
                        rs.getString("experience_level"),
                        rs.getString("preferred_martial_art")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return entrants;
    }

    public void deleteRegistrationsForEvent(int eventId) {

        String sql = "DELETE FROM event_registrations WHERE event_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}