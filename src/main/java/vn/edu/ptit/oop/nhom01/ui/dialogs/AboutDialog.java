package vn.edu.ptit.oop.nhom01.ui.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
public class AboutDialog extends JDialog {
    public AboutDialog(JFrame parent) {
        super(parent, "Thông tin Nhóm8HotelBooking", true);
        setSize(620, 440);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JTextArea txt = new JTextArea(
                "HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG (PTIT)\n" +
                "Môn học: INT1332 - Lập trình Hướng đối tượng\n" +
                "Đơn vị: CÔNG TY TNHH NHÓM 1- HOTEL BOOKING\n\n" +
                "PHÂN CÔNG NHIỆM VỤ 9 THÀNH VIÊN:\n" +
                "1. Hoàng Thị Huyền Trang (Nhóm trưởng): HotelBookingApp, BaseWindow, MainWindow\n" +
                "2. Lê Phạm Hoàng Kha: Room (abstract + cờ dọn phòng), RoomStatus, StandardRoom, DeluxeRoom, SuiteRoom\n" +
                "3. Trần Trí Nguyên: Guest (có Giới tính), Booking (nhiều khách), RoomNotAvailableException\n" +
                "4. Nguyễn Minh Tuấn: PricingPolicy (Strategy BONUS), ServiceCharge, Invoice\n" +
                "5. Lê Hoàng Nam: HotelRepository, InMemoryHotelRepository, BookingService (Thêm Phòng/Tầng)\n" +
                "6. Hồ Hoài Sang: RoomMapPanel (Sơ đồ 2D co giãn động), RoomDetailPanel (Yêu cầu dọn phòng)\n" +
                "7. Nguyễn Văn Giác: BookingDialog (Nhiều khách + Giới tính), AddRoomFloorDialog, BookingTableModel\n" +
                "8. Nguyễn Lê An: InvoiceDialog, InvoicePaperPanel (Mộc đỏ & Hình ảnh DV), RoomFilterDialog\n" +
                "9. Lâm Sỹ Nguyên: ChartWindow, BarChartPanel, LineChartPanel (Biểu đồ Công suất & Doanh thu)\n"
        );
        txt.setEditable(false);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(new EmptyBorder(12, 16, 12, 16));

        JButton btn = new JButton("Đóng");
        btn.addActionListener(e -> dispose());
        JPanel p = new JPanel(); p.add(btn);

        add(new JScrollPane(txt), BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
    }
}
