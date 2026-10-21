package vn.edu.ptit.oop.nhom01.model;
import java.util.*;

public abstract class Room {
    private String roomNumber;
    private int floor;
    private RoomStatus status;
    private boolean cleaningRequested;

    public Room(String roomNumber, int floor, RoomStatus status) {
        setRoomNumber(roomNumber);
        setFloor(floor);
        setStatus(status);
        this.cleaningRequested = false;
    }

    public abstract double getPricePerNight();
    public abstract String getAmenities();
    public abstract String getRoomTypeName();
    public abstract int getMaxOccupancy();

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty())
            throw new IllegalArgumentException("Số phòng không được rỗng!");
        this.roomNumber = roomNumber.trim();
    }

    public int getFloor() { return floor; }
    public void setFloor(int floor) {
        if (floor < 1 || floor > 50) throw new IllegalArgumentException("Tầng phải từ 1 đến 50!");
        this.floor = floor;
    }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = Objects.requireNonNull(status); }

    public boolean isCleaningRequested() { return cleaningRequested; }
    public void setCleaningRequested(boolean cleaningRequested) {
        this.cleaningRequested = cleaningRequested;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(roomNumber, room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }

    @Override
    public String toString() {
        String cleanTag = cleaningRequested ? " - Cần dọn phòng" : "";
        return String.format("Phòng %s (%s - Tầng %d - %s%s)",
                roomNumber, getRoomTypeName(), floor, status.getDisplayName(), cleanTag);
    }
}
