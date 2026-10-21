package vn.edu.ptit.oop.nhom01.ui.dialogs;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import vn.edu.ptit.oop.nhom01.*;
import vn.edu.ptit.oop.nhom01.model.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RoomFilterDialog extends JDialog {
    private final List<Room> allRooms;
    private final JComboBox<String> cboType = new JComboBox<>(new String[]{"Tất cả", "Standard", "Deluxe", "Suite"});
    private final JComboBox<String> cboStatus = new JComboBox<>(new String[]{"Tất cả", "Trống", "Đang có khách", "Bảo trì", "Cần dọn phòng"});
    private final DefaultTableModel model;
    private List<Room> currentList;

    public RoomFilterDialog(JFrame parent, List<Room> allRooms, Consumer<Room> onPick) {
        super(parent, "Tra cứu & Lọc Phòng", true);
        this.allRooms = allRooms;
        setSize(680, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Hạng phòng:")); top.add(cboType);
        top.add(new JLabel("Trạng thái:")); top.add(cboStatus);
        cboType.addActionListener(e -> filter());
        cboStatus.addActionListener(e -> filter());

        model = new DefaultTableModel(new String[]{"Số Phòng", "Tầng", "Hạng Phòng", "Giá/Đêm", "Trạng Thái", "Dọn phòng"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(24);

        JButton btnSelect = new JButton("Chọn phòng này trên Sơ đồ");
        btnSelect.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0 && r < currentList.size()) {
                onPick.accept(currentList.get(r));
                dispose();
            }
        });
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnSelect);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        filter();
    }

    private void filter() {
        String t = (String) cboType.getSelectedItem();
        String s = (String) cboStatus.getSelectedItem();
        currentList = allRooms.stream()
                .filter(r -> "Tất cả".equals(t) || r.getRoomTypeName().equalsIgnoreCase(t))
                .filter(r -> {
                    if ("Cần dọn phòng".equals(s)) return r.isCleaningRequested();
                    return "Tất cả".equals(s) || r.getStatus().getDisplayName().equalsIgnoreCase(s);
                })
                .collect(Collectors.toList());
        model.setRowCount(0);
        NumberFormat vnd = NumberFormat.getNumberInstance(HotelBookingApp.VI_LOCALE);
        for (Room r : currentList) {
            model.addRow(new Object[]{
                    r.getRoomNumber(), "Tầng " + r.getFloor(), r.getRoomTypeName(),
                    vnd.format(r.getPricePerNight()) + " đ", r.getStatus().getDisplayName(),
                    r.isCleaningRequested() ? "Đang yêu cầu dọn" : "Đã sạch"
            });
        }
    }
}
