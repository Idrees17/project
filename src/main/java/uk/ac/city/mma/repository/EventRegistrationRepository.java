package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.EventRegistration;
import uk.ac.city.mma.model.MemberProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRegistrationRepository {

    public void registerMember(int eventId, int memberId, String chosenMartialArt, String experienceLevel) {

        String sql = "INSERT INTO event_registrations (event_id, member_id, chosen_martial_art, experience_level) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, memberId);
            stmt.setString(3, chosenMartialArt);
            stmt.setString(4, experienceLevel);

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
                        rs.getDouble("weight_kg")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return entrants;
    }

    public List<EventRegistration> getRegistrationsForEvent(int eventId) {

        List<EventRegistration> registrations = new ArrayList<>();

        String sql = "SELECT * FROM event_registrations WHERE event_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                registrations.add(new EventRegistration(
                        rs.getInt("registration_id"),
                        rs.getInt("event_id"),
                        rs.getInt("member_id"),
                        rs.getString("chosen_martial_art"),
                        rs.getString("experience_level"),
                        rs.getString("registered_at")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return registrations;
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

    public List<Integer> getRegisteredEventIdsForMember(int memberId) {

        List<Integer> eventIds = new ArrayList<>();

        String sql = "SELECT event_id FROM event_registrations WHERE member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                eventIds.add(rs.getInt("event_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return eventIds;
    }
}