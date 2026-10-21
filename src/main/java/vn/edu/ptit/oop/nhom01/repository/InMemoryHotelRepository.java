package vn.edu.ptit.oop.nhom08.repository;

import vn.edu.ptit.oop.nhom08.model.*;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHotelRepository implements HotelRepository {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public InMemoryHotelRepository() {
        initSampleData();
    }

    private void initSampleData() {
        // Khởi tạo 4 tầng x 6 phòng = 24 phòng mẫu
        for (int floor = 1; floor <= 4; floor++) {
            for (int roomNum = 1; roomNum <= 6; roomNum++) {
                int roomNumber = floor * 100 + roomNum;
                if (roomNum <= 3) {
                    rooms.add(new StandardRoom(roomNumber, floor, 2));
                } else if (roomNum <= 5) {
                    rooms.add(new DeluxeRoom(roomNumber, floor, "Huong Bien"));
                } else {
                    rooms.add(new SuiteRoom(roomNumber, floor, true));
                }
            }
        }
    }

    @Override
    public List<Room> getAllRooms() {
        return rooms;
    }

    @Override
    public void addRoom(Room room) {
        rooms.add(room);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookings;
    }

    @Override
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    @Override
    public void removeBooking(String bookingId) {
        bookings.removeIf(b -> b.getBookingId().equals(bookingId));
    }
}