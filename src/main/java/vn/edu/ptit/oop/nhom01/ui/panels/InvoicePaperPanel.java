package vn.edu.ptit.oop.nhom08.ui.panels;

import vn.edu.ptit.oop.nhom08.model.Booking;
import vn.edu.ptit.oop.nhom08.model.Guest;
import vn.edu.ptit.oop.nhom08.model.Invoice;
import vn.edu.ptit.oop.nhom08.model.Room;
import vn.edu.ptit.oop.nhom08.model.ServiceCharge;
import vn.edu.ptit.oop.nhom08.model.ServiceType;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Panel vẽ tờ Hóa đơn GTGT (Hóa đơn đỏ) kèm hình ảnh dịch vụ và con dấu mộc đỏ.
 * Phụ trách: Nguyễn Lê An - MSSV: N24DCAT001 (Thành viên 8)
 */
public class InvoicePaperPanel extends JPanel {

    private Invoice invoice;
    private boolean paidStampVisible = true;

    private static final Locale VI_LOCALE = Locale.forLanguageTag("vi-VN");
    private static final NumberFormat CURRENCY_FMT = NumberFormat.getInstance(VI_LOCALE);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public InvoicePaperPanel(Invoice invoice) {
        this.invoice = invoice;
        setPreferredSize(new Dimension(540, 680));
        setBackground(new Color(255, 253, 248));
    }

