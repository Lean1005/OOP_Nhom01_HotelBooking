package vn.edu.ptit.oop.nhom08.model;

public class User {
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String cccd;

    public User(String username, String password, String fullName, String phone, String cccd) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.cccd = cccd;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getCccd() { return cccd; }
}