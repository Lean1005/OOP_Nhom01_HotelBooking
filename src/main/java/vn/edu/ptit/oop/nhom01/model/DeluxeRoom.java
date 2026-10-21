package vn.edu.ptit.oop.nhom08.model;

public class DeluxeRoom extends Room {
    private String viewDirection;

    public DeluxeRoom(String roomNumber, int floor, String viewDirection) {
        super(roomNumber, floor);
        setViewDirection(viewDirection);
    }

    public String getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(String viewDirection) {
        if (viewDirection == null || viewDirection.trim().isEmpty()) {
            throw new IllegalArgumentException("Huong nhin khong duoc de trong");
        }
        this.viewDirection = viewDirection.trim();
    }

    @Override
    public double getPricePerNight() {
        return 900000;
    }

    @Override
    public String getAmenities() {
        return "Wifi, dieu hoa, TV, minibar, huong nhin: " + viewDirection;
    }

    @Override
    public String getRoomTypeName() {
        return "Deluxe";
    }

    @Override
    public int getMaxOccupancy() {
        return 2;
    }
}
