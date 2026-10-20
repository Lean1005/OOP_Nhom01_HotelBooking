package vn.edu.ptit.oop.nhom08.ui;

import vn.edu.ptit.oop.nhom08.model.Booking;
import vn.edu.ptit.oop.nhom08.model.Guest;
import vn.edu.ptit.oop.nhom08.model.Room;
import vn.edu.ptit.oop.nhom08.service.BookingService;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Bảng đặt phòng. Phụ trách: Nguyễn Văn Giác - N24DCAT021. */
public class BookingTableModel extends AbstractTableModel {
    public static final int TOTAL_COLUMN = 10;
    public static final int STATUS_COLUMN = 11;

    private static final String[] COLUMNS = {
            "Mã đặt phòng", "Số phòng", "Khách lưu trú", "Giới tính", "CCCD", "Điện thoại",
            "Số khách", "Ngày nhận", "Ngày trả", "Số đêm", "Tiền phòng dự kiến", "Trạng thái"
    };
    private static final Class<?>[] TYPES = {
            String.class, String.class, String.class, String.class, String.class, String.class,
            Integer.class, LocalDate.class, LocalDate.class, Integer.class, Double.class, String.class
    };

    // Giữ danh sách của repository để MainWindow có thể gọi fireTableDataChanged sau khi thêm đơn.
    private final List<Booking> bookings;
    private final BookingService bookingService;

    public BookingTableModel(List<Booking> bookings, BookingService bookingService) {
        this.bookings = Objects.requireNonNull(bookings, "Danh sách đặt phòng");
        this.bookingService = Objects.requireNonNull(bookingService, "BookingService");
    }

    @Override
    public int getRowCount() {
        return bookings.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return TYPES[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Booking getBookingAt(int modelRow) {
        return bookings.get(modelRow);
    }

    @Override
    public Object getValueAt(int row, int column) {
        Booking booking = getBookingAt(row);
        switch (column) {
            case 0: return booking.getBookingId();
            case 1: return booking.getRoomNumber();
            case 2: return joinGuests(booking, Guest::getFullName);
            case 3: return joinGuests(booking, guest -> guest.getGender().getLabel());
            case 4: return joinGuests(booking, Guest::getCccd);
            case 5: return joinGuests(booking, Guest::getPhone);
            case 6: return booking.getGuests().size();
            case 7: return booking.getCheckIn();
            case 8: return booking.getCheckOut();
            case 9: return booking.nights();
            case TOTAL_COLUMN:
                Room room = bookingService.getRepository().getAllRooms().stream()
                        .filter(item -> item.getRoomNumber().equals(booking.getRoomNumber()))
                        .findFirst().orElse(null);
                if (room == null) {
                    return null;
                }
                return bookingService.getPricingPolicy().calculateRoomTotal(
                        room.getPricePerNight(), booking.nights());
            case STATUS_COLUMN: return getBookingStatus(row);
            default: throw new IndexOutOfBoundsException("Cột không hợp lệ: " + column);
        }
    }

    private String joinGuests(Booking booking, Function<Guest, String> value) {
        return booking.getGuests().stream().map(value).collect(Collectors.joining("; "));
    }

    public String getBookingStatus(int modelRow) {
        Booking booking = getBookingAt(modelRow);
        if (booking.isCheckedOut()) {
            return "Đã trả phòng";
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(booking.getCheckIn())) {
            return "Chưa đến ngày nhận";
        }
        if (!today.isBefore(booking.getCheckOut())) {
            return "Đến hạn trả phòng";
        }
        return "Trong thời gian lưu trú";
    }
}
