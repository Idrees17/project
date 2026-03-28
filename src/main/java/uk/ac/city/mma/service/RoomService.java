package uk.ac.city.mma.service;

import uk.ac.city.mma.model.Room;
import uk.ac.city.mma.repository.RoomRepository;

import java.util.*;

public class RoomService {

    private RoomRepository repo = new RoomRepository();

    public void addRoom(String name, int capacity) {
        repo.addRoom(new Room(0, name, capacity));
    }

    public List<Room> getAllRooms() {
        return repo.getAllRooms();
    }

    public void deleteRoom(int roomId) {
        repo.deleteRoom(roomId);
    }

    public Room getRoomById(int roomId) {
        return repo.getRoomById(roomId);
    }

    public void updateRoom(int roomId, String name, int capacity) {
        repo.updateRoom(roomId, name, capacity);
    }
}