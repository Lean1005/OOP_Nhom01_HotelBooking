package vn.edu.ptit.oop.nhom08.common;

public enum Role {
    CUSTOMER("Khách hàng"),
    STAFF("Nhân viên / Chủ khách sạn");
    
    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}