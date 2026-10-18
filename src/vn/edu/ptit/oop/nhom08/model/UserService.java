package vn.edu.ptit.oop.nhom08.model;

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
import vn.edu.ptit.oop.nhom08.ui.*;
import vn.edu.ptit.oop.nhom08.ui.dialogs.*;
import vn.edu.ptit.oop.nhom08.ui.panels.*;
import vn.edu.ptit.oop.nhom08.util.*;
public class UserService {
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;

    public UserService() {
        // Tài khoản mặc định Staff / Chủ khách sạn
        users.put("admin", new User("admin", "123456", "Quản trị viên / Chủ khách sạn", Role.STAFF, "0900000001", "001200000001"));
        users.put("staff1", new User("staff1", "123456", "Lễ tân Nguyễn Văn A", Role.STAFF, "0900000002", "001200000002"));
        // Tài khoản mẫu Khách hàng
        users.put("khachhang", new User("khachhang", "123456", "Nguyễn Văn Minh", Role.CUSTOMER, "0901234567", "001204012345"));
    }

    public boolean register(String username, String password, String fullName, String phone, String cccd) throws Exception {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập và mật khẩu không được để trống!");
        }
        if (users.containsKey(username.trim())) {
            throw new Exception("Tên đăng nhập '" + username + "' đã tồn tại!");
        }
        User newUser = new User(username.trim(), password, fullName, Role.CUSTOMER, phone, cccd);
        users.put(username.trim(), newUser);
        return true;
    }

    public User login(String username, String password, Role expectedRole) throws Exception {
        if (username == null || password == null) {
            throw new Exception("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
        }
        User u = users.get(username.trim());
        if (u == null || !u.getPassword().equals(password)) {
            throw new Exception("Sai tên đăng nhập hoặc mật khẩu!");
        }
        if (u.getRole() != expectedRole) {
            throw new Exception("Tài khoản này không thuộc quyền truy cập '" + expectedRole.getDisplayName() + "'!");
        }
        this.currentUser = u;
        return u;
    }

    public User getCurrentUser() { return currentUser; }
    public void logout() { currentUser = null; }
}