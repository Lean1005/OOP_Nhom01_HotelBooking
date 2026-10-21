package vn.edu.ptit.oop.nhom08.ui.panels;

import vn.edu.ptit.oop.nhom08.model.Room;
import vn.edu.ptit.oop.nhom08.model.RoomStatus;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/** Hiển thị thông tin của phòng đang được chọn trên sơ đồ. */
public class RoomDetailPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final JLabel lblRoomNumber;
    private final JLabel lblRoomType;
    private final JLabel lblStatus;
    private final JLabel lblPrice;
    private final JLabel lblOccupancy;
    private final JTextArea txtAmenities;
    private final JTextArea txtGuests;
    private final JToggleButton btnCleaning;

    private Room currentRoom;
    private RoomMapPanel mapPanel;

    public RoomDetailPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Chi tiết phòng"));
        setPreferredSize(new Dimension(300, 0));
        setMinimumSize(new Dimension(260, 350));

        lblRoomNumber = new JLabel("Chọn một phòng trên sơ đồ");
        lblRoomNumber.setFont(new Font("Arial", Font.BOLD, 16));
        lblRoomType = new JLabel("Loại phòng: —");
        lblStatus = new JLabel("Trạng thái: —");
        lblPrice = new JLabel("Giá: —");
        lblOccupancy = new JLabel("Sức chứa tối đa: —");

        txtAmenities = createReadOnlyTextArea(2);
        txtAmenities.setText("Tiện nghi: —");
        txtGuests = createReadOnlyTextArea(6);
        txtGuests.setText("Chưa chọn phòng.");

        btnCleaning = new JToggleButton("Yêu cầu dọn phòng");
        btnCleaning.setAlignmentX(LEFT_ALIGNMENT);
        btnCleaning.setEnabled(false);
        btnCleaning.setFocusPainted(false);
        btnCleaning.setOpaque(true);
        btnCleaning.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btnCleaning.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentRoom == null) {
                    return;
                }

                boolean requested = btnCleaning.isSelected();
                currentRoom.setCleaningRequested(requested);
                updateCleaningButton(requested);

                if (mapPanel != null) {
                    mapPanel.refreshMap();
                }
            }
        });

        add(Box.createVerticalStrut(12));
        add(lblRoomNumber);
        add(Box.createVerticalStrut(14));
        add(lblRoomType);
        add(Box.createVerticalStrut(9));
        add(lblStatus);
        add(Box.createVerticalStrut(9));
        add(lblPrice);
        add(Box.createVerticalStrut(9));
        add(lblOccupancy);
        add(Box.createVerticalStrut(9));
        add(new JLabel("Tiện nghi:"));
        add(Box.createVerticalStrut(3));
        txtAmenities.setAlignmentX(LEFT_ALIGNMENT);
        add(txtAmenities);
        add(Box.createVerticalStrut(14));
        add(new JLabel("Khách đang lưu trú:"));
        add(Box.createVerticalStrut(4));
        JScrollPane guestsScrollPane = new JScrollPane(txtGuests);
        guestsScrollPane.setAlignmentX(LEFT_ALIGNMENT);
        guestsScrollPane.setPreferredSize(new Dimension(270, 135));
        guestsScrollPane.setMinimumSize(new Dimension(220, 90));
        guestsScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        add(guestsScrollPane);
        add(Box.createVerticalStrut(18));
        add(btnCleaning);
        add(Box.createVerticalGlue());
    }

    /** Giữ lại chữ ký phương thức cũ để tương thích với nơi đang gọi panel này. */
    public void updateDetail(Room room, RoomMapPanel map) {
        List<String> guestNames = map == null ? null : map.getGuestNamesForRoom(room);
        updateDetail(room, map, guestNames);
    }

    /** Cập nhật chi tiết phòng và danh sách khách do lớp quản lý dữ liệu cung cấp. */
    public void updateDetail(Room room, RoomMapPanel map, List<String> guestNames) {
        this.currentRoom = room;
        this.mapPanel = map;

        if (room == null) {
            clearDetail();
            return;
        }

        lblRoomNumber.setText("Phòng: " + room.getRoomNumber() + " - Tầng " + room.getFloor());
        lblRoomType.setText("Loại phòng: " + safeText(room.getRoomTypeName()));
        lblStatus.setText("Trạng thái: " + getStatusText(room.getStatus()));

        NumberFormat moneyFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        moneyFormat.setMaximumFractionDigits(0);
        moneyFormat.setMinimumFractionDigits(0);
        lblPrice.setText("Giá: " + moneyFormat.format(room.getPricePerNight()) + " VNĐ/đêm");
        lblOccupancy.setText("Sức chứa tối đa: " + room.getMaxOccupancy() + " người");
        txtAmenities.setText(safeText(room.getAmenities()));
        txtAmenities.setCaretPosition(0);
        updateGuestList(guestNames);

        btnCleaning.setEnabled(true);
        updateCleaningButton(room.isCleaningRequested());
        revalidate();
        repaint();
    }

    /** Đưa panel về trạng thái ban đầu khi chưa chọn phòng. */
    public void clearDetail() {
        currentRoom = null;
        mapPanel = null;
        lblRoomNumber.setText("Chọn một phòng trên sơ đồ");
        lblRoomType.setText("Loại phòng: —");
        lblStatus.setText("Trạng thái: —");
        lblPrice.setText("Giá: —");
        lblOccupancy.setText("Sức chứa tối đa: —");
        txtAmenities.setText("—");
        txtGuests.setText("Chưa chọn phòng.");
        btnCleaning.setSelected(false);
        btnCleaning.setText("Yêu cầu dọn phòng");
        btnCleaning.setEnabled(false);
        btnCleaning.setBackground(UIManager.getColor("Button.background"));
    }

    private JTextArea createReadOnlyTextArea(int rows) {
        JTextArea textArea = new JTextArea(rows, 18);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFocusable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setBackground(getBackground());
        textArea.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return textArea;
    }

    private void updateGuestList(List<String> guestNames) {
        if (guestNames == null) {
            txtGuests.setText("Chưa cập nhật dữ liệu khách lưu trú.");
            txtGuests.setCaretPosition(0);
            return;
        }
        if (guestNames.isEmpty()) {
            txtGuests.setText("Không có khách đang lưu trú.");
            txtGuests.setCaretPosition(0);
            return;
        }

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < guestNames.size(); i++) {
            String name = guestNames.get(i);
            if (i > 0) {
                text.append('\n');
            }
            text.append(i + 1).append(". ")
                    .append(name == null || name.trim().isEmpty() ? "Khách không rõ tên" : name.trim());
        }
        txtGuests.setText(text.toString());
        txtGuests.setCaretPosition(0);
    }

    private void updateCleaningButton(boolean requested) {
        btnCleaning.setSelected(requested);
        if (requested) {
            btnCleaning.setText("Hủy yêu cầu dọn phòng");
            btnCleaning.setBackground(new Color(255, 230, 210));
        } else {
            btnCleaning.setText("Yêu cầu dọn phòng");
            btnCleaning.setBackground(UIManager.getColor("Button.background"));
        }
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

    private String safeText(String text) {
        return text == null || text.trim().isEmpty() ? "Chưa có thông tin" : text.trim();
    }
}
