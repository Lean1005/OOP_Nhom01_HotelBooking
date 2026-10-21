package vn.edu.ptit.oop.nhom08.model;

public class SuiteRoom extends Room {
    private boolean hasJacuzzi;

    public SuiteRoom(String roomNumber, int floor, boolean hasJacuzzi) {
        super(roomNumber, floor);
        this.hasJacuzzi = hasJacuzzi;
    }

    public boolean isHasJacuzzi() {
        return hasJacuzzi;
    }

    public void setHasJacuzzi(boolean hasJacuzzi) {
        this.hasJacuzzi = hasJacuzzi;
    }

    @Override
    public double getPricePerNight() {
        return 1800000;
    }

    @Override
    public String getAmenities() {
        return "Wifi, dieu hoa, TV, phong khach rieng, jacuzzi: "
                + (hasJacuzzi ? "co" : "khong");
    }

    @Override
    public String getRoomTypeName() {
        return "Suite";
    }

    @Override
    public int getMaxOccupancy() {
        return 4;
    }
}
