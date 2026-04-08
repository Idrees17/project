package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClassSessionRegistrationRepository {

    public void registerMember(int sessionId, int memberId) {

        String sql = "INSERT INTO class_registrations (session_id, member_id) VALUES (?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRegistered(int sessionId, int memberId) {

        String sql = "SELECT COUNT(*) FROM class_registrations WHERE session_id = ? AND member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
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

    public List<Integer> getRegisteredSessionIdsForMember(int memberId) {

        List<Integer> ids = new ArrayList<>();

        String sql = "SELECT session_id FROM class_registrations WHERE member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("session_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    public void unregisterMember(int sessionId, int memberId) {

        String sql = "DELETE FROM class_registrations WHERE session_id = ? AND member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRegistrationCount(int sessionId) {

        String sql = "SELECT COUNT(*) FROM class_registrations WHERE session_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}