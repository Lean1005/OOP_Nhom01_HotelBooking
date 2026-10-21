package vn.edu.ptit.oop.nhom08;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=HotelBookingDB;encrypt=true;trustServerCertificate=true;";
            String user = "sa";        
            String password = "28112006";    
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Kết nối thất bại: " + e.getMessage());
        }
        return conn;
    }
}