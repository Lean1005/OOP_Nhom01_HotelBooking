package vn.edu.ptit.oop.nhom08.ui;

import javax.swing.table.AbstractTableModel;
import vn.edu.ptit.oop.nhom08.HotelBookingApp;
import vn.edu.ptit.oop.nhom08.model.Booking;
import vn.edu.ptit.oop.nhom08.model.Guest;
import vn.edu.ptit.oop.nhom08.service.BookingService;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingTableModel extends AbstractTableModel {
    private final String[] cols = {
            "Mã ĐP", "Phòng", "Khách lưu trú", "Số khách", "Giới tính",
            "CCCD đại diện", "SĐT", "Check-in", "Check-out", "Số đêm", "Tiền phòng (VNĐ)", "Trạng thái"
    };
    private final List<Booking> bookings;
    private final BookingService service;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat vnd = NumberFormat.getNumberInstance(HotelBookingApp.VI_LOCALE);

    public BookingTableModel(List<Booking> bookings, BookingService service) {
        this.bookings = bookings;
        this.service = service;
    }

    @Override public int getRowCount() { return bookings.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }

    @Override
    public Object getValueAt(int r, int c) {
        Booking b = bookings.get(r);
        Guest p = b.getPrimaryGuest();
        switch (c) {
            case 0: return b.getBookingId();
            case 1: return "P." + b.getRoom().getRoomNumber() + " (" + b.getRoom().getRoomTypeName() + ")";
            case 2: return b.getGuestsSummary();
            case 3: return b.getGuests().size() + " người";
            case 4: return b.getAllGendersSummary();
            case 5: return p.getCccd();
            case 6: return p.getPhone();
            case 7: return b.getCheckIn().format(df);
            case 8: return b.getCheckOut().format(df);
            case 9: return b.nights();
            case 10: return vnd.format(b.totalCost(service.getPricingPolicy()));
            default: 
                // Xử lý phân loại trạng thái hiển thị trên bảng
                if (b.isCheckedOut()) {
                    return "Đã Check-out";
                }
                // Kiểm tra trạng thái hiện tại từ đối tượng Booking (hoặc database)
                if (b.getStatus() != null && !b.getStatus().isEmpty()) {
                    String st = b.getStatus();
                    if (st.equalsIgnoreCase("CONFIRMED") || st.equals("Đặt phòng thành công")) {
                        return "Đặt phòng thành công";
                    } else if (st.equalsIgnoreCase("CHECKED_IN") || st.equals("Đang lưu trú")) {
                        return "Đang lưu trú";
                    }
                    return st;
                }
                return "Đặt phòng thành công"; // Mặc định khi vừa tạo đơn đặt phòng mới
        }
    }

    public Booking getBookingAt(int row) { return bookings.get(row); }
}