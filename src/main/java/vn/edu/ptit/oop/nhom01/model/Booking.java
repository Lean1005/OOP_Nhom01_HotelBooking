package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exception.InvalidBookingDataException;

/**
 * Đơn đặt phòng, hỗ trợ nhiều khách cùng lưu trú trong một phòng.
 *
 * Quy tắc dữ liệu (luôn đúng trong suốt vòng đời đối tượng):
 *  - ngày trả phòng phải sau ngày nhận phòng ít nhất 1 ngày;
 *  - có ít nhất 1 khách;
 *  - không có hai khách trùng CCCD.
 */
public class Booking {

    private final String bookingId;
    private final String roomNumber;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final List<Guest> guests = new ArrayList<>();

    public Booking(String bookingId, String roomNumber, LocalDate checkIn, LocalDate checkOut,
                   List<Guest> guests) throws InvalidBookingDataException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new InvalidBookingDataException("Mã đặt phòng không được để trống.");
        }
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new InvalidBookingDataException("Số phòng không được để trống.");
        }
        if (checkIn == null || checkOut == null) {
            throw new InvalidBookingDataException(
                    "Ngày nhận phòng và ngày trả phòng không được để trống.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidBookingDataException(
                    "Ngày trả phòng phải sau ngày nhận phòng ít nhất 1 ngày.");
        }
        if (guests == null || guests.isEmpty()) {
            throw new InvalidBookingDataException("Đơn đặt phòng phải có ít nhất 1 khách.");
        }

        this.bookingId = bookingId.trim();
        this.roomNumber = roomNumber.trim();
        this.checkIn = checkIn;
        this.checkOut = checkOut;

        // Sao chép từng phần tử vào list nội bộ (sao chép phòng vệ): Booking không giữ
        // tham chiếu tới list của bên ngoài, nên bên ngoài sửa list đó cũng không ảnh hưởng.
        for (Guest guest : guests) {
            checkCanAdd(guest);
            this.guests.add(guest);
        }
    }

    /** Thêm một khách vào đơn đặt phòng. */
    public void addGuest(Guest guest) throws InvalidBookingDataException {
        checkCanAdd(guest);
        guests.add(guest);
    }

    /** Xóa một khách khỏi đơn; đơn luôn phải còn ít nhất 1 khách. */
    public void removeGuest(Guest guest) throws InvalidBookingDataException {
        if (!guests.contains(guest)) {
            throw new InvalidBookingDataException("Khách này không có trong đơn đặt phòng.");
        }
        if (guests.size() == 1) {
            throw new InvalidBookingDataException("Đơn đặt phòng phải còn ít nhất 1 khách.");
        }
        guests.remove(guest);
    }

    private void checkCanAdd(Guest guest) throws InvalidBookingDataException {
        if (guest == null) {
            throw new InvalidBookingDataException("Khách không được để trống.");
        }
        if (guests.contains(guest)) { // Guest.equals so sánh theo CCCD
            throw new InvalidBookingDataException(
                    "Khách có CCCD " + guest.getCccd() + " đã có trong đơn đặt phòng.");
        }
    }

    /**
     * Trả về danh sách khách ở dạng CHỈ ĐỌC.
     *
     * Không trả thẳng biến guests vì lớp bên ngoài có thể tự ý thêm hoặc xóa phần tử,
     * làm hỏng dữ liệu nội bộ của Booking (xóa hết khách, thêm khách trùng CCCD...).
     * Muốn thay đổi danh sách phải đi qua addGuest()/removeGuest() để được kiểm tra.
     * Gọi add()/remove() trên list trả về sẽ ném UnsupportedOperationException.
     */
    public List<Guest> getGuests() {
        return Collections.unmodifiableList(guests);
    }

    public int getGuestCount() {
        return guests.size();
    }

    /** Số đêm lưu trú = số ngày giữa ngày nhận và ngày trả phòng. */
    public int nights() {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    @Override
    public String toString() {
        return "Booking{" + bookingId + ", phòng " + roomNumber + ", " + checkIn + " → " + checkOut
                + " (" + nights() + " đêm), " + guests.size() + " khách}";
    }
}
