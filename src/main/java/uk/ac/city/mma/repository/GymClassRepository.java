package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.GymClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GymClassRepository {

    public void createClass(GymClass gymClass) {

        String sql = "INSERT INTO classes (class_name, description, skill_level, capacity) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gymClass.getClassName());
            stmt.setString(2, gymClass.getDescription());
            stmt.setString(3, gymClass.getSkillLevel());
            stmt.setInt(4, gymClass.getCapacity());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public List<GymClass> getAllClasses() {

        List<GymClass> classes = new ArrayList<>();

        String sql = "SELECT * FROM classes";

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                GymClass gymClass = new GymClass(
                        rs.getInt("class_id"),
                        rs.getString("class_name"),
                        rs.getString("description"),
                        rs.getString("skill_level"),
                        rs.getInt("capacity")
                );

                classes.add(gymClass);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public void deleteClass(int classId) {

        String sql = "DELETE FROM classes WHERE class_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}