package vn.edu.ptit.oop.nhom08.model;

public abstract class Room {
    private String roomNumber;
    private int floor;
    private RoomStatus status;
    private boolean cleaningRequested;

    public Room(String roomNumber, int floor) {
        setRoomNumber(roomNumber);
        setFloor(floor);
        this.status = RoomStatus.AVAILABLE;
        this.cleaningRequested = false;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("So phong khong duoc de trong");
        }
        this.roomNumber = roomNumber.trim();
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        if (floor < 1) {
            throw new IllegalArgumentException("Tang phai lon hon hoac bang 1");
        }
        this.floor = floor;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trang thai phong khong duoc null");
        }
        this.status = status;
    }

    public boolean isCleaningRequested() {
        return cleaningRequested;
    }

    public void setCleaningRequested(boolean cleaningRequested) {
        this.cleaningRequested = cleaningRequested;
    }

    public abstract double getPricePerNight();

    public abstract String getAmenities();

    public abstract String getRoomTypeName();

    public abstract int getMaxOccupancy();
}
