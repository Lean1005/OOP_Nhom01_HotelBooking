package vn.edu.ptit.oop.nhom01.model;

import java.awt.*;

public enum ServiceType {
    MINIBAR("Minibar & Snack", new Color(230, 126, 34)),
    LAUNDRY("Giặt ủi cao cấp", new Color(52, 152, 219)),
    BREAKFAST("Buffet Sáng VIP", new Color(39, 174, 96)),
    SPA("Spa & Xông hơi", new Color(155, 89, 182));

    private final String shortTitle;
    private final Color themeColor;

    ServiceType(String shortTitle, Color themeColor) {
        this.shortTitle = shortTitle;
        this.themeColor = themeColor;
    }

    public String getShortTitle() { return shortTitle; }
    public Color getThemeColor() { return themeColor; }
}
