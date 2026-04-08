package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberMembershipRepository {

    public void assignMembership(int memberId, int membershipId) {

        String checkSql = "SELECT COUNT(*) FROM member_memberships WHERE member_id = ?";
        String insertSql = "INSERT INTO member_memberships (member_id, membership_id) VALUES (?, ?)";
        String updateSql = "UPDATE member_memberships SET membership_id = ? WHERE member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, memberId);
            ResultSet rs = checkStmt.executeQuery();

            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

            if (!exists) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, memberId);
                    insertStmt.setInt(2, membershipId);
                    insertStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, membershipId);
                    updateStmt.setInt(2, memberId);
                    updateStmt.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getMembershipIdForMember(int memberId) {

        String sql = "SELECT membership_id FROM member_memberships WHERE member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("membership_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}