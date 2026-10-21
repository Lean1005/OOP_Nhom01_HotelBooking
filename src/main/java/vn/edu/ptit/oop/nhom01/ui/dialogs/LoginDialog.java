package vn.edu.ptit.oop.nhom01.ui.dialogs;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;

import vn.edu.ptit.oop.nhom01.model.*;
import javax.swing.JToolBar;
import javax.swing.JRadioButton;

public class LoginDialog extends javax.swing.JDialog {
    private final UserService userService;
    private boolean loggedIn = false;
    private User loggedInUser;

    public LoginDialog(JFrame parent, UserService userService) {
        super(parent, "Đăng nhập Nhân viên / Chủ khách sạn", true);
        setTitle("ĐĂNG NHẬP ");
        this.userService = userService;
        setSize(380, 220);
        setLocationRelativeTo(parent);
        getContentPane().setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        p.setLayout(null);
        JLabel label = new JLabel("Tên đăng nhập:");
        label.setBounds(20, 20, 158, 29);
        p.add(label); 
        JTextField txtUser = new JTextField("admin");
        txtUser.setBounds(161, 20, 134, 29);
        p.add(txtUser);
        JLabel label_1 = new JLabel("Mật khẩu:");
        label_1.setBounds(20, 59, 158, 29);
        p.add(label_1); 
         JPasswordField txtPass = new JPasswordField("123456");
         txtPass.setBounds(161, 59, 134, 29);
         p.add(txtPass);
        
         JButton btnHidePW = new JButton("👁");
         btnHidePW.setBounds(303, 59, 43, 29);
         btnHidePW.setBackground(new Color(255, 255, 255));
         btnHidePW.addActionListener(e->{
 				if (txtPass.getEchoChar() == 0) {
         	        // Đang hiện mật khẩu -> chuyển sang ẩn
         	        txtPass.setEchoChar('*');
         	        btnHidePW.setText("👁");
         	    } else {
         	        // Đang ẩn mật khẩu -> chuyển sang hiện
         	        txtPass.setEchoChar((char) 0);
         	        btnHidePW.setText("🙈");
         	    }
         	});
         p.add(btnHidePW);
         
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogin.addActionListener(e -> {
            try {
                loggedInUser = userService.login(txtUser.getText(), new String(txtPass.getPassword()));
                loggedIn = true;
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công: " + loggedInUser.getFullName());
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel btnP = new JPanel();
        btnP.add(btnLogin);

        getContentPane().add(p, BorderLayout.CENTER);
        getContentPane().add(btnP, BorderLayout.SOUTH);
    }

    public boolean isLoggedIn() { return loggedIn; }
    public User getLoggedInUser() { return loggedInUser; }
}

