package vn.edu.ptit.oop.nhom01.model;

public class SuiteRoom extends Room {
    private final boolean hasJacuzzi;
    public SuiteRoom(String roomNumber, int floor, RoomStatus status, boolean hasJacuzzi) {
        super(roomNumber, floor, status);
        this.hasJacuzzi = hasJacuzzi;
    }
    @Override public double getPricePerNight() { return 1_800_000; }
    @Override public String getAmenities() {
        return "Phòng khách VIP riêng, Giường King, Buffet sáng miễn phí"
                + (hasJacuzzi ? ", Bể sục Jacuzzi" : "") + " (Tối đa 6 người)";
    }
    @Override public String getRoomTypeName() { return "Suite"; }
    @Override public int getMaxOccupancy() { return 6; }
}
