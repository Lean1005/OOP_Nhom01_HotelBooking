package vn.edu.ptit.oop.nhom08.ui.dialogs;

import vn.edu.ptit.oop.nhom08.model.DeluxeRoom;
import vn.edu.ptit.oop.nhom08.model.Room;
import vn.edu.ptit.oop.nhom08.model.StandardRoom;
import vn.edu.ptit.oop.nhom08.model.SuiteRoom;
import vn.edu.ptit.oop.nhom08.service.BookingService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Thêm một phòng hoặc một tầng. Phụ trách: Nguyễn Văn Giác - N24DCAT021. */
public class AddRoomFloorDialog extends JDialog {
    private final BookingService bookingService;
    private final JTabbedPane tabs = new JTabbedPane();
    private final JComboBox<Integer> floorCombo = new JComboBox<>();
    private final JTextField roomNumberField = new JTextField(16);
    private final JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"});
    private final JSpinner bedCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JTextField viewField = new JTextField("Thành phố", 16);
    private final JCheckBox jacuzziCheckBox = new JCheckBox("Có bồn Jacuzzi", true);
    private final JPanel typeOptions = new JPanel(new CardLayout());
    private final JSpinner newFloorSpinner;
    private boolean changed;
    private Room lastAddedRoom;
    private String actionSummaryMessage = "";

    public AddRoomFloorDialog(Frame owner, BookingService bookingService, int currentFloor, int defaultTabIndex) {
        super(owner, "Thêm phòng / Thêm tầng", true);
        this.bookingService = Objects.requireNonNull(bookingService, "BookingService");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        List<Integer> floors = rooms().stream().map(Room::getFloor).distinct().sorted().collect(Collectors.toList());
        floors.forEach(floorCombo::addItem);
        if (floors.contains(currentFloor)) {
            floorCombo.setSelectedItem(currentFloor);
        }
        int nextFloor = floors.isEmpty() ? 1 : Math.min(9998, floors.get(floors.size() - 1)) + 1;
        newFloorSpinner = new JSpinner(new SpinnerNumberModel(nextFloor, 1, 9999, 1));
        initComponents();
        tabs.setSelectedIndex(defaultTabIndex == 1 ? 1 : 0);
        pack();
        setMinimumSize(new Dimension(530, 350));
        setLocationRelativeTo(owner);
    }