    public InvoicePaperPanel() {
        this(null);
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        repaint();
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setPaidStampVisible(boolean visible) {
        this.paidStampVisible = visible;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int margin = 20;

        // 1. Vẽ khung viền đôi hóa đơn đỏ
        g2.setColor(new Color(190, 30, 45));
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawRect(margin, margin, w - margin * 2, h - margin * 2);
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRect(margin + 4, margin + 4, w - (margin + 4) * 2, h - (margin + 4) * 2);

        if (invoice == null || invoice.getBooking() == null) {
            g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
            g2.drawString("Chưa có dữ liệu hóa đơn", w / 2 - 80, h / 2);
            g2.dispose();
            return;
        }

        Booking booking = invoice.getBooking();
        Room room = booking.getRoom();
        Guest primaryGuest = booking.getPrimaryGuest();

        int y = margin + 28;
        int leftX = margin + 18;
        int rightX = w - margin - 18;

        // 2. Tiêu đề Công ty & Hóa đơn GTGT
        g2.setColor(new Color(180, 20, 35));
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString("CÔNG TY TNHH NHÓM8HOTELBOOKING", leftX, y);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(90, 90, 90));
        g2.drawString("MST: 0318889999  |  Đ/c: 97 Man Thiện, TP. Thủ Đức, TP.HCM", leftX, y + 16);

        y += 42;
        g2.setColor(new Color(190, 20, 35));
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        String title = "HÓA ĐƠN GIÁ TRỊ GIA TĂNG (CHECK-OUT)";
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(title, (w - fmTitle.stringWidth(title)) / 2, y);

        y += 18;
        g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
        g2.setColor(new Color(100, 100, 100));
        String subTitle = "Mã HĐ: " + invoice.getInvoiceId() + "   |   Mã Đặt phòng: " + booking.getBookingId();
        FontMetrics fmSub = g2.getFontMetrics();
        g2.drawString(subTitle, (w - fmSub.stringWidth(subTitle)) / 2, y);

        // Đường kẻ phân cách
        y += 12;
        g2.setColor(new Color(220, 180, 180));
        g2.drawLine(leftX, y, rightX, y);

        // 3. Thông tin khách hàng & phòng
        y += 22;
        g2.setColor(new Color(40, 40, 40));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        String guestName = primaryGuest != null ? primaryGuest.getName() : "Khách lẻ";
        String guestCccd = primaryGuest != null ? primaryGuest.getCccd() : "N/A";
        String guestPhone = primaryGuest != null ? primaryGuest.getPhone() : "N/A";
        int totalGuests = booking.getGuests() != null ? booking.getGuests().size() : 1;

        g2.drawString("Khách hàng đại diện: " + guestName + " (" + totalGuests + " khách)", leftX, y);
        y += 18;
        g2.drawString("CCCD: " + guestCccd + "     |     SĐT: " + guestPhone, leftX, y);
        y += 18;
        String roomInfo = room != null
                ? ("Phòng " + room.getRoomNumber() + " - " + room.getRoomTypeName() + " (Tầng " + room.getFloor() + ")")
                : "N/A";
        g2.drawString("Thông tin phòng: " + roomInfo, leftX, y);
        y += 18;
        g2.drawString("Nhận phòng: " + booking.getCheckIn().format(DATE_FMT)
                + "   |   Trả phòng: " + booking.getCheckOut().format(DATE_FMT)
                + "   (" + booking.nights() + " đêm)", leftX, y);
        y += 18;
        String policyName = invoice.getPricingPolicy() != null
                ? invoice.getPricingPolicy().getPolicyName()
                : "Giá tiêu chuẩn";
        g2.drawString("Chính sách giá áp dụng: " + policyName, leftX, y);

        // 4. Bảng chi tiết dịch vụ thanh toán
        y += 16;
        int tableTop = y;
        int rowH = 24;
        g2.setColor(new Color(245, 225, 225));
        g2.fillRect(leftX, tableTop, rightX - leftX, rowH);
        g2.setColor(new Color(180, 30, 45));
        g2.drawRect(leftX, tableTop, rightX - leftX, rowH);

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("STT", leftX + 8, tableTop + 16);
        g2.drawString("Hạng mục / Dịch vụ", leftX + 45, tableTop + 16);
        g2.drawString("SL", rightX - 155, tableTop + 16);
        g2.drawString("Thành tiền (VNĐ)", rightX - 110, tableTop + 16);

        y = tableTop + rowH;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(new Color(30, 30, 30));

        // Dòng 1: Tiền phòng
        int stt = 1;
        double roomCost = invoice.calculateRoomCost();
        drawTableRow(g2, leftX, rightX, y, rowH, String.valueOf(stt++),
                "Tiền phòng (" + booking.nights() + " đêm)",
                String.valueOf(booking.nights()),
                formatVnd(roomCost));
        y += rowH;

        // Các dòng phụ phí dịch vụ
        List<ServiceCharge> charges = invoice.getServiceCharges();
        if (charges != null) {
            for (ServiceCharge sc : charges) {
                drawTableRow(g2, leftX, rightX, y, rowH, String.valueOf(stt++),
                        sc.getDescription(),
                        String.valueOf(sc.getQuantity()),
                        formatVnd(sc.getTotalAmount()));
                y += rowH;
            }
        }

        // 5. Dòng Tổng cộng thanh toán
        g2.setColor(new Color(252, 238, 238));
        g2.fillRect(leftX, y, rightX - leftX, rowH + 4);
        g2.setColor(new Color(180, 30, 45));
        g2.drawRect(leftX, y, rightX - leftX, rowH + 4);

        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString("TỔNG CỘNG THANH TOÁN:", leftX + 12, y + 18);
        String totalStr = formatVnd(invoice.calculateTotal()) + " đ";
        FontMetrics fmTotal = g2.getFontMetrics();
        g2.drawString(totalStr, rightX - fmTotal.stringWidth(totalStr) - 10, y + 18);

        y += rowH + 22;

        // 6. Vẽ hình ảnh minh họa các dịch vụ đã sử dụng
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(90, 90, 90));
        g2.drawString("Dịch vụ đi kèm đã sử dụng:", leftX, y);
        y += 10;
        drawServiceBadges(g2, leftX, y, charges);

        // 7. Vẽ con dấu mộc đỏ xoay nghiêng của CÔNG TY TNHH NHÓM8HOTELBOOKING
        if (paidStampVisible) {
            drawRedCompanyStamp(g2, rightX - 135, h - margin - 95);
        }

