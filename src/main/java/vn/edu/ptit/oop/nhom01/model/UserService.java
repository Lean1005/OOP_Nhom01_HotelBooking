package vn.edu.ptit.oop.nhom01.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import vn.edu.ptit.oop.nhom01.DBConnection;

public class UserService {
    private User currentUser;
    public UserService() {}
    public User login(String username, String password) throws Exception {
        if (username == null || password == null) {
            throw new Exception("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
        }

        String sql = "SELECT username, password, full_name, phone, cccd FROM NguoiDung WHERE username = ?";
        User u = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                // Kiểm tra mật khẩu
                if (!dbPassword.equals(password)) {
                    throw new Exception("Sai tên đăng nhập hoặc mật khẩu!");
                }
                // Khởi tạo đối tượng User từ dữ liệu SQL
                u = new User(
                    rs.getString("username"),
                    dbPassword,
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getString("cccd")
                );
            } else {
                throw new Exception("Sai tên đăng nhập hoặc mật khẩu!");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }

        this.currentUser = u;
        return u;
    }

    public User getCurrentUser() { 
        return currentUser; 
    }
    
    public void logout() { 
        currentUser = null; 
    }
}
