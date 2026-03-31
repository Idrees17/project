package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.MemberProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberProfileRepository {

    public MemberProfile getByUserId(int userId) {

        String sql = "SELECT * FROM member_profiles WHERE user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new MemberProfile(
                        rs.getInt("member_id"),
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("age"),
                        rs.getInt("height_cm"),
                        rs.getDouble("weight_kg"),
                        rs.getString("experience_level"),
                        rs.getString("preferred_martial_art")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void createProfile(MemberProfile profile) {

        String sql = "INSERT INTO member_profiles " +
                "(user_id, first_name, last_name, age, height_cm, weight_kg, experience_level, preferred_martial_art) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getFirstName());
            stmt.setString(3, profile.getLastName());
            stmt.setInt(4, profile.getAge());
            stmt.setInt(5, profile.getHeightCm());
            stmt.setDouble(6, profile.getWeightKg());
            stmt.setString(7, profile.getExperienceLevel());
            stmt.setString(8, profile.getPreferredMartialArt());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProfile(MemberProfile profile) {

        String sql = "UPDATE member_profiles SET " +
                "first_name = ?, last_name = ?, age = ?, height_cm = ?, weight_kg = ?, " +
                "experience_level = ?, preferred_martial_art = ? " +
                "WHERE user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getFirstName());
            stmt.setString(2, profile.getLastName());
            stmt.setInt(3, profile.getAge());
            stmt.setInt(4, profile.getHeightCm());
            stmt.setDouble(5, profile.getWeightKg());
            stmt.setString(6, profile.getExperienceLevel());
            stmt.setString(7, profile.getPreferredMartialArt());
            stmt.setInt(8, profile.getUserId());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsForUser(int userId) {

        String sql = "SELECT COUNT(*) FROM member_profiles WHERE user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}