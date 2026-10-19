package vn.edu.ptit.oop.nhom08.repository;

import vn.edu.ptit.oop.nhom08.model.Booking;
import vn.edu.ptit.oop.nhom08.model.Room;
import java.util.List;

public interface HotelRepository {
    List<Room> getAllRooms();
    void addRoom(Room room);
    List<Booking> getAllBookings();
    void addBooking(Booking booking);
    void removeBooking(String bookingId);
}