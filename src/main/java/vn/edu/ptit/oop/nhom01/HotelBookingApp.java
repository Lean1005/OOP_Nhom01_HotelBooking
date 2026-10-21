package vn.edu.ptit.oop.nhom01;

import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import vn.edu.ptit.oop.nhom01.ui.StartScreen;

public class HotelBookingApp {
    public static final Locale VI_LOCALE = Locale.forLanguageTag("vi-VN");
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new StartScreen().setVisible(true));           
    }
}
