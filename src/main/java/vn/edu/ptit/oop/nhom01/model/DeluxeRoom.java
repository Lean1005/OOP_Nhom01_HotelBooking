package vn.edu.ptit.oop.nhom01.model;

public class DeluxeRoom extends Room {
    private final String viewDirection;
    public DeluxeRoom(String roomNumber, int floor, RoomStatus status, String viewDirection) {
        super(roomNumber, floor, status);
        this.viewDirection = viewDirection;
    }
    @Override public double getPricePerNight() { return 900_000; }
    @Override public String getAmenities() { return "Smart TV 55 inch, Ban công (" + viewDirection + "), Bồn tắm nằm, Minibar (Tối đa 4 người)"; }
    @Override public String getRoomTypeName() { return "Deluxe"; }
    @Override public int getMaxOccupancy() { return 4; }
}
