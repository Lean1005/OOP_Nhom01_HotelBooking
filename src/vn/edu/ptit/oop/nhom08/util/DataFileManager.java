package vn.edu.ptit.oop.nhom08.util;

import vn.edu.ptit.oop.nhom08.model.Booking;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DataFileManager {
    public static void exportBookingsToCSV(List<Booking> bookings, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("BookingID,RoomNumber,CheckIn,CheckOut,MainGuest");
            for (Booking b : bookings) {
                String guestName = b.getGuests().isEmpty() ? "N/A" : b.getGuests().get(0).getFullName();
                writer.printf("%s,%d,%s,%s,%s%n",
                        b.getBookingId(),
                        b.getRoom().getRoomNumber(),
                        b.getCheckIn(),
                        b.getCheckOut(),
                        guestName);
            }
        }
    }
}