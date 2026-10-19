package vn.edu.ptit.oop.nhom08.model;

public class StandardRoom extends Room {
    private int bedCount;

    public StandardRoom(String roomNumber, int floor, int bedCount) {
        super(roomNumber, floor);
        setBedCount(bedCount);
    }

    public int getBedCount() {
        return bedCount;
    }

    public void setBedCount(int bedCount) {
        if (bedCount < 1) {
            throw new IllegalArgumentException("So giuong phai lon hon hoac bang 1");
        }
        this.bedCount = bedCount;
    }

    @Override
    public double getPricePerNight() {
        return 500000;
    }

    @Override
    public String getAmenities() {
        return "Wifi, dieu hoa, TV, " + bedCount + " giuong";
    }

    @Override
    public String getRoomTypeName() {
        return "Standard";
    }

    @Override
    public int getMaxOccupancy() {
        return bedCount * 2;
    }
}
