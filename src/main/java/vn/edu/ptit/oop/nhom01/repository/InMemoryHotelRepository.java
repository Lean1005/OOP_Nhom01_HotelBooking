package vn.edu.ptit.oop.nhom08.repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import vn.edu.ptit.oop.nhom08.DBConnection;
import vn.edu.ptit.oop.nhom08.exception.InvalidBookingDataException;
import vn.edu.ptit.oop.nhom08.model.*;
import vn.edu.ptit.oop.nhom08.service.StandardPricing;

public class InMemoryHotelRepository implements HotelRepository {
    // Giữ nguyên tên class để không phải sửa nơi khởi tạo repository trong giao diện.
    // Dữ liệu thực tế được đọc/ghi trực tiếp từ SQL Server, không còn lưu trong RAM.

    private Room mapRoom(ResultSet rs) throws SQLException {
        String no = rs.getString("room_id");
        int floor = rs.getInt("floor");
        String type = rs.getString("room_type_name");
        String statusText = rs.getString("status");
        RoomStatus status;
        try { status = RoomStatus.valueOf(statusText.trim().toUpperCase(Locale.ROOT)); }
        catch (Exception ex) { status = RoomStatus.AVAILABLE; }
        Room room;
        if ("Deluxe".equalsIgnoreCase(type)) room = new DeluxeRoom(no, floor, status, "Hướng Biển");
        else if ("Suite".equalsIgnoreCase(type)) room = new SuiteRoom(no, floor, status, true);
        else room = new StandardRoom(no, floor, status, 1);
        room.setCleaningRequested(rs.getBoolean("cleaning_requested"));
        return room;
    }

