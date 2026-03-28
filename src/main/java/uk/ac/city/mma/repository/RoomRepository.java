package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.Room;

import java.sql.*;
import java.util.*;

public class RoomRepository {

    public void addRoom(Room room) {

        String sql = "INSERT INTO rooms (name, capacity) VALUES (?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, room.getName());
            stmt.setInt(2, room.getCapacity());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Room> getAllRooms() {

        List<Room> list = new ArrayList<>();

        String sql = "SELECT * FROM rooms";

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("room_id"),
                        rs.getString("name"),
                        rs.getInt("capacity")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void deleteRoom(int roomId) {

        String sql = "DELETE FROM rooms WHERE room_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Room getRoomById(int roomId) {

        String sql = "SELECT * FROM rooms WHERE room_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getInt("room_id"),
                        rs.getString("name"),
                        rs.getInt("capacity")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateRoom(int roomId, String name, int capacity) {

        String sql = "UPDATE rooms SET name = ?, capacity = ? WHERE room_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, capacity);
            stmt.setInt(3, roomId);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
