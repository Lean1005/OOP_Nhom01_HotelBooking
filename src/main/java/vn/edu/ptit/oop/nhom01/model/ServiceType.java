// Author: Nguyen Minh Tuan - MSSV: N24DCAT085
package vn.edu.ptit.oop.nhom08.model;

public enum ServiceType {
    MINIBAR("Nước ngọt & Đồ ăn vặt Minibar", 50000.0),
    LAUNDRY("Dịch vụ Giặt ủi lấy ngay", 80000.0),
    BREAKFAST_BUFFET("Buffet Sáng tiêu chuẩn", 120000.0),
    SPA("Chăm sóc Sức khỏe & Spa", 350000.0);

    private final String displayName;
    private final double defaultPrice;

    ServiceType(String displayName, double defaultPrice) {
        this.displayName = displayName;
        this.defaultPrice = defaultPrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultPrice() {
        return defaultPrice;
    }
}