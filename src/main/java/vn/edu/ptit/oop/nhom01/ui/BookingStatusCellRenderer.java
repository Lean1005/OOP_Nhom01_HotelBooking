package vn.edu.ptit.oop.nhom08.ui;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Định dạng ô và tô màu dòng đặt phòng. Phụ trách: Nguyễn Văn Giác - N24DCAT021. */
public class BookingStatusCellRenderer extends DefaultTableCellRenderer {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                   boolean focused, int viewRow, int viewColumn) {
        super.getTableCellRendererComponent(table, value, selected, focused, viewRow, viewColumn);
        // Renderer được tái sử dụng cho nhiều ô, nên luôn đặt lại định dạng trước khi dùng.
        setToolTipText(null);
        setHorizontalAlignment(value instanceof Number ? SwingConstants.RIGHT : SwingConstants.LEFT);
        int modelColumn = table.convertColumnIndexToModel(viewColumn);
        if (value instanceof LocalDate) {
            setText(DATE_FORMAT.format((LocalDate) value));
            setHorizontalAlignment(SwingConstants.CENTER);
        } else if (table.getModel() instanceof BookingTableModel
                && modelColumn == BookingTableModel.TOTAL_COLUMN && value instanceof Number) {
            setText(moneyFormat.format(((Number) value).doubleValue()));
        }

        if (table.getModel() instanceof BookingTableModel && viewRow >= 0) {
            BookingTableModel model = (BookingTableModel) table.getModel();
            // Sau khi lọc/sắp xếp, vị trí trên bảng không còn là vị trí trong danh sách.
            int modelRow = table.convertRowIndexToModel(viewRow);
            String status = model.getBookingStatus(modelRow);
            setToolTipText(status);
            if (!selected) {
                setForeground(table.getForeground());
                if (model.getBookingAt(modelRow).isCheckedOut()) {
                    setBackground(new Color(235, 238, 240));
                } else if (!LocalDate.now().isBefore(model.getBookingAt(modelRow).getCheckOut())) {
                    setBackground(new Color(255, 235, 220));
                } else if (LocalDate.now().isBefore(model.getBookingAt(modelRow).getCheckIn())) {
                    setBackground(new Color(231, 242, 255));
                } else {
                    setBackground(new Color(232, 247, 237));
                }
            }
        } else if (!selected) {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        return this;
    }
}
