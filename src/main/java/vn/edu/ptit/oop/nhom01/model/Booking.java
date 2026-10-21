package vn.edu.ptit.oop.nhom01.model;

import vn.edu.ptit.oop.nhom01.exception.*;
import vn.edu.ptit.oop.nhom01.service.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Booking {
    private final String bookingId;
    private final List<Guest> guests = new ArrayList<>();
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private boolean checkedOut;
    private String status;

    public Booking(String bookingId, List<Guest> guestList, Room room, LocalDate checkIn, LocalDate checkOut)
            throws InvalidBookingDataException {
        this(bookingId, guestList, room, checkIn, checkOut, "Đặt phòng thành công");
    }

    public Booking(String bookingId, List<Guest> guestList, Room room, LocalDate checkIn, LocalDate checkOut, String status)
            throws InvalidBookingDataException {
        if (guestList == null || guestList.isEmpty())
            throw new InvalidBookingDataException("Phải có ít nhất 1 khách hàng trong danh sách đặt phòng!");
        if (room == null)
            throw new InvalidBookingDataException("Phòng không được để trống!");
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn))
            throw new InvalidBookingDataException("Ngày Check-out phải sau ngày Check-in ít nhất 1 đêm!");
        this.bookingId = bookingId;
        this.guests.addAll(guestList);
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = (status != null && !status.trim().isEmpty()) ? status : "Đặt phòng thành công";
        this.checkedOut = "CHECKED_OUT".equalsIgnoreCase(status) || "Đã Check-out".equalsIgnoreCase(status) || "Đã trả phòng".equalsIgnoreCase(status);
    }

    public Guest getPrimaryGuest() {
        return guests.get(0);
    }

    public List<Guest> getGuests() {
        return Collections.unmodifiableList(guests);
    }

    public String getGuestsSummary() {
        if (guests.size() == 1) {
            Guest g = guests.get(0);
            return g.getName() + " (" + g.getGender().getLabel() + ")";
        }
        return getPrimaryGuest().getName() + " (+" + (guests.size() - 1) + " người đi cùng)";
    }

    public String getAllGendersSummary() {
        return guests.stream().map(g -> g.getGender().getLabel()).collect(Collectors.joining(", "));
    }

    public long nights() { return ChronoUnit.DAYS.between(checkIn, checkOut); }
    public double totalCost() { return nights() * room.getPricePerNight(); }
    public double totalCost(PricingPolicy policy) {
        return policy != null ? policy.calculatePrice(room, nights()) : totalCost();
    }

    public String getBookingId() { return bookingId; }
    public Room getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public boolean isCheckedOut() { return checkedOut; }
    public void setCheckedOut(boolean checkedOut) { 
        this.checkedOut = checkedOut; 
        if (checkedOut && (this.status == null || !this.status.equalsIgnoreCase("CHECKED_OUT"))) {
            this.status = "Đã Check-out";
        }
    }

    public String getStatus() { 
        return status; 
    }

    public void setStatus(String status) { 
        this.status = status; 
        if ("CHECKED_OUT".equalsIgnoreCase(status) || "Đã Check-out".equalsIgnoreCase(status) || "Đã trả phòng".equalsIgnoreCase(status)) {
            this.checkedOut = true;
        }
    }
}
