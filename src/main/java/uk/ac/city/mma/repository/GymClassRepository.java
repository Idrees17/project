package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.GymClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GymClassRepository {

    public void createClass(GymClass gymClass) {

        String sql = "INSERT INTO classes (class_name, description, skill_level, class_type, capacity) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gymClass.getClassName());
            stmt.setString(2, gymClass.getDescription());
            stmt.setString(3, gymClass.getSkillLevel());
            stmt.setString(4, gymClass.getClassType());
            stmt.setInt(5, gymClass.getCapacity());

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
                        rs.getString("class_type"),
                        rs.getInt("capacity")
                );
                classes.add(gymClass);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public GymClass getClassById(int classId) {

        String sql = "SELECT * FROM classes WHERE class_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new GymClass(
                        rs.getInt("class_id"),
                        rs.getString("class_name"),
                        rs.getString("description"),
                        rs.getString("skill_level"),
                        rs.getString("class_type"),
                        rs.getInt("capacity")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateClass(GymClass gymClass) {

        String sql = "UPDATE classes SET class_name = ?, description = ?, skill_level = ?, class_type = ?, capacity = ? WHERE class_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gymClass.getClassName());
            stmt.setString(2, gymClass.getDescription());
            stmt.setString(3, gymClass.getSkillLevel());
            stmt.setString(4, gymClass.getClassType());
            stmt.setInt(5, gymClass.getCapacity());
            stmt.setInt(6, gymClass.getClassId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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