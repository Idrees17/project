package uk.ac.city.mma.repository;

import uk.ac.city.mma.model.Coach;
import uk.ac.city.mma.config.MySQLConnection;

import java.sql.*;
import java.util.*;

public class CoachRepository {

    public void addCoach(Coach coach) {

        String sql = "INSERT INTO coaches (name, specialty) VALUES (?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, coach.getName());
            stmt.setString(2, coach.getSpecialty());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Coach> getAllCoaches() {

        List<Coach> list = new ArrayList<>();

        String sql = "SELECT * FROM coaches";

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Coach(
                        rs.getInt("coach_id"),
                        rs.getString("name"),
                        rs.getString("specialty")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void deleteCoach(int coachId) {

        String sql = "DELETE FROM coaches WHERE coach_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coachId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Coach getCoachById(int coachId) {

        String sql = "SELECT * FROM coaches WHERE coach_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coachId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Coach(
                        rs.getInt("coach_id"),
                        rs.getString("name"),
                        rs.getString("specialty")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateCoach(int coachId, String name, String specialty) {

        String sql = "UPDATE coaches SET name = ?, specialty = ? WHERE coach_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, specialty);
            stmt.setInt(3, coachId);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}