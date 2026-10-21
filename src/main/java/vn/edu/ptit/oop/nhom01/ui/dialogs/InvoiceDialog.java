package vn.edu.ptit.oop.nhom08.ui.dialogs;

import vn.edu.ptit.oop.nhom08.model.Booking;
import vn.edu.ptit.oop.nhom08.model.Invoice;
import vn.edu.ptit.oop.nhom08.model.ServiceCharge;
import vn.edu.ptit.oop.nhom08.model.ServiceType;
import vn.edu.ptit.oop.nhom08.service.BookingService;
import vn.edu.ptit.oop.nhom08.service.PricingPolicy;
import vn.edu.ptit.oop.nhom08.service.StandardPricing;
import vn.edu.ptit.oop.nhom08.ui.panels.InvoicePaperPanel;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Hộp thoại Check-out trả phòng, tích chọn phụ phí dịch vụ và xuất hóa đơn đỏ (.PNG).
 * Phụ trách: Nguyễn Lê An - MSSV: N24DCAT001 (Thành viên 8)
 */
public class InvoiceDialog extends JDialog {

    private final Booking booking;
    private final BookingService bookingService;
    private final PricingPolicy pricingPolicy;
    private Invoice invoice;
    private boolean confirmed = false;

    private final InvoicePaperPanel invoicePaperPanel;
    private final Map<ServiceType, JCheckBox> serviceCheckBoxes = new EnumMap<>(ServiceType.class);
    private final Map<ServiceType, JSpinner> quantitySpinners = new EnumMap<>(ServiceType.class);

    public InvoiceDialog(Frame owner, Booking booking, BookingService bookingService, PricingPolicy pricingPolicy) {
        super(owner, "Check-out & Xuất Hóa đơn GTGT - Phòng "
                + (booking != null && booking.getRoom() != null ? booking.getRoom().getRoomNumber() : ""), true);
        this.booking = booking;
        this.bookingService = bookingService;
        this.pricingPolicy = pricingPolicy != null ? pricingPolicy : new StandardPricing();

        this.invoice = new Invoice("INV-" + System.currentTimeMillis() % 100000, this.booking, this.pricingPolicy);
        this.invoicePaperPanel = new InvoicePaperPanel(this.invoice);

        initComponents();
        rebuildInvoicePreview();
        pack();
        setLocationRelativeTo(owner);
    }

    public InvoiceDialog(Frame owner, Booking booking, PricingPolicy pricingPolicy) {
        this(owner, booking, null, pricingPolicy);
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));

        // Panel bên trái: Chọn phụ phí dịch vụ (Minibar, Giặt ủi, Buffet Sáng, Spa)
        JPanel servicePanel = new JPanel(new GridBagLayout());
        servicePanel.setBorder(BorderFactory.createTitledBorder("Chọn Phụ phí Dịch vụ phát sinh"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));
        int row = 0;
        for (ServiceType type : ServiceType.values()) {
            JCheckBox cb = new JCheckBox(type.getDisplayName()
                    + " (" + nf.format(Math.round(type.getDefaultUnitPrice())) + "đ)");
            cb.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
            spQty.setEnabled(false);

            cb.addActionListener(e -> {
                spQty.setEnabled(cb.isSelected());
                rebuildInvoicePreview();
            });
            spQty.addChangeListener(e -> rebuildInvoicePreview());

            serviceCheckBoxes.put(type, cb);
            quantitySpinners.put(type, spQty);

            gbc.gridx = 0;
            gbc.gridy = row;
            servicePanel.add(cb, gbc);

            gbc.gridx = 1;
            servicePanel.add(new JLabel("SL:"), gbc);

            gbc.gridx = 2;
            servicePanel.add(spQty, gbc);
            row++;
        }

        // Panel bên phải: Tờ hóa đơn trực quan
        JScrollPane previewScroll = new JScrollPane(invoicePaperPanel);
        previewScroll.setBorder(BorderFactory.createTitledBorder("Xem trước Hóa đơn Đỏ (GTGT)"));

        // Panel nút bấm phía dưới
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnExportPng = new JButton("Xuất ảnh Hóa đơn (.PNG)");
        JButton btnConfirm = new JButton("Xác nhận Check-out & Thu tiền");
        JButton btnCancel = new JButton("Đóng");

        btnConfirm.setBackground(new Color(40, 167, 69));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 12));

        btnExportPng.addActionListener(e -> exportInvoiceToPng());
        btnConfirm.addActionListener(e -> onConfirmCheckOut());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnExportPng);
        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnCancel);

        add(servicePanel, BorderLayout.WEST);
        add(previewScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void rebuildInvoicePreview() {
        Invoice updated = new Invoice(invoice.getInvoiceId(), booking, pricingPolicy);
        for (ServiceType type : ServiceType.values()) {
            JCheckBox cb = serviceCheckBoxes.get(type);
            JSpinner sp = quantitySpinners.get(type);
            if (cb != null && cb.isSelected()) {
                int qty = (Integer) sp.getValue();
                updated.addServiceCharge(new ServiceCharge(type, qty));
            }
        }
        this.invoice = updated;
        this.invoicePaperPanel.setInvoice(updated);
    }

    private void exportInvoiceToPng() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu ảnh Hóa đơn (.PNG)");
        chooser.setSelectedFile(new File("HoaDon_" + booking.getBookingId() + ".png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            try {
                int w = Math.max(invoicePaperPanel.getWidth(), 540);
                int h = Math.max(invoicePaperPanel.getHeight(), 680);
                invoicePaperPanel.setSize(w, h);
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = img.createGraphics();
                invoicePaperPanel.paint(g2);
                g2.dispose();
                ImageIO.write(img, "png", file);
                JOptionPane.showMessageDialog(this,
                        "Đã xuất hóa đơn thành công:\n" + file.getAbsolutePath(),
                        "Xuất ảnh PNG", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi lưu ảnh hóa đơn: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onConfirmCheckOut() {
        rebuildInvoicePreview();
        if (bookingService != null) {
            bookingService.checkOut(booking.getBookingId(), getSelectedCharges(), pricingPolicy);
        }
        this.confirmed = true;
        JOptionPane.showMessageDialog(this,
                "Check-out thành công cho đơn " + booking.getBookingId() + "!",
                "Hoàn tất thanh toán", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public List<ServiceCharge> getSelectedCharges() {
        return new ArrayList<>(invoice.getServiceCharges());
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}