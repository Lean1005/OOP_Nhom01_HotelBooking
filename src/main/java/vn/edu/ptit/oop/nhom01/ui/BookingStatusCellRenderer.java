package vn.edu.ptit.oop.nhom01.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class BookingStatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        int modelRow = table.convertRowIndexToModel(row);
        // Lấy giá trị trạng thái từ cột 11 (hoặc cột hiển thị trạng thái của bạn)
        String status = String.valueOf(table.getModel().getValueAt(modelRow, 11));
        
        if (!isSelected) {
            // Thiết lập màu nền và màu chữ riêng biệt cho từng trạng thái
            if ("Đã Check-out".equals(status)) {
                c.setBackground(new Color(235, 245, 238)); // Xanh lá nhạt
                setForeground(new Color(39, 110, 70));     // Chữ xanh đậm
            } else if ("Đang lưu trú".equals(status)) {
                c.setBackground(new Color(253, 237, 236)); // Đỏ nhạt / hồng nhạt
                setForeground(new Color(192, 57, 43));     // Chữ đỏ đậm
            } else if ("Đặt phòng thành công".equals(status) || "CONFIRMED".equals(status)) {
                c.setBackground(new Color(235, 243, 250)); // Xanh dương nhạt
                setForeground(new Color(41, 128, 185));    // Chữ xanh dương đậm
            } else {
                c.setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
        } else {
            // Khi dòng được chọn, ưu tiên sử dụng màu chữ và màu nền khi chọn của JTable để tránh bị chìm chữ
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        
        return c;
    }
}
