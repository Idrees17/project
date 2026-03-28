package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public User findByUsernameAndPassword(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        User.Role.valueOf(rs.getString("role"))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
