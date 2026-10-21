package vn.edu.ptit.oop.nhom08.ui.panels;

import vn.edu.ptit.oop.nhom08.model.Room;
import vn.edu.ptit.oop.nhom08.model.RoomStatus;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Panel vẽ sơ đồ phòng khách sạn theo từng tầng.
 * Có thể đặt trong JScrollPane để xem được khách sạn có nhiều phòng/tầng.
 */
public class RoomMapPanel extends JPanel implements Scrollable {
    private static final long serialVersionUID = 1L;
    private static final int ROOM_WIDTH = 116;
    private static final int ROOM_HEIGHT = 96;
    private static final int GAP_X = 14;
    private static final int GAP_Y = 14;
    private static final int MARGIN = 22;
    private static final int MAP_START_Y = 76;
    private static final int FLOOR_HEADER_HEIGHT = 28;
    private static final int FLOOR_BOTTOM_GAP = 18;

    private List<Room> roomList = new ArrayList<>();
    private final Map<Room, Rectangle> roomRectangles = new HashMap<>();
    private final Map<String, List<String>> guestNamesByRoom = new HashMap<>();
    private Room selectedRoom;
    private final RoomDetailPanel detailPanel;
    private int lastWidth = -1;

    public RoomMapPanel(RoomDetailPanel detailPanel) {
        this.detailPanel = detailPanel;
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(760, 500));
        setMinimumSize(new Dimension(500, 350));
        setToolTipText("Nhấn vào một phòng để xem chi tiết");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        // Khi vùng hiển thị thay đổi chiều rộng, tính lại số cột và chiều cao sơ đồ.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                if (width != lastWidth) {
                    lastWidth = width;
                    revalidate();
                    repaint();
                }
            }
        });
    }

    /** Cập nhật danh sách phòng; không phụ thuộc thứ tự phòng đầu vào. */
    public void updateRooms(List<Room> rooms) {
        Room oldSelection = selectedRoom;
        roomList = new ArrayList<>();
        if (rooms != null) {
            for (Room room : rooms) {
                if (room != null) {
                    roomList.add(room);
                }
            }
        }

        // Nếu dữ liệu phòng được nạp lại bằng các đối tượng Room mới, giữ lựa chọn
        // dựa trên số phòng thay vì giữ tham chiếu cũ.
        selectedRoom = findRoomByNumber(oldSelection == null ? null : oldSelection.getRoomNumber());
        roomRectangles.clear();
        revalidate();
        repaint();

        if (detailPanel != null) {
            if (selectedRoom == null) {
                detailPanel.clearDetail();
            } else {
                detailPanel.updateDetail(selectedRoom, this, getGuestNamesForRoom(selectedRoom));
            }
        }
    }

    /** Vẽ lại sơ đồ sau khi trạng thái phòng hoặc yêu cầu dọn phòng thay đổi. */
    public void refreshMap() {
        repaint();
    }

    /**
     * Cập nhật tên khách đang lưu trú theo số phòng.
     *
     * Map đầu vào có dạng: số phòng -> danh sách tên khách đang lưu trú.
     * Giá trị là danh sách rỗng nghĩa là hiện không có khách; nếu thiếu khóa,
     * panel sẽ hiển thị rằng dữ liệu khách chưa được cập nhật.
     * Việc lọc đơn đặt phòng còn hiệu lực nên do lớp quản lý nghiệp vụ thực hiện.
     */
    public void updateGuestNamesByRoom(Map<String, ? extends List<String>> guestsByRoomNumber) {
        guestNamesByRoom.clear();
        if (guestsByRoomNumber != null) {
            for (Map.Entry<String, ? extends List<String>> entry : guestsByRoomNumber.entrySet()) {
                String roomNumber = entry.getKey();
                if (roomNumber == null || roomNumber.trim().isEmpty()) {
                    continue;
                }

                List<String> names = new ArrayList<>();
                List<String> suppliedNames = entry.getValue();
                if (suppliedNames != null) {
                    for (String name : suppliedNames) {
                        if (name != null && !name.trim().isEmpty()) {
                            names.add(name.trim());
                        }
                    }
                }
                guestNamesByRoom.put(roomNumber.trim(), names);
            }
        }

        if (selectedRoom != null && detailPanel != null) {
            detailPanel.updateDetail(selectedRoom, this, getGuestNamesForRoom(selectedRoom));
        }
    }

    /** Trả về danh sách tên khách của phòng đã chọn; null nghĩa là chưa có dữ liệu. */
    public List<String> getGuestNamesForRoom(Room room) {
        if (room == null) {
            return null;
        }
        List<String> names = guestNamesByRoom.get(room.getRoomNumber());
        if (names == null && !guestNamesByRoom.containsKey(room.getRoomNumber())) {
            return null;
        }
        return Collections.unmodifiableList(new ArrayList<>(names));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        roomRectangles.clear();

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(new Color(35, 45, 60));
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("SƠ ĐỒ PHÒNG KHÁCH SẠN", MARGIN, 28);
            drawLegendItem(g2, MARGIN, 49, new Color(144, 238, 144), "Trống");
            drawLegendItem(g2, MARGIN + 150, 49, new Color(255, 99, 71), "Đang có khách");
            drawLegendItem(g2, MARGIN + 330, 49, new Color(255, 215, 0), "Bảo trì");

            if (roomList.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                g2.drawString("Chưa có dữ liệu phòng.", MARGIN, MAP_START_Y + 28);
                return;
            }

            TreeMap<Integer, List<Room>> roomsByFloor = groupRoomsByFloor();
            int columns = getColumnCount();
            int floorY = MAP_START_Y;

            for (Map.Entry<Integer, List<Room>> floorEntry : roomsByFloor.entrySet()) {
                List<Room> roomsOnFloor = floorEntry.getValue();
                int rows = (roomsOnFloor.size() + columns - 1) / columns;

                g2.setColor(new Color(235, 239, 245));
                g2.fillRoundRect(MARGIN - 6, floorY, Math.max(120, getWidth() - 2 * MARGIN + 12),
                        FLOOR_HEADER_HEIGHT - 4, 10, 10);
                g2.setColor(new Color(45, 57, 72));
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("TẦNG " + floorEntry.getKey(), MARGIN + 4, floorY + 18);

                int roomsStartY = floorY + FLOOR_HEADER_HEIGHT;
                for (int i = 0; i < roomsOnFloor.size(); i++) {
                    Room room = roomsOnFloor.get(i);
                    int column = i % columns;
                    int row = i / columns;
                    int x = MARGIN + column * (ROOM_WIDTH + GAP_X);
                    int y = roomsStartY + row * (ROOM_HEIGHT + GAP_Y);
                    Rectangle rectangle = new Rectangle(x, y, ROOM_WIDTH, ROOM_HEIGHT);
                    roomRectangles.put(room, rectangle);
                    drawRoom(g2, room, rectangle);
                }

                floorY = roomsStartY + rows * (ROOM_HEIGHT + GAP_Y) + FLOOR_BOTTOM_GAP;
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillRoundRect(x, y - 10, 13, 13, 4, 4);
        g2.setColor(new Color(65, 70, 80));
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString(label, x + 19, y + 1);
    }

    private void drawRoom(Graphics2D g2, Room room, Rectangle rectangle) {
        g2.setColor(getRoomColor(room));
        g2.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 14, 14);

        if (room.equals(selectedRoom)) {
            g2.setColor(new Color(35, 105, 210));
            g2.setStroke(new BasicStroke(3.0f));
        } else {
            g2.setColor(new Color(95, 102, 112));
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 14, 14);

        g2.setColor(new Color(30, 35, 40));
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String roomName = "P." + room.getRoomNumber();
        FontMetrics metrics = g2.getFontMetrics();
        int roomNameWidth = metrics.stringWidth(roomName);
        g2.drawString(roomName,
                rectangle.x + (rectangle.width - roomNameWidth) / 2,
                rectangle.y + 31);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        String statusText = getStatusText(room.getStatus());
        FontMetrics statusMetrics = g2.getFontMetrics();
        g2.drawString(statusText,
                rectangle.x + (rectangle.width - statusMetrics.stringWidth(statusText)) / 2,
                rectangle.y + 51);

        if (room.isCleaningRequested()) {
            String cleaningText = "[CẦN DỌN PHÒNG]";
            g2.setColor(new Color(165, 35, 35));
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            FontMetrics cleaningMetrics = g2.getFontMetrics();
            int textWidth = cleaningMetrics.stringWidth(cleaningText);
            g2.drawString(cleaningText,
                    rectangle.x + (rectangle.width - textWidth) / 2,
                    rectangle.y + 76);
        }
    }

    private Color getRoomColor(Room room) {
        if (room.getStatus() == RoomStatus.AVAILABLE) {
            return new Color(144, 238, 144);
        }
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            return new Color(255, 99, 71);
        }
        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            return new Color(255, 215, 0);
        }
        return new Color(220, 220, 220);
    }

    private String getStatusText(RoomStatus status) {
        if (status == RoomStatus.AVAILABLE) {
            return "Trống";
        }
        if (status == RoomStatus.OCCUPIED) {
            return "Đang có khách";
        }
        if (status == RoomStatus.MAINTENANCE) {
            return "Bảo trì";
        }
        return "Chưa xác định";
    }

    private void handleMouseClick(Point clickPoint) {
        for (Map.Entry<Room, Rectangle> entry : roomRectangles.entrySet()) {
            if (entry.getValue().contains(clickPoint)) {
                selectedRoom = entry.getKey();
                repaint();
                if (detailPanel != null) {
                    detailPanel.updateDetail(selectedRoom, this, getGuestNamesForRoom(selectedRoom));
                }
                return;
            }
        }
    }

    private TreeMap<Integer, List<Room>> groupRoomsByFloor() {
        TreeMap<Integer, List<Room>> grouped = new TreeMap<>();
        for (Room room : roomList) {
            grouped.computeIfAbsent(room.getFloor(), key -> new ArrayList<>()).add(room);
        }
        Comparator<Room> roomNumberComparator = Comparator.comparing(
                Room::getRoomNumber, String.CASE_INSENSITIVE_ORDER);
        for (List<Room> roomsOnFloor : grouped.values()) {
            roomsOnFloor.sort(roomNumberComparator);
        }
        return grouped;
    }

    private int getColumnCount() {
        int width = getWidth() > 0 ? getWidth() : 760;
        int usableWidth = Math.max(ROOM_WIDTH, width - 2 * MARGIN);
        return Math.max(1, (usableWidth + GAP_X) / (ROOM_WIDTH + GAP_X));
    }

    private int calculateContentHeight(int columns) {
        if (roomList.isEmpty()) {
            return 420;
        }
        int height = MAP_START_Y;
        TreeMap<Integer, List<Room>> grouped = groupRoomsByFloor();
        for (List<Room> roomsOnFloor : grouped.values()) {
            int rows = (roomsOnFloor.size() + columns - 1) / columns;
            height += FLOOR_HEADER_HEIGHT;
            height += rows * (ROOM_HEIGHT + GAP_Y);
            height += FLOOR_BOTTOM_GAP;
        }
        return Math.max(420, height + 20);
    }

    private Room findRoomByNumber(String roomNumber) {
        if (roomNumber == null) {
            return null;
        }
        for (Room room : roomList) {
            if (Objects.equals(room.getRoomNumber(), roomNumber)) {
                return room;
            }
        }
        return null;
    }

    @Override
    public Dimension getPreferredSize() {
        int width = Math.max(760, getWidth());
        int columns = getColumnCount();
        return new Dimension(width, calculateContentHeight(columns));
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(760, 500);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL
                ? Math.max(24, visibleRect.height - 24)
                : Math.max(24, visibleRect.width - 24);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
