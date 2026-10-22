package vn.edu.ptit.oop.nhom01.ui;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import vn.edu.ptit.oop.nhom01.common.*;
import vn.edu.ptit.oop.nhom01.model.*;
import vn.edu.ptit.oop.nhom01.repository.*;
import vn.edu.ptit.oop.nhom01.service.*;
import vn.edu.ptit.oop.nhom01.ui.dialogs.*;
import vn.edu.ptit.oop.nhom01.ui.panels.*;
import vn.edu.ptit.oop.nhom01.util.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

public class MainWindow extends BaseWindow {
    private HotelRepository repository;
    private BookingService bookingService;
    private RoomMapPanel roomMapPanel;
    private JScrollPane mapScrollPane; // Khai báo thành field của lớp để dễ refresh
    private RoomDetailPanel roomDetailPanel;
    private BookingTableModel bookingTableModel;
    private JTable bookingTable;
    private ChartWindow chartWindow;
    private User currentUser;
    private UserService userService;

    public MainWindow(User currentUser, UserService userService) {
        super("Nhóm1HotelBooking - Hệ thống Quản lý Đặt phòng Khách sạn [Nhân viên: " + currentUser.getFullName() + "]", 1280, 780);
        statusBar.setBounds(0, 699, 1266, 22);
        toolBar.setBounds(0, 0, 1266, 21);
        this.currentUser = currentUser;
        this.userService = userService;
    }

    @Override
    protected void initUI() {
        repository = new InMemoryHotelRepository();
        bookingService = new BookingService(repository);

        setupToolbarAndMenus();

        roomMapPanel = new RoomMapPanel(repository.findAllRooms(), this::onRoomSelected);
        mapScrollPane = new JScrollPane(roomMapPanel);
        mapScrollPane.setBorder(BorderFactory.createTitledBorder(
                "Sơ đồ Phòng Trực quan (Tự động co giãn khi Thêm Phòng / Tầng - Click chuột để chọn phòng)"));
        mapScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mapScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        roomDetailPanel = new RoomDetailPanel(
                () -> openBookingDialog(roomMapPanel.getSelectedRoom()),
                this::toggleCleaningRequestStatus,
                this::toggleMaintenanceStatus
        );

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapScrollPane, roomDetailPanel);
        topSplit.setResizeWeight(0.71);
        topSplit.setDividerLocation(855);