        g2.dispose();
    }

    private void drawTableRow(Graphics2D g2, int leftX, int rightX, int y, int rowH,
                              String stt, String desc, String qty, String amount) {
        g2.setColor(new Color(210, 210, 210));
        g2.drawRect(leftX, y, rightX - leftX, rowH);
        g2.setColor(new Color(35, 35, 35));
        g2.drawString(stt, leftX + 12, y + 16);
        g2.drawString(desc, leftX + 45, y + 16);
        g2.drawString(qty, rightX - 150, y + 16);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(amount, rightX - fm.stringWidth(amount) - 10, y + 16);
    }

    private void drawServiceBadges(Graphics2D g2, int startX, int startY, List<ServiceCharge> charges) {
        if (charges == null || charges.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            g2.setColor(new Color(130, 130, 130));
            g2.drawString("(Không phát sinh phụ phí dịch vụ)", startX, startY + 18);
            return;
        }
        int x = startX;
        int y = startY;
        for (ServiceCharge sc : charges) {
            ServiceType type = sc.getServiceType();
            Color badgeColor = type != null ? type.getBadgeColor() : new Color(41, 128, 185);
            String label = type != null ? type.getDisplayName() : sc.getDescription();

            g2.setColor(new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 35));
            g2.fillRoundRect(x, y, 88, 54, 10, 10);
            g2.setColor(badgeColor);
            g2.setStroke(new BasicStroke(1.3f));
            g2.drawRoundRect(x, y, 88, 54, 10, 10);

            // Vẽ icon minh họa dịch vụ bằng Graphics2D
            drawServiceIcon(g2, type, x + 32, y + 8, badgeColor);

            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(label);
            g2.drawString(label, x + (88 - textW) / 2, y + 46);

            x += 96;
            if (x > getWidth() - 180) {
                x = startX;
                y += 60;
            }
        }
    }

    private void drawServiceIcon(Graphics2D g2, ServiceType type, int x, int y, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f));
        if (type == ServiceType.MINIBAR) {
            // Hình ly nước Minibar
            g2.drawPolygon(new int[]{x + 2, x + 22, x + 12}, new int[]{y + 2, y + 2, y + 14}, 3);
            g2.drawLine(x + 12, y + 14, x + 12, y + 22);
            g2.drawLine(x + 6, y + 22, x + 18, y + 22);
        } else if (type == ServiceType.LAUNDRY) {
            // Hình máy giặt
            g2.drawRoundRect(x + 3, y + 2, 18, 20, 4, 4);
            g2.drawOval(x + 7, y + 7, 10, 10);
        } else if (type == ServiceType.BREAKFAST) {
            // Hình tách cà phê / đĩa ăn sáng
            g2.drawArc(x + 3, y + 6, 16, 12, 180, 180);
            g2.drawLine(x + 3, y + 12, x + 19, y + 12);
            g2.drawLine(x + 2, y + 20, x + 22, y + 20);
        } else {
            // Hình ngôi sao / hoa Spa
            g2.drawOval(x + 4, y + 4, 16, 16);
            g2.fillOval(x + 9, y + 9, 6, 6);
        }
    }

    private void drawRedCompanyStamp(Graphics2D g2, int centerX, int centerY) {
        AffineTransform old = g2.getTransform();
        g2.rotate(Math.toRadians(-14), centerX, centerY);

        Color stampRed = new Color(210, 25, 40, 195);
        g2.setColor(stampRed);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(centerX - 68, centerY - 42, 136, 84);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawOval(centerX - 63, centerY - 37, 126, 74);

        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        String topText = "CÔNG TY TNHH NHÓM8HOTELBOOKING";
        FontMetrics fmTop = g2.getFontMetrics();
        g2.drawString(topText, centerX - fmTop.stringWidth(topText) / 2, centerY - 12);

        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        String midText = "ĐÃ THU TIỀN";
        FontMetrics fmMid = g2.getFontMetrics();
        g2.drawString(midText, centerX - fmMid.stringWidth(midText) / 2, centerY + 6);

        g2.setFont(new Font("SansSerif", Font.ITALIC, 9));
        String botText = "CHECKED OUT";
        FontMetrics fmBot = g2.getFontMetrics();
        g2.drawString(botText, centerX - fmBot.stringWidth(botText) / 2, centerY + 22);

        g2.setTransform(old);
    }

    private String formatVnd(double amount) {
        return CURRENCY_FMT.format(Math.round(amount));
    }
}