    @Override public List<Room> findAllRooms() {
        List<Room> result = new ArrayList<>();
        String sql = "SELECT room_id, floor, room_type_name, price_per_night, status, cleaning_requested, max_occupacy FROM Phong ORDER BY room_id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRoom(rs));
        } catch (SQLException e) { throw new IllegalStateException("Không đọc được danh sách phòng từ SQL Server.", e); }
        return result;
    }

    @Override public Room findRoomByNumber(String roomNumber) {
        String sql = "SELECT room_id, floor, room_type_name, price_per_night, status, cleaning_requested, max_occupacy FROM Phong WHERE room_id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapRoom(rs) : null; }
        } catch (SQLException e) { throw new IllegalStateException("Không tìm được phòng " + roomNumber, e); }
    }

    @Override public void addRoom(Room room) {
        String sql = "INSERT INTO Phong(room_id, floor, room_type_name, price_per_night, status, cleaning_requested, max_occupacy) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setInt(2, room.getFloor());
            ps.setString(3, room.getRoomTypeName());
            ps.setDouble(4, room.getPricePerNight());
            ps.setString(5, room.getStatus().name());
            ps.setBoolean(6, room.isCleaningRequested());
            ps.setInt(7, room.getMaxOccupancy());
            ps.executeUpdate();
        } catch (SQLException e) { throw new IllegalStateException("Không lưu được phòng " + room.getRoomNumber(), e); }
    }

    @Override public List<Booking> findAllBookings() {
        List<Booking> result = new ArrayList<>();
        String sql = "SELECT p.ma_dat_phong, p.cccd_dai_dien, p.room_id, p.ngay_nhan_phong, p.ngay_tra_phong, p.trang_thai, " +
                     "k.ho_ten, k.sdt, k.gioi_tinh FROM PhieuDatPhong p JOIN KhachHang k ON p.cccd_dai_dien=k.cccd ORDER BY p.ma_dat_phong";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room room = findRoomByNumber(rs.getString("room_id"));
                if (room == null || rs.getDate("ngay_nhan_phong") == null || rs.getDate("ngay_tra_phong") == null) continue;
                String genderText = rs.getString("gioi_tinh");
                Gender gender = "NU".equalsIgnoreCase(genderText) ? Gender.NU : Gender.NAM;
                try {
                    Guest guest = new Guest(rs.getString("cccd_dai_dien"), rs.getString("ho_ten"), gender, rs.getString("sdt"));
                    String id = "BK-" + rs.getInt("ma_dat_phong");
                    
                    // Lấy trạng thái thực tế từ CSDL (trang_thai) truyền vào Booking
                    String dbStatus = rs.getString("trang_thai");
                    Booking booking = new Booking(id, Collections.singletonList(guest), room,
                        rs.getDate("ngay_nhan_phong").toLocalDate(), rs.getDate("ngay_tra_phong").toLocalDate(), dbStatus);
                    
                    if ("CHECKED_OUT".equalsIgnoreCase(dbStatus) || "Đã Check-out".equalsIgnoreCase(dbStatus)) {
                        booking.setCheckedOut(true);
                    }
                    result.add(booking);
                } catch (InvalidBookingDataException | IllegalArgumentException ex) {
                    System.err.println("Bỏ qua dữ liệu đặt phòng không hợp lệ, mã " + rs.getInt("ma_dat_phong") + ": " + ex.getMessage());
                }
            }
        } catch (SQLException e) { throw new IllegalStateException("Không đọc được đặt phòng từ SQL Server.", e); }
        return result;
    }

    @Override public void addBooking(Booking booking) {
        String guestSql = "IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE cccd=?) INSERT INTO KhachHang(cccd,ho_ten,sdt,gioi_tinh) VALUES(?,?,?,?)";
        String bookingSql = "INSERT INTO PhieuDatPhong(cccd_dai_dien,room_id,ngay_nhan_phong,ngay_tra_phong,trang_thai,tong_bill) VALUES(?,?,?,?,?,?)";
        // Phòng sẽ giữ nguyên trạng thái trống (AVAILABLE) cho đến khi nhân viên bấm Check-in thực tế.
        String roomSql = "UPDATE Phong SET cleaning_requested=? WHERE room_id=?"; 
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Guest g = booking.getGuests().get(0);
                // 1. Lưu hoặc cập nhật thông tin khách hàng
                try (PreparedStatement ps = c.prepareStatement(guestSql)) {
                    ps.setString(1, g.getCccd()); 
                    ps.setString(2, g.getCccd()); 
                    ps.setString(3, g.getName());
                    ps.setString(4, g.getPhone()); 
                    ps.setString(5, g.getGender().name()); 
                    ps.executeUpdate();
                }
                
                // 2. Thêm phiếu đặt phòng mới với trạng thái ban đầu là "CONFIRMED" (Đặt phòng thành công)
                try (PreparedStatement ps = c.prepareStatement(bookingSql)) {
                    ps.setString(1, g.getCccd()); 
                    ps.setString(2, booking.getRoom().getRoomNumber());
                    ps.setDate(3, java.sql.Date.valueOf(booking.getCheckIn()));
                    ps.setDate(4, java.sql.Date.valueOf(booking.getCheckOut()));
                    ps.setString(5, "CONFIRMED"); 
                    ps.setDouble(6, booking.totalCost(new StandardPricing())); 
                    ps.executeUpdate();
                }
                
                // 3. Chỉ cập nhật yêu cầu dọn phòng nếu có, KHÔNG làm thay đổi trạng thái màu sắc phòng
                try (PreparedStatement ps = c.prepareStatement(roomSql)) {
                    ps.setBoolean(1, booking.getRoom().isCleaningRequested());
                    ps.setString(2, booking.getRoom().getRoomNumber()); 
                    ps.executeUpdate();
                }
                
                c.commit();
            } catch (Exception e) { 
                c.rollback(); 
                throw e; 
            }
        } catch (SQLException e) { 
            throw new IllegalStateException("Không lưu được đặt phòng vào SQL Server.", e); 
        }
    }
    @Override public Map<LocalDate, Double> getRevenueLast14Days() {
        Map<LocalDate, Double> result = new LinkedHashMap<>();
        String sql = "SELECT CAST(issued_at AS date) AS revenue_date, SUM(grand_total) AS total FROM Invoices WHERE issued_at >= DATEADD(day,-14,GETDATE()) GROUP BY CAST(issued_at AS date) ORDER BY revenue_date";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.put(rs.getDate("revenue_date").toLocalDate(), rs.getDouble("total"));
        } catch (SQLException e) { throw new IllegalStateException("Không đọc được doanh thu từ SQL Server.", e); }
        return result;
    }

    @Override public void addRevenue(LocalDate date, double amount) {
        // Doanh thu được ghi bằng saveInvoice; không cộng vào một Map chỉ tồn tại trong RAM.
        // Giữ hàm để tương thích với HotelRepository hiện tại.
    }

    @Override public void updateBookingStatus(Booking booking, String status) {
        int id = parseBookingId(booking.getBookingId());
        String sql = "UPDATE PhieuDatPhong SET trang_thai=? WHERE ma_dat_phong=?";
        
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1. Cập nhật trạng thái phiếu đặt phòng
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, status); 
                    ps.setInt(2, id); 
                    ps.executeUpdate();
                }
                
                // 2. Nếu trạng thái là CHECKED_OUT, tự động giải phóng phòng về AVAILABLE và bật cờ dọn phòng
                if ("CHECKED_OUT".equalsIgnoreCase(status) || "Đã trả phòng".equalsIgnoreCase(status)) {
                    String roomId = null;
                    if (booking.getRoom() != null) {
                        roomId = booking.getRoom().getRoomNumber();
                    } else {
                        // Truy vấn ngược room_id từ database nếu booking không giữ đối tượng Room
                        String findRoomSql = "SELECT room_id FROM PhieuDatPhong WHERE ma_dat_phong=?";
                        try (PreparedStatement psFind = c.prepareStatement(findRoomSql)) {
                            psFind.setInt(1, id);
                            try (ResultSet rs = psFind.executeQuery()) {
                                if (rs.next()) roomId = rs.getString("room_id");
                            }
                        }
                    }
                    
                    if (roomId != null) {
                        String roomSql = "UPDATE Phong SET status='AVAILABLE', cleaning_requested=1 WHERE room_id=?";
                        try (PreparedStatement psRoom = c.prepareStatement(roomSql)) {
                            psRoom.setString(1, roomId);
                            psRoom.executeUpdate();
                        }
                    }
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) { 
            throw new IllegalStateException("Không cập nhật được trạng thái đặt phòng.", e); 
        }
    }

    @Override public void saveInvoice(Booking booking, Invoice invoice) {
        int bookingId = parseBookingId(booking.getBookingId());
        String invoiceSql = "IF NOT EXISTS (SELECT 1 FROM Invoices WHERE booking_id=?) INSERT INTO Invoices(booking_id,room_charge,total_service_charges,grand_total,issued_at) VALUES(?,?,?,?,?)";
        String updateBookingSql = "UPDATE PhieuDatPhong SET trang_thai='CHECKED_OUT' WHERE ma_dat_phong=?";
        String updateRoomSql = "UPDATE Phong SET status='AVAILABLE', cleaning_requested=1 WHERE room_id=?";

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1. Lưu hóa đơn
                try (PreparedStatement ps = c.prepareStatement(invoiceSql)) {
                    ps.setInt(1, bookingId);
                    ps.setInt(2, bookingId);
                    ps.setDouble(3, invoice.getRoomCharge()); 
                    ps.setDouble(4, invoice.getTotalServiceCharges());
                    ps.setDouble(5, invoice.getGrandTotal()); 
                    ps.setTimestamp(6, Timestamp.valueOf(invoice.getIssuedAt()));
                    ps.executeUpdate();
                }
                
                // 2. Cập nhật trạng thái phiếu đặt thành CHECKED_OUT
                try (PreparedStatement ps = c.prepareStatement(updateBookingSql)) {
                    ps.setInt(1, bookingId);
                    ps.executeUpdate();
                }
                
                // 3. Giải phóng phòng về AVAILABLE và bật cờ yêu cầu dọn vệ sinh
                String roomId = (booking.getRoom() != null) ? booking.getRoom().getRoomNumber() : null;
                if (roomId == null) {
                    String findRoomSql = "SELECT room_id FROM PhieuDatPhong WHERE ma_dat_phong=?";
                    try (PreparedStatement psFind = c.prepareStatement(findRoomSql)) {
                        psFind.setInt(1, bookingId);
                        try (ResultSet rs = psFind.executeQuery()) {
                            if (rs.next()) roomId = rs.getString("room_id");
                        }
                    }
                }
                
                if (roomId != null) {
                    try (PreparedStatement ps = c.prepareStatement(updateRoomSql)) {
                        ps.setString(1, roomId);
                        ps.executeUpdate();
                    }
                }
                
                c.commit();
            } catch (Exception e) { 
                c.rollback(); 
                throw e; 
            }
        } catch (SQLException e) { 
            throw new IllegalStateException("Không lưu được hóa đơn và cập nhật trạng thái check-out.", e); 
        }
    }

    @Override public void updateRoom(Room room) {
        String sql = "UPDATE Phong SET status=?, cleaning_requested=? WHERE room_id=?";
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,room.getStatus().name()); ps.setBoolean(2,room.isCleaningRequested());
            ps.setString(3,room.getRoomNumber()); ps.executeUpdate();
        } catch(SQLException e) { throw new IllegalStateException("Không cập nhật được trạng thái phòng.",e); }
    }

    private int parseBookingId(String id) {
        if (id == null || !id.matches("BK-\\d+")) throw new IllegalArgumentException("Mã đặt phòng không hợp lệ: " + id);
        return Integer.parseInt(id.substring(3));
    }
}
