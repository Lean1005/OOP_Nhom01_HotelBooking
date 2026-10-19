package vn.edu.ptit.oop.nhom08.service;

import vn.edu.ptit.oop.nhom08.exception.RoomNotAvailableException;
import vn.edu.ptit.oop.nhom08.model.Booking;
import vn.edu.ptit.oop.nhom08.model.Room;
import vn.edu.ptit.oop.nhom08.model.StandardRoom;
import vn.edu.ptit.oop.nhom08.repository.HotelRepository;

import java.time.LocalDate;

public class BookingService {
    private final HotelRepository repository;

    public BookingService(HotelRepository repository) {
        this.repository = repository;
    }

    public boolean isRoomAvailable(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Booking b : repository.getAllBookings()) {
            if (b.getRoom().getRoomNumber() == roomNumber) {
                // Thuật toán kiểm tra trùng lịch đặt phòng
                boolean isOverlap = checkIn.isBefore(b.getCheckOut()) && checkOut.isAfter(b.getCheckIn());
                if (isOverlap) {
                    return false;
                }
            }
        }
        return true;
    }

    public void createBooking(Booking booking) throws RoomNotAvailableException {
        if (!isRoomAvailable(booking.getRoom().getRoomNumber(), booking.getCheckIn(), booking.getCheckOut())) {
            throw new RoomNotAvailableException("Phong " + booking.getRoom().getRoomNumber() + " da co nguoi dat trong thoi gian nay!");
        }
        repository.addBooking(booking);
    }

    public void addNewFloor(int floorNumber) {
        for (int i = 1; i <= 6; i++) {
            int roomNumber = floorNumber * 100 + i;
            repository.addRoom(new StandardRoom(roomNumber, floorNumber, 2));
        }
    }
}