        JPanel bottomPanel = createBookingTableSection();

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, bottomPanel);
        mainSplit.setResizeWeight(0.58);
        mainSplit.setDividerLocation(435);

        getContentPane().add(mainSplit, BorderLayout.CENTER);

        Room defaultRoom = repository.findRoomByNumber("402");
        if (defaultRoom == null && !repository.findAllRooms().isEmpty()) {
            defaultRoom = repository.findAllRooms().get(0);
        }
        if (defaultRoom != null) {
            roomMapPanel.setSelectedRoom(defaultRoom);
            updateRoomDetailView(defaultRoom);
        }
        updateStatusBar();
    }

    private void setupToolbarAndMenus() {
        addToolButton(new Color(39, 174, 96), "+", "Đặt phòng mới", "Mở hộp thoại đặt phòng (hỗ trợ nhiều khách)",
                e -> openBookingDialog(roomMapPanel.getSelectedRoom()));
        addToolButton(new Color(22, 160, 133), "R", "+ Thêm Phòng", "Thêm một phòng mới vào tầng bất kỳ",
                e -> openAddRoomOrFloorDialog(0));
        addToolButton(new Color(41, 128, 185), "T", "+ Thêm Tầng", "Xây thêm một tầng mới với nhiều phòng",
                e -> openAddRoomOrFloorDialog(1));
        addToolButton(new Color(230, 126, 34), "$", "Check-out & Hóa đơn", "Thanh toán trả phòng và in hóa đơn có mộc đỏ",
                e -> openCheckOutDialog());
        addToolButton(new Color(52, 152, 219), "D", "Yêu cầu Dọn phòng", "Bật/Tắt trạng thái yêu cầu dọn phòng",
                e -> toggleCleaningRequestStatus());
        addToolButton(new Color(142, 68, 173), "F", "Lọc phòng", "Tra cứu lọc phòng theo tiêu chí",
                e -> openRoomFilterDialog());
        addToolButton(new Color(192, 57, 43), "C", "Biểu đồ Thống kê", "Xem biểu đồ Công suất & Doanh thu 14 ngày",
                e -> openChartWindow());
        addToolButton(new Color(44, 62, 80), "S", "Xuất CSV", "Xuất danh sách đặt phòng ra file CSV",
                e -> exportBookingsToCsv());

        toolBar.addSeparator(new Dimension(10, 0));
        toolBar.add(new JLabel(" Chính sách giá: "));

        PricingPolicy[] policies = { new StandardPricing(), new WeekendPricing(), new HolidayPricing() };
        JComboBox<PricingPolicy> policyCombo = new JComboBox<>(policies);
        policyCombo.setMaximumSize(new Dimension(215, 28));
        policyCombo.addActionListener(e -> {
            PricingPolicy selected = (PricingPolicy) policyCombo.getSelectedItem();
            if (selected != null) {
                bookingService.setPricingPolicy(selected);
                updateRoomDetailView(roomMapPanel.getSelectedRoom());
                bookingTableModel.fireTableDataChanged();
                setStatus("Đã đổi chính sách giá: " + selected.getPolicyName());
            }
        });
        toolBar.add(policyCombo);

        JMenuItem addRoomMenu = new JMenuItem("Thêm Phòng mới...");
        addRoomMenu.addActionListener(e -> openAddRoomOrFloorDialog(0));
        JMenuItem addFloorMenu = new JMenuItem("Thêm Tầng mới...");
        addFloorMenu.addActionListener(e -> openAddRoomOrFloorDialog(1));
        fileMenu.insert(addRoomMenu, 0);
        fileMenu.insert(addFloorMenu, 1);
        fileMenu.insertSeparator(2);

        JMenuItem showChartItem = new JMenuItem("Mở cửa sổ Biểu đồ Thống kê");
        showChartItem.addActionListener(e -> openChartWindow());
        viewMenu.add(showChartItem);
    }

    private JPanel createBookingTableSection() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách Đặt phòng (Hiển thị đầy đủ số lượng khách, giới tính & trạng thái Check-out)"));

        bookingTableModel = new BookingTableModel(repository.findAllBookings(), bookingService);
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setRowHeight(26);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        BookingStatusCellRenderer renderer = new BookingStatusCellRenderer();
        for (int i = 0; i < bookingTable.getColumnCount(); i++) {
            bookingTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        TableRowSorter<BookingTableModel> sorter = new TableRowSorter<>(bookingTableModel);
        bookingTable.setRowSorter(sorter);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        topBar.add(new JLabel("Tìm nhanh (Số phòng / Tên khách / CCCD):"));
        JTextField txtSearch = new JTextField(22);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String kw = txtSearch.getText().trim();
                sorter.setRowFilter(kw.isEmpty() ? null :
                        RowFilter.regexFilter("(?i)" + Pattern.quote(kw), 1, 2, 4));
            }
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
        });
        topBar.add(txtSearch);

        JButton btnCheckOut = new JButton("Check-out & Lập Hóa đơn cho khách");
        btnCheckOut.setBackground(new Color(255, 128, 128));
        btnCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCheckOut.addActionListener(e -> openCheckOutDialog());
        topBar.add(btnCheckOut);

        panel.add(topBar, BorderLayout.NORTH);
        
        JButton btnCheckIn = new JButton("Check-in cho khách");
        btnCheckIn.setBackground(new Color(46, 204, 113)); // Màu xanh lá nổi bật
        btnCheckIn.setForeground(new Color(0, 0, 0));
        btnCheckIn.addActionListener(e->performCheckIn());
        btnCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        topBar.add(btnCheckIn);
        panel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);
        return panel;
    }

    private void updateRoomDetailView(Room room) {
        if (room == null) return;
        Booking activeBooking = bookingService.findActiveBookingByRoom(room);
        roomDetailPanel.displayRoom(room, bookingService.getPricingPolicy(), activeBooking);
    }

    private void onRoomSelected(Room room) {
        updateRoomDetailView(room);
        String cleanNote = room.isCleaningRequested() ? " | [ĐANG YÊU CẦU DỌN PHÒNG]" : "";
        setStatus("Đang chọn Phòng " + room.getRoomNumber() + " (" + room.getRoomTypeName()
                + " - " + room.getStatus().getDisplayName() + ")" + cleanNote);
    }

    private void openAddRoomOrFloorDialog(int defaultTabIndex) {
        int currentFloor = (roomMapPanel.getSelectedRoom() != null)
                ? roomMapPanel.getSelectedRoom().getFloor() : 1;
        AddRoomFloorDialog dialog = new AddRoomFloorDialog(this, bookingService, currentFloor, defaultTabIndex);
        dialog.setVisible(true);
        if (dialog.isChanged()) {
            Room newlyAdded = dialog.getLastAddedRoom();
            refreshAllViews();
            if (newlyAdded != null) {
                roomMapPanel.setSelectedRoom(newlyAdded);
            }
            setStatus(dialog.getActionSummaryMessage());
        }
    }

    private void toggleCleaningRequestStatus() {
        Room selected = roomMapPanel.getSelectedRoom();
        if (selected == null) return;
        selected.setCleaningRequested(!selected.isCleaningRequested());
        
        // CẬP NHẬT NGAY LẬP TỨC XUỐNG SQL SERVER
        repository.updateRoom(selected);
        
        refreshAllViews();
        if (selected.isCleaningRequested()) {
            setStatus("Đã gửi YÊU CẦU DỌN PHÒNG cho Phòng " + selected.getRoomNumber());
        } else {
            setStatus("Đã xác nhận DỌN PHÒNG XONG cho Phòng " + selected.getRoomNumber());
        }
    }

    private void toggleMaintenanceStatus() {
        Room selected = roomMapPanel.getSelectedRoom();
        if (selected == null) return;
        if (selected.getStatus() == RoomStatus.OCCUPIED) {
            JOptionPane.showMessageDialog(this, "Phòng đang có khách ở, không thể chuyển sang Bảo trì!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selected.setStatus(selected.getStatus() == RoomStatus.MAINTENANCE
                ? RoomStatus.AVAILABLE : RoomStatus.MAINTENANCE);
                
        // CẬP NHẬT NGAY LẬP TỨC XUỐNG SQL SERVER
        repository.updateRoom(selected);
        
        refreshAllViews();
        if (selected.getStatus() == RoomStatus.MAINTENANCE) {
            setStatus("Đã chuyển Phòng " + selected.getRoomNumber() + " sang trạng thái BẢO TRÌ");
        } else {
            setStatus("Đã chuyển Phòng " + selected.getRoomNumber() + " sang trạng thái TRỐNG (sẵn sàng đón khách)");
        }
    }
    private void openBookingDialog(Room preSelectedRoom) {
        // Khởi tạo dialog đặt phòng với service và phòng đang chọn sẵn
        BookingDialog dialog = new BookingDialog(this, bookingService, preSelectedRoom);
        dialog.setVisible(true); // Chương trình sẽ đứng lại đây chờ người dùng thao tác xong
        
        // Sau khi dialog đóng lại, kiểm tra xem người dùng đã bấm xác nhận thành công chưa
        if (dialog.isConfirmed()) {
            // GỌI HÀM LÀM MỚI NGAY LẬP TỨC ĐỂ ĐẨY DỮ LIỆU LÊN GIAO DIỆN CHÍNH
            refreshAllViews();
            // Tự động chọn lại đúng phòng vừa đặt để người dùng thấy ngay trạng thái mới
            Booking newBooking = dialog.getCreatedBooking();
            if (newBooking != null && newBooking.getRoom() != null) {
                String bookedRoomNo = newBooking.getRoom().getRoomNumber();
                Room freshRoom = repository.findRoomByNumber(bookedRoomNo);
                if (freshRoom != null && roomMapPanel != null) {
                    roomMapPanel.setSelectedRoom(freshRoom);
                    updateRoomDetailView(freshRoom);
                }
            }
            
            setStatus("Đặt phòng thành công! Sơ đồ phòng và danh sách đã được cập nhật.");
        }
    }
    
    private void openCheckOutDialog() {
        int viewRow = bookingTable.getSelectedRow();
        
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng đặt phòng trong bảng bên dưới để Check-out!",
                    "Chưa chọn đơn đặt phòng", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = bookingTable.convertRowIndexToModel(viewRow);
        Booking selectedBooking = bookingTableModel.getBookingAt(modelRow);
        String status = String.valueOf(bookingTable.getModel().getValueAt(modelRow, 11));

        if (selectedBooking.isCheckedOut()||"Đặt phòng thành công".equals(status)) {
            JOptionPane.showMessageDialog(this, "Đơn đặt phòng này chưa thực hiện thủ tục check-in hoặc đã được Check-out trước đó!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InvoiceDialog dialog = new InvoiceDialog(this, selectedBooking, bookingService);
        dialog.setVisible(true);
        if (dialog.isPaid()) {
            // Đổi trạng thái phòng đối tượng cục bộ trong bộ nhớ ngay lập tức
            if (selectedBooking.getRoom() != null) {
                selectedBooking.getRoom().setStatus(RoomStatus.AVAILABLE);
                selectedBooking.getRoom().setCleaningRequested(true);
            }
            
            // Cập nhật lại toàn bộ giao diện ngay tức thì
            refreshAllViews();
            
            setStatus("Đã Check-out thành công phòng " + selectedBooking.getRoom().getRoomNumber()
                    + " (Phòng đã tự động bật cờ Yêu cầu dọn phòng)");
        }
    }
    private void performCheckIn() {
        int viewRow = bookingTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng đặt phòng trong bảng để Check-in!",
                    "Chưa chọn đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = bookingTable.convertRowIndexToModel(viewRow);
        Booking selectedBooking = bookingTableModel.getBookingAt(modelRow);
        
        String status = String.valueOf(bookingTable.getModel().getValueAt(modelRow, 11));
        if ("Đang lưu trú".equals(status) || selectedBooking.isCheckedOut()) {
            JOptionPane.showMessageDialog(this, "Đơn này đã được Check-in hoặc đã hoàn tất trước đó!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();
        if (!selectedBooking.getCheckIn().equals(today)) {
        	String todayStr = LocalDate.now().format(dtf);
            JOptionPane.showMessageDialog(this,
                "Không thể check-in! Hôm nay (" + todayStr + ") không phải là ngày nhận phòng của đơn này.\n" +
                "Đơn này có lịch check-in vào ngày: " + selectedBooking.getCheckIn().format(dtf),
                "Lỗi Ngày Check-in", JOptionPane.WARNING_MESSAGE);
            return; // Dừng lại, không cho phép check-in
        }
        // Cập nhật trạng thái trong CSDL:
        repository.updateBookingStatus(selectedBooking, "Đang lưu trú");
        
        if (selectedBooking.getRoom() != null) {
            selectedBooking.getRoom().setStatus(RoomStatus.OCCUPIED);
            repository.updateRoom(selectedBooking.getRoom());
        }

        // Làm mới lại toàn bộ giao diện ngay lập tức
        refreshAllViews();
        
        setStatus("Check-in thành công cho phòng " + selectedBooking.getRoom().getRoomNumber() + ". Phòng chuyển sang trạng thái Đang lưu trú.");
    }

    private void openRoomFilterDialog() {
        RoomFilterDialog dialog = new RoomFilterDialog(this, repository.findAllRooms(), room -> {
            roomMapPanel.setSelectedRoom(room);
            onRoomSelected(room);
        });
        dialog.setVisible(true);
    }

   
    private void openChartWindow() {
        if (chartWindow == null || !chartWindow.isDisplayable()) {
            chartWindow = new ChartWindow(bookingService);
        }
        chartWindow.refreshCharts();
        chartWindow.setVisible(true);
        chartWindow.toFront();
    }

    private void exportBookingsToCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("DanhSachDatPhong_Nhom8HotelBooking.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                DataFileManager.exportBookingsToCsv(repository.findAllBookings(),
                        bookingService.getPricingPolicy(), chooser.getSelectedFile().toPath());
                JOptionPane.showMessageDialog(this, "Đã xuất file CSV tại:\n" + chooser.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi ghi file: " + ex.getMessage());
            }
        }
    }

    /**
     * Phương thức đồng bộ hóa và làm mới toàn bộ giao diện dựa trên dữ liệu mới nhất từ SQL Server
     */
    private void refreshAllViews() {
        // 1. Đọc lại danh sách phòng và đơn đặt mới nhất từ SQL Server
        List<Room> latestRooms = repository.findAllRooms();
        List<Booking> latestBookings = repository.findAllBookings();

        // 2. Cập nhật dữ liệu trực tiếp vào sơ đồ phòng
        roomMapPanel.setRooms(latestRooms);

        // 3. Giữ nguyên phòng đang được chọn và cập nhật giao diện chi tiết bên phải
        Room selected = roomMapPanel.getSelectedRoom();
        if (selected != null) {
            String selectedRoomNo = selected.getRoomNumber();
            Room freshSelected = latestRooms.stream()
                    .filter(r -> r.getRoomNumber().equalsIgnoreCase(selectedRoomNo))
                    .findFirst().orElse(null);
            if (freshSelected == null) {
                freshSelected = repository.findRoomByNumber(selectedRoomNo);
            }
            if (freshSelected != null) {
                roomMapPanel.setSelectedRoom(freshSelected);
                updateRoomDetailView(freshSelected);
            }
        }

        roomMapPanel.revalidate();
        roomMapPanel.repaint();

        // 4. Cập nhật lại bảng danh sách đặt phòng
        bookingTableModel = new BookingTableModel(latestBookings, bookingService);
        bookingTable.setModel(bookingTableModel);
        
        BookingStatusCellRenderer renderer = new BookingStatusCellRenderer();
        for (int i = 0; i < bookingTable.getColumnCount(); i++) {
            bookingTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        TableRowSorter<BookingTableModel> sorter = new TableRowSorter<>(bookingTableModel);
        bookingTable.setRowSorter(sorter);

        // 5. Cập nhật biểu đồ nếu đang mở
        if (chartWindow != null && chartWindow.isDisplayable()) {
            chartWindow.refreshCharts();
        }
        
        updateStatusBar();
    }
    private void updateStatusBar() {
        long floors = repository.findAllRooms().stream().map(Room::getFloor).distinct().count();
        long avail = repository.findAllRooms().stream().filter(r -> r.getStatus() == RoomStatus.AVAILABLE).count();
        long occ = repository.findAllRooms().stream().filter(r -> r.getStatus() == RoomStatus.OCCUPIED).count();
        long needClean = repository.findAllRooms().stream().filter(r -> r.isCleaningRequested()).count();
        setStatus(String.format("Quy mô: %d tầng - %d phòng | Trống: %d | Đang có khách: %d | Cần dọn phòng: %d | Tổng đơn: %d | Chính sách: %s",
                floors, repository.findAllRooms().size(), avail, occ, needClean,
                repository.findAllBookings().size(), bookingService.getPricingPolicy().getPolicyName()));
    }

    @Override
    protected void showAbout() {
        new AboutDialog(this).setVisible(true);
    }
}
