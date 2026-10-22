package vn.edu.ptit.oop.nhom01.ui;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import vn.edu.ptit.oop.nhom01.model.*;
import vn.edu.ptit.oop.nhom01.ui.dialogs.*;

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

        JPanel panel = new JPanel() {};
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
