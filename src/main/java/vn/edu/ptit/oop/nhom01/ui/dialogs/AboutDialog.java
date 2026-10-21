package vn.edu.ptit.oop.nhom08.ui.dialogs;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

/**
 * Hộp thoại giới thiệu đề tài và danh sách 9 thành viên của nhóm.
 * Phụ trách: Nguyễn Lê An - MSSV: N24DCAT001 (Thành viên 8)
 */
public class AboutDialog extends JDialog {

    public AboutDialog(Frame owner) {
        super(owner, "Giới thiệu Hệ thống & Danh sách Thành viên", true);
        initComponents();
        setSize(720, 420);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblHeader = new JLabel(
                "<html><center><h2>HỆ THỐNG QUẢN LÝ ĐẶT PHÒNG KHÁCH SẠN (HOTEL BOOKING)</h2>"
                        + "Học phần: INT1332 – Lập trình Hướng đối tượng (PTIT)</center></html>",
                SwingConstants.CENTER);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));
        lblHeader.setForeground(new Color(25, 85, 145));

        String[] columns = {"STT", "Họ và tên", "MSSV", "Vai trò / Phân hệ phụ trách", "Tỷ lệ"};
        Object[][] data = {
                {"1", "Hoàng Thị Huyền Trang", "N24DCCN083", "Nhóm trưởng & Khung hệ thống (BaseWindow, MainWindow)", "12%"},
                {"2", "Lê Phạm Hoàng Kha", "N24DCCN036", "Model Phòng & Kế thừa (Room, Standard, Deluxe, Suite)", "11%"},
                {"3", "Trần Trí Nguyên", "N24DCAT053", "Model Khách, Booking & Ngoại lệ (Guest, Booking, Exception)", "11%"},
                {"4", "Nguyễn Minh Tuấn", "N24DCAT085", "Chiến lược Giá (Strategy Pattern) & Hóa đơn (Invoice)", "11%"},
                {"5", "Lê Hoàng Nam", "N24DCAT050", "Kho dữ liệu & Nghiệp vụ Đặt phòng (Repository, Service, CSV)", "11%"},
                {"6", "Hồ Hoài Sang", "N24DCCN159", "Đồ họa 2D Sơ đồ phòng & Dọn phòng (RoomMapPanel)", "11%"},
                {"7", "Nguyễn Văn Giác", "N24DCAT021", "Hộp thoại Đặt phòng, Mở rộng & Bảng (BookingDialog)", "11%"},
                {"8", "Nguyễn Lê An", "N24DCAT001", "Hóa đơn Đỏ, Hình ảnh DV & Lọc phòng (InvoiceDialog, Filter)", "11%"},
                {"9", "Lâm Sỹ Nguyên", "N24DCAT052", "Biểu đồ Thống kê & Kiểm thử (ChartWindow, Bar/LineChart)", "11%"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);

        add(lblHeader, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}