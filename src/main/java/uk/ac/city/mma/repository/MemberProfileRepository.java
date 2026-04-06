package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.MemberProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
                        rs.getDouble("weight_kg")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void createProfile(MemberProfile profile) {

        String sql = "INSERT INTO member_profiles " +
                "(user_id, first_name, last_name, age, height_cm, weight_kg) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getFirstName());
            stmt.setString(3, profile.getLastName());
            stmt.setInt(4, profile.getAge());
            stmt.setInt(5, profile.getHeightCm());
            stmt.setDouble(6, profile.getWeightKg());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProfile(MemberProfile profile) {

        String sql = "UPDATE member_profiles SET " +
                "first_name = ?, last_name = ?, age = ?, height_cm = ?, weight_kg = ? " +
                "WHERE user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getFirstName());
            stmt.setString(2, profile.getLastName());
            stmt.setInt(3, profile.getAge());
            stmt.setInt(4, profile.getHeightCm());
            stmt.setDouble(5, profile.getWeightKg());
            stmt.setInt(6, profile.getUserId());

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

    public List<MemberProfile> getAllProfiles() {

        List<MemberProfile> profiles = new ArrayList<>();

        String sql = "SELECT * FROM member_profiles ORDER BY last_name, first_name";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                profiles.add(new MemberProfile(
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

        return profiles;
    }

    public MemberProfile getProfileByMemberId(int memberId) {

        String sql = "SELECT * FROM member_profiles WHERE member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new MemberProfile(
                        rs.getInt("member_id"),
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("age"),
                        rs.getInt("height_cm"),
                        rs.getDouble("weight_kg")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteProfile(int memberId) {

        String sql = "DELETE FROM member_profiles WHERE member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProfileByMemberId(MemberProfile profile) {

        String sql = "UPDATE member_profiles SET " +
                "first_name = ?, last_name = ?, age = ?, height_cm = ?, weight_kg = ? " +
                "WHERE member_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getFirstName());
            stmt.setString(2, profile.getLastName());
            stmt.setInt(3, profile.getAge());
            stmt.setInt(4, profile.getHeightCm());
            stmt.setDouble(5, profile.getWeightKg());
            stmt.setInt(6, profile.getMemberId());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}