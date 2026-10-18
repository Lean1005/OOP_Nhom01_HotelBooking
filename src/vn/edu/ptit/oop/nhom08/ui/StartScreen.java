package vn.edu.ptit.oop.nhom08.ui;
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
import vn.edu.ptit.oop.nhom08.*;
import vn.edu.ptit.oop.nhom08.common.*;
import vn.edu.ptit.oop.nhom08.exception.*;
import vn.edu.ptit.oop.nhom08.model.*;
import vn.edu.ptit.oop.nhom08.repository.*;
import vn.edu.ptit.oop.nhom08.service.*;
import vn.edu.ptit.oop.nhom08.ui.dialogs.*;
import vn.edu.ptit.oop.nhom08.ui.panels.*;
import vn.edu.ptit.oop.nhom08.util.*;
import org.eclipse.wb.swing.FocusTraversalOnArray;

public class StartScreen extends JFrame {
    private final UserService userService = new UserService();
    private boolean authenticated = false;
    private User loggedInUser;

    public StartScreen() {
        
        super("Nhóm8HotelBooking - Đăng nhập & Phân quyền Hệ thống");
        setTitle("Hotel Booking- LOGIN ");
        setSize(495, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("HỆ THỐNG ĐẶT PHÒNG KHÁCH SẠN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(25, 55, 109));

        JButton btnCustomer = new JButton("Khách hàng");
        btnCustomer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCustomer.setContentAreaFilled(false); 
        btnCustomer.setOpaque(true);
        btnCustomer.setBackground(new Color(0, 128, 192));
        btnCustomer.setForeground(new Color(255, 255, 255));
        btnCustomer.addActionListener(e -> openCustomerAuthDialog());
                   
        JButton btnStaff = new JButton("Quản trị viên");
        btnStaff.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnStaff.setContentAreaFilled(false);
        btnStaff.setOpaque(true);
        btnStaff.setBackground(new Color(0, 128, 192));
        btnStaff.setForeground(new Color(255, 255, 255));
        btnStaff.addActionListener(e -> openStaffLoginDialog());
        

        panel.add(lblTitle);
        panel.add(btnCustomer);
        panel.add(btnStaff);

        getContentPane().add(panel, BorderLayout.CENTER);
        setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{getContentPane(), panel, lblTitle, btnCustomer, btnStaff}));
    }

    private void openCustomerAuthDialog() {
        CustomerAuthDialog dlg = new CustomerAuthDialog(this, userService);
        dlg.setVisible(true);
        if (dlg.isLoggedIn()) {
            this.loggedInUser = dlg.getLoggedInUser();
            dispose();
            new MainWindow(loggedInUser, userService).setVisible(true);
        }
    }

    private void openStaffLoginDialog() {
        StaffLoginDialog dlg = new StaffLoginDialog(this, userService);
        dlg.setVisible(true);
        if (dlg.isLoggedIn()) {
            this.loggedInUser = dlg.getLoggedInUser();
            dispose();
            new MainWindow(loggedInUser, userService).setVisible(true);
        }
    }
}

