package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.Membership;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipRepository {

    public void createMembership(Membership membership) {

        String sql = "INSERT INTO memberships (membership_name, description, allowed_martial_arts, allowed_skill_levels) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, membership.getMembershipName());
            stmt.setString(2, membership.getDescription());
            stmt.setString(3, membership.getAllowedMartialArts());
            stmt.setString(4, membership.getAllowedSkillLevels());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Membership> getAllMemberships() {

        List<Membership> memberships = new ArrayList<>();

        String sql = "SELECT * FROM memberships ORDER BY membership_name";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                memberships.add(new Membership(
                        rs.getInt("membership_id"),
                        rs.getString("membership_name"),
                        rs.getString("description"),
                        rs.getString("allowed_martial_arts"),
                        rs.getString("allowed_skill_levels")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return memberships;
    }

    public Membership getMembershipById(int membershipId) {

        String sql = "SELECT * FROM memberships WHERE membership_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membershipId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Membership(
                        rs.getInt("membership_id"),
                        rs.getString("membership_name"),
                        rs.getString("description"),
                        rs.getString("allowed_martial_arts"),
                        rs.getString("allowed_skill_levels")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateMembership(Membership membership) {

        String sql = "UPDATE memberships SET membership_name = ?, description = ?, allowed_martial_arts = ?, allowed_skill_levels = ? WHERE membership_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, membership.getMembershipName());
            stmt.setString(2, membership.getDescription());
            stmt.setString(3, membership.getAllowedMartialArts());
            stmt.setString(4, membership.getAllowedSkillLevels());
            stmt.setInt(5, membership.getMembershipId());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMembership(int membershipId) {

        String sql = "DELETE FROM memberships WHERE membership_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membershipId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}