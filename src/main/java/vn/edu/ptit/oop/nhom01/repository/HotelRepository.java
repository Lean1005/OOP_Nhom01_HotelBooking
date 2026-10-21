package vn.edu.ptit.oop.nhom01.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import vn.edu.ptit.oop.nhom01.model.*;

public interface HotelRepository {
    List<Room> findAllRooms();
    Room findRoomByNumber(String roomNumber);
    void addRoom(Room room);
    List<Booking> findAllBookings();
    void addBooking(Booking booking);
    Map<LocalDate, Double> getRevenueLast14Days();
    void addRevenue(LocalDate date, double amount);

    // Default methods giữ tương thích với các repository khác trong đồ án.
    default void updateBookingStatus(Booking booking, String status) {}
    default void updateRoom(Room room) {}
    default void saveInvoice(Booking booking, Invoice invoice) {}
}
