package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.MemberProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClassSessionRegistrationRepository {

    public void registerMember(int sessionId, int memberId, String weekStartDate) {

        String sql = "INSERT INTO class_registrations (session_id, member_id, week_start_date) VALUES (?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            stmt.setInt(2, memberId);
            stmt.setString(3, weekStartDate);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterMember(int sessionId, int memberId, String weekStartDate) {

        String sql = "DELETE FROM class_registrations WHERE session_id = ? AND member_id = ? AND week_start_date = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            stmt.setInt(2, memberId);
            stmt.setString(3, weekStartDate);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRegistered(int sessionId, int memberId, String weekStartDate) {

        String sql = "SELECT COUNT(*) FROM class_registrations WHERE session_id = ? AND member_id = ? AND week_start_date = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            stmt.setInt(2, memberId);
            stmt.setString(3, weekStartDate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Integer> getRegisteredSessionIdsForMemberAndWeek(int memberId, String weekStartDate) {

        List<Integer> ids = new ArrayList<>();

        String sql = "SELECT session_id FROM class_registrations WHERE member_id = ? AND week_start_date = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setString(2, weekStartDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) ids.add(rs.getInt("session_id"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    public int getRegistrationCountForWeek(int sessionId, String weekStartDate) {

        String sql = "SELECT COUNT(*) FROM class_registrations WHERE session_id = ? AND week_start_date = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            stmt.setString(2, weekStartDate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<MemberProfile> getRegistrantsForSessionAndWeek(int sessionId, String weekStartDate) {

        List<MemberProfile> members = new ArrayList<>();

        String sql = "SELECT mp.* FROM class_registrations cr " +
                "JOIN member_profiles mp ON cr.member_id = mp.member_id " +
                "WHERE cr.session_id = ? AND cr.week_start_date = ? " +
                "ORDER BY mp.last_name, mp.first_name";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            stmt.setString(2, weekStartDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                members.add(new MemberProfile(
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

        return members;
    }
}