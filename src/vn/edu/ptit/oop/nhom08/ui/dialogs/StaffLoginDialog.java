package vn.edu.ptit.oop.nhom08.ui.dialogs;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import vn.edu.ptit.oop.nhom08.model.*;
import javax.swing.JToolBar;
import javax.swing.JRadioButton;

public class StaffLoginDialog extends javax.swing.JDialog {
    private final UserService userService;
    private boolean loggedIn = false;
    private User loggedInUser;

    public StaffLoginDialog(JFrame parent, UserService userService) {
        super(parent, "Đăng nhập Nhân viên / Chủ khách sạn", true);
        setTitle("Nhân viên / Chủ khách sạn");
        this.userService = userService;
        setSize(380, 220);
        setLocationRelativeTo(parent);
        getContentPane().setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(3, 2, 10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel("Tên đăng nhập:");
        p.add(label); 
        JTextField txtUser = new JTextField("admin");
        p.add(txtUser);
        p.add(new JLabel("Mật khẩu:")); 
         JPasswordField txtPass = new JPasswordField("123456");
         p.add(txtPass);

        JButton btnLogin = new JButton("Đăng nhập Quản trị");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogin.addActionListener(e -> {
            try {
                loggedInUser = userService.login(txtUser.getText(), new String(txtPass.getPassword()), Role.STAFF);
                loggedIn = true;
                JOptionPane.showMessageDialog(this, "Đăng nhập quản trị thành công: " + loggedInUser.getFullName());
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
