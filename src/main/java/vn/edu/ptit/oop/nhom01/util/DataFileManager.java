package vn.edu.ptit.oop.nhom01.util;
import vn.edu.ptit.oop.nhom01.model.*;
import vn.edu.ptit.oop.nhom01.service.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
public class DataFileManager {
    public static void exportBookingsToCsv(List<Booking> bookings, PricingPolicy policy, java.nio.file.Path path) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write('\ufeff');
            w.write("Mã ĐP,Phòng,Hạng Phòng,Số Khách,Khách Đại Diện & Đi Cùng,Giới Tính,CCCD Chính,SĐT Chính,Check-In,Check-Out,Số Đêm,Thành Tiền,Trạng Thái\n");
            for (Booking b : bookings) {
                Guest p = b.getPrimaryGuest();
                w.write(String.format("%s,%s,%s,%d,\"%s\",\"%s\",%s,%s,%s,%s,%d,%.0f,%s\n",
                        b.getBookingId(), b.getRoom().getRoomNumber(), b.getRoom().getRoomTypeName(),
                        b.getGuests().size(), b.getGuestsSummary(), b.getAllGendersSummary(),
                        p.getCccd(), p.getPhone(),
                        b.getCheckIn(), b.getCheckOut(), b.nights(), b.totalCost(policy),
                        b.isCheckedOut() ? "Đã Check-out" : "Đang lưu trú"));
            }
        }
    }
}
