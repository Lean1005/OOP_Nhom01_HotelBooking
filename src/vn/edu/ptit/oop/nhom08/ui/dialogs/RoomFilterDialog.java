package vn.edu.ptit.oop.nhom08.ui.dialogs;

import vn.edu.ptit.oop.nhom08.model.DeluxeRoom;
import vn.edu.ptit.oop.nhom08.model.Room;
import vn.edu.ptit.oop.nhom08.model.RoomStatus;
import vn.edu.ptit.oop.nhom08.model.StandardRoom;
import vn.edu.ptit.oop.nhom08.model.SuiteRoom;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Hộp thoại tra cứu và lọc danh sách phòng theo Hạng phòng, Trạng thái và Yêu cầu dọn phòng.
 * Phụ trách: Nguyễn Lê An - MSSV: N24DCAT001 (Thành viên 8)
 */
public class RoomFilterDialog extends JDialog {

    private final List<Room> allRooms;
    private Room selectedRoom = null;

    private JComboBox<String> cboType;
    private JComboBox<String> cboStatus;
    private JComboBox<String> cboCleaning;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    private final List<Room> filteredRooms = new ArrayList<>();

    public RoomFilterDialog(Frame owner, List<Room> rooms) {
        super(owner, "Lọc & Tra cứu Phòng nâng cao", true);
        this.allRooms = rooms != null ? rooms : new ArrayList<>();
        initComponents();
        applyFilter();
        setSize(680, 440);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel filterBar = new JPanel(new GridLayout(1, 6, 8, 8));
        filterBar.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc phòng"));

        cboType = new JComboBox<>(new String[]{"Tất cả hạng", "Standard", "Deluxe", "Suite"});
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Trống (AVAILABLE)", "Đang ở (OCCUPIED)", "Bảo trì (MAINTENANCE)"});
        cboCleaning = new JComboBox<>(new String[]{"Tất cả dọn phòng", "Cần dọn phòng", "Đã dọn sạch"});

        cboType.addActionListener(e -> applyFilter());
        cboStatus.addActionListener(e -> applyFilter());
        cboCleaning.addActionListener(e -> applyFilter());

        filterBar.add(new JLabel("Hạng phòng:"));
        filterBar.add(cboType);
        filterBar.add(new JLabel("Trạng thái:"));
        filterBar.add(cboStatus);
        filterBar.add(new JLabel("Dọn phòng:"));
        filterBar.add(cboCleaning);

        String[] cols = {"Số phòng", "Tầng", "Hạng phòng", "Trạng thái", "Giá/Đêm (VNĐ)", "Dọn phòng"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(24);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSelect = new JButton("Chọn phòng này");
        JButton btnClose = new JButton("Đóng");

        btnSelect.addActionListener(e -> {
            int row = resultTable.getSelectedRow();
            if (row >= 0 && row < filteredRooms.size()) {
                selectedRoom = filteredRooms.get(row);
                dispose();
            }
        });
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnSelect);
        bottomPanel.add(btnClose);

        add(filterBar, BorderLayout.NORTH);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void applyFilter() {
        tableModel.setRowCount(0);
        filteredRooms.clear();
        NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

        int typeIdx = cboType.getSelectedIndex();
        int statusIdx = cboStatus.getSelectedIndex();
        int cleanIdx = cboCleaning.getSelectedIndex();

        for (Room r : allRooms) {
            if (typeIdx == 1 && !(r instanceof StandardRoom)) continue;
            if (typeIdx == 2 && !(r instanceof DeluxeRoom)) continue;
            if (typeIdx == 3 && !(r instanceof SuiteRoom)) continue;

            if (statusIdx == 1 && r.getStatus() != RoomStatus.AVAILABLE) continue;
            if (statusIdx == 2 && r.getStatus() != RoomStatus.OCCUPIED) continue;
            if (statusIdx == 3 && r.getStatus() != RoomStatus.MAINTENANCE) continue;

            if (cleanIdx == 1 && !r.isCleaningRequested()) continue;
            if (cleanIdx == 2 && r.isCleaningRequested()) continue;

            filteredRooms.add(r);
            tableModel.addRow(new Object[]{
                    r.getRoomNumber(),
                    "Tầng " + r.getFloor(),
                    r.getRoomTypeName(),
                    r.getStatus().getDisplayName(),
                    nf.format(Math.round(r.getPricePerNight())),
                    r.isCleaningRequested() ? "CẦN DỌN PHÒNG" : "Bình thường"
            });
        }
    }

    public Room getSelectedRoom() {
        return selectedRoom;
    }

    public List<Room> getFilteredRooms() {
        return new ArrayList<>(filteredRooms);
    }
}
