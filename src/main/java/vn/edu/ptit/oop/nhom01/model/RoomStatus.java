package vn.edu.ptit.oop.nhom01.model;

public enum RoomStatus {
    AVAILABLE("Trống"), OCCUPIED("Đang có khách"), MAINTENANCE("Bảo trì");
    private final String displayName;
    RoomStatus(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
