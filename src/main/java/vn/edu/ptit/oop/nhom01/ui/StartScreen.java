package vn.edu.ptit.oop.nhom01.ui;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import vn.edu.ptit.oop.nhom01.*;
import vn.edu.ptit.oop.nhom01.common.*;
import vn.edu.ptit.oop.nhom01.exception.*;
import vn.edu.ptit.oop.nhom01.model.*;
import vn.edu.ptit.oop.nhom01.repository.*;
import vn.edu.ptit.oop.nhom01.service.*;
import vn.edu.ptit.oop.nhom01.ui.dialogs.*;
import vn.edu.ptit.oop.nhom01.ui.panels.*;
import vn.edu.ptit.oop.nhom01.util.*;

public class StartScreen extends JFrame {
    private final UserService userService = new UserService();
    private boolean authenticated = false;
    private User loggedInUser;

    public StartScreen() {
        setTitle("Hotel Booking- LOGIN ");
        setSize(495, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
        	@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                java.net.URL imgUrl = getClass().getResource("/image/Screenshot 2026-10-20 003409.png");
                if (imgUrl != null) {
                    ImageIcon imageIcon = new ImageIcon(imgUrl);
                    g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("CHÀO MỪNG BẠN ĐẾN VỚI HOTEL BOOKING", SwingConstants.CENTER);
        lblTitle.setToolTipText("");
        lblTitle.setBounds(40, 23, 401, 90);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(25, 55, 109));
                   
        JButton btnStaff = new JButton("BẮT ĐẦU");
        btnStaff.setBounds(99, 173, 281, 71);
        btnStaff.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnStaff.setContentAreaFilled(false);
        btnStaff.setOpaque(true);
        btnStaff.setBackground(new Color(0, 128, 192));
        btnStaff.setForeground(new Color(255, 255, 255));
        btnStaff.addActionListener(e -> openLoginDialog());
        panel.setLayout(null);
        panel.add(lblTitle);
        panel.add(btnStaff);

        getContentPane().add(panel, BorderLayout.CENTER);
    }
    private void openLoginDialog() {
        LoginDialog dlg = new LoginDialog(this, userService);
        dlg.setVisible(true);
        if (dlg.isLoggedIn()) {
            this.loggedInUser = dlg.getLoggedInUser();
            dispose();
            new MainWindow(loggedInUser, userService).setVisible(true);
        }
    }
}
