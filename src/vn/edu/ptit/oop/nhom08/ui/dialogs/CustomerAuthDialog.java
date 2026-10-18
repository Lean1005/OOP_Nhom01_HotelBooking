package vn.edu.ptit.oop.nhom08.ui.dialogs;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.border.EmptyBorder;
import vn.edu.ptit.oop.nhom08.model.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.Color;


public class CustomerAuthDialog extends JDialog{
    private final UserService userService;
    private boolean loggedIn = false;
    private User loggedInUser;

    public CustomerAuthDialog(JFrame parent, UserService userService) {
        super(parent, "Đăng nhập / Đăng ký Khách hàng", true);
        this.userService = userService;
        setSize(440, 360);
        setLocationRelativeTo(parent);
        getContentPane().setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Đăng nhập Khách hàng", createLoginPanel());
        tabs.addTab("Đăng ký tài khoản mới", createRegisterPanel());
        getContentPane().add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel createLoginPanel() {
        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JTextField txtUser = new JTextField("khachhang");
        txtUser.setBounds(128, 32, 153, 43);
        txtUser.setHorizontalAlignment(SwingConstants.CENTER);
        p.setLayout(null);

        JLabel label = new JLabel("Tên đăng nhập:");
        label.setBounds(20, 20, 185, 67);
        p.add(label); p.add(txtUser);
        JLabel label_1 = new JLabel("Mật khẩu:");
        label_1.setBounds(20, 97, 185, 67);
        p.add(label_1); 

        JPanel main = new JPanel(new BorderLayout());
        main.add(p, BorderLayout.CENTER);
        JPasswordField txtPass = new JPasswordField("123456");
        txtPass.setBounds(128, 109, 153, 43);
        txtPass.setHorizontalAlignment(SwingConstants.CENTER);
        txtPass.setEchoChar('*');
        p.add(txtPass);
        
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogin.addActionListener(e -> {
            try {
                loggedInUser = userService.login(txtUser.getText(), new String(txtPass.getPassword()), Role.CUSTOMER);
                loggedIn = true;
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công: " + loggedInUser.getFullName());
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel btnP = new JPanel(); btnP.add(btnLogin);
        
        JButton btnHidePW = new JButton("👁");
        btnHidePW.setBounds(291, 117, 43, 26);
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
        main.add(btnP, BorderLayout.SOUTH);
        return main;
    }

    private JPanel createRegisterPanel() {
        JPanel p = new JPanel(new GridLayout(6, 2, 8, 8));
        p.setBorder(new EmptyBorder(15, 20, 15, 20));

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtCccd = new JTextField();

        p.add(new JLabel("Tên đăng nhập:")); p.add(txtUser);
        p.add(new JLabel("Mật khẩu:")); p.add(txtPass);
        p.add(new JLabel("Họ và tên:")); p.add(txtName);
        p.add(new JLabel("Số điện thoại:")); p.add(txtPhone);
        p.add(new JLabel("Số CCCD:")); p.add(txtCccd);

        JButton btnReg = new JButton("Đăng ký tài khoản");
        btnReg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReg.addActionListener(e -> {
            try {
                userService.register(txtUser.getText(), new String(txtPass.getPassword()),
                        txtName.getText(), txtPhone.getText(), txtCccd.getText());
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng chuyển sang tab Đăng nhập.");
                txtUser.setText(""); txtPass.setText(""); txtName.setText(""); txtPhone.setText(""); txtCccd.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng ký", JOptionPane.WARNING_MESSAGE);
            }
        });
        JPanel btnP = new JPanel(); btnP.add(btnReg);

        JPanel main = new JPanel(new BorderLayout());
        main.add(p, BorderLayout.CENTER);
        main.add(btnP, BorderLayout.SOUTH);
        return main;
    }

    public boolean isLoggedIn() { return this.loggedIn; }
    public User getLoggedInUser() { return this.loggedInUser; }
}