    private List<Room> rooms() {
        return bookingService.getRepository().getAllRooms();
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        JPanel roomPanel = new JPanel(new GridBagLayout());
        addField(roomPanel, 0, "Tầng hiện có:", floorCombo);
        addField(roomPanel, 1, "Số phòng mới:", roomNumberField);
        addField(roomPanel, 2, "Loại phòng:", roomTypeCombo);
        typeOptions.add(labeledOption("Số giường:", bedCountSpinner), "Standard");
        typeOptions.add(labeledOption("Hướng nhìn:", viewField), "Deluxe");
        typeOptions.add(labeledOption("Tiện nghi:", jacuzziCheckBox), "Suite");
        addField(roomPanel, 3, "Đặc điểm:", typeOptions);
        roomTypeCombo.addActionListener(event -> ((CardLayout) typeOptions.getLayout())
                .show(typeOptions, (String) roomTypeCombo.getSelectedItem()));
        tabs.addTab("Thêm một phòng", roomPanel);

        JPanel floorPanel = new JPanel(new GridBagLayout());
        addField(floorPanel, 0, "Số tầng mới:", newFloorSpinner);
        addField(floorPanel, 1, "Quy mô:", new JLabel("6 phòng Standard, mỗi phòng 2 giường"));
        addField(floorPanel, 2, "Số phòng:", new JLabel("Tầng × 100 + 01 đến 06 (ví dụ tầng 5: 501–506)"));
        tabs.addTab("Thêm tầng mới", floorPanel);
        content.add(tabs, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(event -> dispose());
        JButton addButton = new JButton("Xác nhận thêm");
        addButton.addActionListener(event -> {
            if (changed) return;
            try {
                if (tabs.getSelectedIndex() == 0) {
                    addSingleRoom();
                } else {
                    addFloor();
                }
                changed = true;
                dispose();
            } catch (java.text.ParseException exception) {
                showError(tabs.getSelectedIndex() == 0 ? "Số giường phải là số nguyên từ 1 đến 10."
                        : "Số tầng phải là số nguyên từ 1 đến 9999.");
            } catch (IllegalArgumentException | IllegalStateException exception) {
                showError(exception.getMessage());
            }
        });
        actions.add(cancelButton);
        actions.add(addButton);
        content.add(actions, BorderLayout.SOUTH);
        getRootPane().registerKeyboardAction(event -> dispose(), KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private static JPanel labeledOption(String label, Component component) {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private static void addField(JPanel panel, int row, String label, Component component) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 6, 8, 6);
        constraints.gridy = row;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, constraints);
    }

    private void addSingleRoom() throws java.text.ParseException {
        Integer floor = (Integer) floorCombo.getSelectedItem();
        if (floor == null) {
            throw new IllegalArgumentException("Chưa có tầng nào. Hãy thêm tầng mới trước.");
        }
        String number = roomNumberField.getText().trim();
        if (number.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập số phòng.");
        }
        if (rooms().stream().anyMatch(room -> room.getRoomNumber().equalsIgnoreCase(number))) {
            throw new IllegalArgumentException("Số phòng " + number + " đã tồn tại.");
        }
        Room room;
        switch (roomTypeCombo.getSelectedIndex()) {
            case 0:
                bedCountSpinner.commitEdit();
                int beds = ((Number) bedCountSpinner.getValue()).intValue();
                if (beds < 1 || beds > 10) {
                    throw new IllegalArgumentException("Số giường phải từ 1 đến 10.");
                }
                room = new StandardRoom(number, floor, beds);
                break;
            case 1:
                room = new DeluxeRoom(number, floor, viewField.getText().trim());
                break;
            case 2:
                room = new SuiteRoom(number, floor, jacuzziCheckBox.isSelected());
                break;
            default: throw new IllegalArgumentException("Vui lòng chọn loại phòng.");
        }
        bookingService.addSingleRoom(room);
        lastAddedRoom = room;
        actionSummaryMessage = "Đã thêm phòng " + number + " (" + room.getRoomTypeName() + ") vào tầng " + floor + ".";
    }

    private void addFloor() throws java.text.ParseException {
        newFloorSpinner.commitEdit();
        int floor = ((Number) newFloorSpinner.getValue()).intValue();
        if (floor < 1 || floor > 9999) {
            throw new IllegalArgumentException("Số tầng phải từ 1 đến 9999.");
        }
        if (rooms().stream().anyMatch(room -> room.getFloor() == floor)) {
            throw new IllegalArgumentException("Tầng " + floor + " đã tồn tại. Dùng tab Thêm một phòng để bổ sung.");
        }
        for (int index = 1; index <= 6; index++) {
            String number = String.valueOf(floor * 100 + index);
            if (rooms().stream().anyMatch(room -> room.getRoomNumber().equalsIgnoreCase(number))) {
                throw new IllegalArgumentException("Không thể thêm tầng: số phòng " + number + " đã tồn tại.");
            }
        }
        // Giữ đúng chữ ký addNewFloor(int) và quy mô 6 phòng trong BookingService hiện tại.
        bookingService.addNewFloor(floor);
        List<Room> added = rooms().stream().filter(room -> room.getFloor() == floor).collect(Collectors.toList());
        if (added.isEmpty()) {
            throw new IllegalStateException("Service chưa thêm phòng vào tầng mới.");
        }
        lastAddedRoom = added.get(0);
        actionSummaryMessage = "Đã thêm tầng " + floor + " với " + added.size() + " phòng.";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Không thể thêm phòng/tầng", JOptionPane.WARNING_MESSAGE);
    }

    public boolean isChanged() {
        return changed;
    }

    public Room getLastAddedRoom() {
        return lastAddedRoom;
    }

    public String getActionSummaryMessage() {
        return actionSummaryMessage;
    }
}
