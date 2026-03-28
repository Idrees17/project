package uk.ac.city.mma.model;

public class Room {

    private int roomId;
    private String name;
    private int capacity;

    public Room() {}

    public Room(int roomId, String name, int capacity) {
        this.roomId = roomId;
        this.name = name;
        this.capacity = capacity;
    }

    public int getRoomId() { return roomId; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }

    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setName(String name) { this.name = name; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}