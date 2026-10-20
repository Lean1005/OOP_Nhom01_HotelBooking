package vn.edu.ptit.oop.nhom08.ui.dialogs;

import vn.edu.ptit.oop.nhom08.exception.InvalidBookingDataException;
import vn.edu.ptit.oop.nhom08.exception.RoomNotAvailableException;
import vn.edu.ptit.oop.nhom08.model.Booking;
import vn.edu.ptit.oop.nhom08.model.Gender;
import vn.edu.ptit.oop.nhom08.model.Guest;
import vn.edu.ptit.oop.nhom08.model.Room;
import vn.edu.ptit.oop.nhom08.model.RoomStatus;
import vn.edu.ptit.oop.nhom08.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/** Đặt một phòng cho nhiều khách. Phụ trách: Nguyễn Văn Giác - N24DCAT021. */
public class BookingDialog extends JDialog {
    private final BookingService bookingService;
    private final JComboBox<Room> roomCombo = new JComboBox<>();
    private final JSpinner checkInSpinner = dateSpinner(LocalDate.now());
    private final JSpinner checkOutSpinner = dateSpinner(LocalDate.now().plusDays(1));
    private final JTextField nameField = new JTextField(22);
    private final JTextField cccdField = new JTextField(16);
    private final JTextField phoneField = new JTextField(16);
    private final JComboBox<Gender> genderCombo = new JComboBox<>(Gender.values());
    private final List<Guest> guests = new ArrayList<>();
    private final DefaultTableModel guestTableModel = new DefaultTableModel(
            new String[]{"Họ tên", "Giới tính", "CCCD", "Điện thoại"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable guestTable = new JTable(guestTableModel);
    private final JLabel summaryLabel = new JLabel(" ");
    private final JLabel amountLabel = new JLabel(" ");
    private final JButton confirmButton = new JButton("Xác nhận đặt phòng");
    private final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private boolean confirmed;
    private Booking createdBooking;

    public BookingDialog(Frame owner, BookingService bookingService, Room preselectedRoom) {
        super(owner, "Đặt phòng - Danh sách khách lưu trú", true);
        this.bookingService = Objects.requireNonNull(bookingService, "BookingService");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        for (Room room : bookingService.getRepository().getAllRooms()) {
            roomCombo.addItem(room);
            if (preselectedRoom != null && room.getRoomNumber().equals(preselectedRoom.getRoomNumber())) {
                roomCombo.setSelectedItem(room);
            }
        }
        roomCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                           boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value instanceof Room) {
                    Room room = (Room) value;
                    setText("Phòng " + room.getRoomNumber() + " - " + room.getRoomTypeName()
                            + " - tối đa " + room.getMaxOccupancy() + " khách"
                            + (room.getStatus() == RoomStatus.MAINTENANCE ? " (Bảo trì)" : ""));
                }
                return this;
            }
        });
        initComponents();
        roomCombo.addActionListener(event -> updateSummary());
        checkInSpinner.addChangeListener(event -> updateSummary());
        checkOutSpinner.addChangeListener(event -> updateSummary());
        updateSummary();
        pack();
        setMinimumSize(new Dimension(760, 590));
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        JPanel stayPanel = new JPanel(new GridBagLayout());
        stayPanel.setBorder(BorderFactory.createTitledBorder("Phòng và thời gian lưu trú"));
        addField(stayPanel, 0, "Phòng:", roomCombo);
        addField(stayPanel, 1, "Ngày nhận (dd/MM/yyyy):", checkInSpinner);
        addField(stayPanel, 2, "Ngày trả (dd/MM/yyyy):", checkOutSpinner);
        content.add(stayPanel, BorderLayout.NORTH);

        JPanel guestsPanel = new JPanel(new BorderLayout(6, 6));
        guestsPanel.setBorder(BorderFactory.createTitledBorder("Khách cùng lưu trú"));
        JPanel guestForm = new JPanel(new GridBagLayout());
        addField(guestForm, 0, "Họ tên:", nameField);
        addField(guestForm, 1, "Giới tính:", genderCombo);
        addField(guestForm, 2, "CCCD:", cccdField);
        addField(guestForm, 3, "Điện thoại:", phoneField);
        JButton addGuestButton = new JButton("Thêm khách vào danh sách");
        addGuestButton.addActionListener(event -> addGuest());
        JButton removeGuestButton = new JButton("Xóa khách đang chọn");
        removeGuestButton.addActionListener(event -> removeGuest());
        JPanel guestActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guestActions.add(addGuestButton);
        guestActions.add(removeGuestButton);
        JPanel guestInput = new JPanel(new BorderLayout());
        guestInput.add(guestForm, BorderLayout.CENTER);
        guestInput.add(guestActions, BorderLayout.SOUTH);
        guestsPanel.add(guestInput, BorderLayout.NORTH);
        guestTable.setAutoCreateRowSorter(true);
        guestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guestTable.setRowHeight(25);
        guestTable.setPreferredScrollableViewportSize(new Dimension(700, 130));
        guestsPanel.add(new JScrollPane(guestTable), BorderLayout.CENTER);
        content.add(guestsPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        JPanel totals = new JPanel(new GridLayout(0, 1, 2, 4));
        totals.add(summaryLabel);
        totals.add(amountLabel);
        totals.add(new JLabel("Tiền trên là tiền phòng dự kiến, chưa gồm phụ phí dịch vụ."));
        bottom.add(totals, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(event -> dispose());
        confirmButton.addActionListener(event -> confirmBooking());
        actions.add(cancelButton);
        actions.add(confirmButton);
        bottom.add(actions, BorderLayout.SOUTH);
        content.add(bottom, BorderLayout.SOUTH);
        getRootPane().registerKeyboardAction(event -> dispose(), KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private static void addField(JPanel panel, int row, String label, Component component) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 6, 4, 6);
        constraints.gridy = row;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, constraints);
    }

    private static JSpinner dateSpinner(LocalDate date) {
        Date initial = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner spinner = new JSpinner(new SpinnerDateModel(initial, null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        editor.getFormat().setLenient(false);
        spinner.setEditor(editor);
        return spinner;
    }

    private LocalDate dateOf(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void addGuest() {
        try {
            Room room = (Room) roomCombo.getSelectedItem();
            if (room == null) {
                throw new IllegalArgumentException("Chưa có phòng để đặt.");
            }
            if (guests.size() >= room.getMaxOccupancy()) {
                throw new IllegalArgumentException("Số khách vượt sức chứa của phòng đã chọn.");
            }
            Guest guest = new Guest(nameField.getText().trim(), cccdField.getText().trim(),
                    phoneField.getText().trim(), (Gender) genderCombo.getSelectedItem());
            if (guests.stream().anyMatch(existing -> existing.getCccd().equals(guest.getCccd()))) {
                throw new IllegalArgumentException("CCCD này đã có trong danh sách khách.");
            }
            guests.add(guest);
            guestTableModel.addRow(new Object[]{guest.getFullName(), guest.getGender().getLabel(),
                    guest.getCccd(), guest.getPhone()});
            nameField.setText("");
            cccdField.setText("");
            phoneField.setText("");
            nameField.requestFocusInWindow();
            updateSummary();
        } catch (InvalidBookingDataException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void removeGuest() {
        int viewRow = guestTable.getSelectedRow();
        if (viewRow < 0) {
            showError("Hãy chọn khách cần xóa trong bảng.");
            return;
        }
        int modelRow = guestTable.convertRowIndexToModel(viewRow);
        guests.remove(modelRow);
        guestTableModel.removeRow(modelRow);
        updateSummary();
    }

    private void updateSummary() {
        Room room = (Room) roomCombo.getSelectedItem();
        long nights = ChronoUnit.DAYS.between(dateOf(checkInSpinner), dateOf(checkOutSpinner));
        boolean valid = room != null && room.getStatus() != RoomStatus.MAINTENANCE
                && nights > 0 && !dateOf(checkInSpinner).isBefore(LocalDate.now())
                && !guests.isEmpty() && guests.size() <= room.getMaxOccupancy();
        confirmButton.setEnabled(valid);
        if (room == null) {
            summaryLabel.setText("Chưa có phòng trong danh sách.");
        } else {
            summaryLabel.setText("Số đêm: " + Math.max(0, nights) + " | Số khách: " + guests.size()
                    + "/" + room.getMaxOccupancy()
                    + (room.getStatus() == RoomStatus.MAINTENANCE ? " | Phòng đang bảo trì" : ""));
        }
        if (room != null && nights > 0) {
            double total = bookingService.getPricingPolicy().calculateRoomTotal(room.getPricePerNight(), nights);
            amountLabel.setText("Tiền phòng dự kiến: " + moneyFormat.format(total)
                    + " (" + bookingService.getPricingPolicy().getPolicyName() + ")");
        } else {
            amountLabel.setText("Ngày trả phòng phải sau ngày nhận phòng.");
        }
    }

    private void confirmBooking() {
        if (confirmed) return;
        try {
            checkInSpinner.commitEdit();
            checkOutSpinner.commitEdit();
            Room room = (Room) roomCombo.getSelectedItem();
            if (room == null || room.getStatus() == RoomStatus.MAINTENANCE) {
                throw new IllegalArgumentException("Vui lòng chọn phòng không bảo trì.");
            }
            if (dateOf(checkInSpinner).isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày nhận phòng không được ở quá khứ.");
            }
            if (!dateOf(checkOutSpinner).isAfter(dateOf(checkInSpinner))) {
                throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng.");
            }
            if (guests.isEmpty() || guests.size() > room.getMaxOccupancy()) {
                throw new IllegalArgumentException("Cần ít nhất một khách và không vượt sức chứa phòng.");
            }
            if (!nameField.getText().trim().isEmpty() || !cccdField.getText().trim().isEmpty()
                    || !phoneField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Hãy thêm khách đang nhập vào danh sách hoặc xóa các ô nhập.");
            }
            Booking booking = new Booking("BK-" + UUID.randomUUID(), room.getRoomNumber(),
                    dateOf(checkInSpinner), dateOf(checkOutSpinner), new ArrayList<>(guests));
            // Chỉ service được ghi đơn; nếu trùng lịch, hộp thoại giữ nguyên dữ liệu để sửa.
            bookingService.createBooking(booking);
            createdBooking = booking;
            confirmed = true;
            dispose();
        } catch (ParseException exception) {
            showError("Ngày không hợp lệ. Hãy nhập đúng định dạng dd/MM/yyyy.");
        } catch (InvalidBookingDataException | RoomNotAvailableException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Kiểm tra thông tin", JOptionPane.WARNING_MESSAGE);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Booking getCreatedBooking() {
        return createdBooking;
    }
}
