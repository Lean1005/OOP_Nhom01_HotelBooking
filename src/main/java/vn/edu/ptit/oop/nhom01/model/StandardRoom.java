package vn.edu.ptit.oop.nhom01.model;

public class StandardRoom extends Room {
    private final int bedCount;
    public StandardRoom(String roomNumber, int floor, RoomStatus status, int bedCount) {
        super(roomNumber, floor, status);
        this.bedCount = Math.max(1, bedCount);
    }
    @Override public double getPricePerNight() { return 500_000; }
    @Override public String getAmenities() { return "Wifi tốc độ cao, Tivi 43 inch, Điều hòa (" + bedCount + " giường - Tối đa " + getMaxOccupancy() + " người)"; }
    @Override public String getRoomTypeName() { return "Standard"; }
    @Override public int getMaxOccupancy() { return bedCount * 2; }
}
