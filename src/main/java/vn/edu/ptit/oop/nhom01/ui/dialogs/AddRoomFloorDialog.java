package vn.edu.ptit.oop.nhom01.ui.dialogs;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import vn.edu.ptit.oop.nhom01.exception.*;
import vn.edu.ptit.oop.nhom01.model.*;
import vn.edu.ptit.oop.nhom01.service.*;
import java.awt.*;
import java.util.List;

public class AddRoomFloorDialog extends JDialog {
    private final BookingService bookingService;
    private boolean changed = false;
    private Room lastAddedRoom;
    private String actionSummaryMessage = "";

    public AddRoomFloorDialog(JFrame parent, BookingService bookingService, int selectedFloor, int initialTab) {
        super(parent, "Quản lý Mở rộng Khách sạn: Thêm Phòng & Thêm Tầng - Nhóm8HotelBooking", true);
        this.bookingService = bookingService;
        setSize(540, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.addTab("1. Thêm Phòng mới vào Tầng", createAddSingleRoomPanel(selectedFloor));
        tabbedPane.addTab("2. Thêm Tầng mới (Tạo hàng loạt phòng)", createAddFloorPanel());

        if (initialTab >= 0 && initialTab < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(initialTab);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAddSingleRoomPanel(int defaultFloor) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(14, 16, 12, 16));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 10));

        List<Integer> floors = bookingService.getExistingFloors();
        JComboBox<Integer> cboFloor = new JComboBox<>(floors.toArray(new Integer[0]));
        cboFloor.setSelectedItem(defaultFloor);

        JTextField txtRoomNo = new JTextField(bookingService.suggestNextRoomNumber(defaultFloor));
        txtRoomNo.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JComboBox<String> cboType = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"});
        JSpinner spnBeds = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
        JComboBox<String> cboView = new JComboBox<>(new String[]{"Hướng Biển", "Hướng Thành phố", "Hướng Hồ bơi"});
        JCheckBox chkJacuzzi = new JCheckBox("Trang bị bể sục Jacuzzi VIP", true);

        cboFloor.addActionListener(e -> {
            Integer fl = (Integer) cboFloor.getSelectedItem();
            if (fl != null) {
                txtRoomNo.setText(bookingService.suggestNextRoomNumber(fl));
            }
        });

        form.add(new JLabel("Chọn Tầng muốn thêm phòng:"));  form.add(cboFloor);
        form.add(new JLabel("Số phòng mới (tự động gợi ý):")); form.add(txtRoomNo);
        form.add(new JLabel("Chọn Hạng phòng:"));            form.add(cboType);
        form.add(new JLabel("Số giường (nếu là Standard):")); form.add(spnBeds);
        form.add(new JLabel("Hướng ban công (nếu là Deluxe):")); form.add(cboView);
        form.add(new JLabel("Tiện ích VIP (nếu là Suite):")); form.add(chkJacuzzi);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddRoom = new JButton("Xác nhận Thêm Phòng");
        btnAddRoom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddRoom.setForeground(new Color(22, 160, 133));
        JButton btnClose = new JButton("Đóng");

        btnAddRoom.addActionListener(e -> {
            try {
                int fl = (Integer) cboFloor.getSelectedItem();
                String roomNo = txtRoomNo.getText();
                String type = (String) cboType.getSelectedItem();
                int beds = (Integer) spnBeds.getValue();
                String view = (String) cboView.getSelectedItem();
                boolean jacuzzi = chkJacuzzi.isSelected();

                lastAddedRoom = bookingService.addSingleRoom(roomNo, fl, type, beds, view, jacuzzi);
                changed = true;
                actionSummaryMessage = "Đã thêm thành công Phòng " + lastAddedRoom.getRoomNumber()
                        + " (" + lastAddedRoom.getRoomTypeName() + ") vào Tầng " + fl + "!";
                JOptionPane.showMessageDialog(this, actionSummaryMessage, "Thêm Phòng thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (InvalidBookingDataException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnClose.addActionListener(e -> dispose());

        btnBar.add(btnAddRoom);
        btnBar.add(btnClose);

        panel.add(form, BorderLayout.CENTER);
        panel.add(btnBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAddFloorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(14, 16, 12, 16));

        int nextFloor = bookingService.getNextNewFloorNumber();
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 12));

        JSpinner spnNewFloor = new JSpinner(new SpinnerNumberModel(nextFloor, 1, 30, 1));
        spnNewFloor.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JSpinner spnRoomCount = new JSpinner(new SpinnerNumberModel(6, 1, 12, 1));
        JComboBox<String> cboFloorType = new JComboBox<>(new String[]{
                "Hỗn hợp (Standard + Deluxe + Suite)",
                "Standard",
                "Deluxe",
                "Suite"
        });
        JLabel lblPreview = new JLabel();
        lblPreview.setForeground(new Color(41, 128, 185));
        lblPreview.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        Runnable updatePreview = () -> {
            int fl = (Integer) spnNewFloor.getValue();
            int cnt = (Integer) spnRoomCount.getValue();
            lblPreview.setText(String.format("Sẽ tự động tạo %d phòng: từ P.%d01 đến P.%d%02d", cnt, fl, fl, cnt));
        };
        spnNewFloor.addChangeListener(e -> updatePreview.run());
        spnRoomCount.addChangeListener(e -> updatePreview.run());
        updatePreview.run();

        form.add(new JLabel("Số thứ tự Tầng mới cần xây:"));  form.add(spnNewFloor);
        form.add(new JLabel("Số lượng phòng trên Tầng mới:")); form.add(spnRoomCount);
        form.add(new JLabel("Cấu hình Hạng phòng:"));         form.add(cboFloorType);
        form.add(new JLabel("Dự kiến các phòng được tạo:"));  form.add(lblPreview);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddFloor = new JButton("Xác nhận Xây Thêm Tầng");
        btnAddFloor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddFloor.setForeground(new Color(41, 128, 185));
        JButton btnClose = new JButton("Đóng");

        btnAddFloor.addActionListener(e -> {
            try {
                int fl = (Integer) spnNewFloor.getValue();
                int cnt = (Integer) spnRoomCount.getValue();
                String opt = (String) cboFloorType.getSelectedItem();

                List<Room> created = bookingService.addNewFloor(fl, cnt, opt);
                if (!created.isEmpty()) {
                    lastAddedRoom = created.get(0);
                }
                changed = true;
                actionSummaryMessage = "Đã xây thêm Tầng " + fl + " với " + created.size() + " phòng mới thành công!";
                JOptionPane.showMessageDialog(this, actionSummaryMessage, "Thêm Tầng thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (InvalidBookingDataException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnClose.addActionListener(e -> dispose());

        btnBar.add(btnAddFloor);
        btnBar.add(btnClose);

        panel.add(form, BorderLayout.CENTER);
        panel.add(btnBar, BorderLayout.SOUTH);
        return panel;
    }

    public boolean isChanged() { return changed; }
    public Room getLastAddedRoom() { return lastAddedRoom; }
    public String getActionSummaryMessage() { return actionSummaryMessage; }
}
