package vn.edu.ptit.oop.nhom08.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import vn.edu.ptit.oop.nhom08.ui.panels.BarChartPanel;
import vn.edu.ptit.oop.nhom08.ui.panels.LineChartPanel;

import javax.swing.*;
import java.awt.*;


public class ChartWindow extends JFrame {
    private JTabbedPane tabbedPane;
    private BarChartPanel barChartPanel;
    private LineChartPanel lineChartPanel;
    private JButton btnRefresh;
    private JButton btnClose;

    static {
      
        StandardChartTheme theme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
        theme.setExtraLargeFont(new Font("Segoe UI", Font.BOLD, 18)); // Tiêu đề lớn
        theme.setLargeFont(new Font("Segoe UI", Font.BOLD, 14));      // Nhãn trục
        theme.setRegularFont(new Font("Segoe UI", Font.PLAIN, 12));    // Chú giải & giá trị
        ChartFactory.setChartTheme(theme);
    }

    public ChartWindow() {
        setTitle("Thống Kê & Báo Cáo Doanh Thu Khách Sạn - Nhóm 8");
        setSize(950, 650);
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

       
        tabbedPane = new JTabbedPane();
        barChartPanel = new BarChartPanel();
        lineChartPanel = new LineChartPanel();

        tabbedPane.addTab("Công Suất Phòng (BarChart)", barChartPanel);
        tabbedPane.addTab("Doanh Thu 14 Ngày (LineChart)", lineChartPanel);

        add(tabbedPane, BorderLayout.CENTER);

        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        btnRefresh = new JButton("Làm mới dữ liệu");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRefresh.addActionListener(e -> {
            barChartPanel.simulateRandomData();
            lineChartPanel.refreshRevenueData();
            JOptionPane.showMessageDialog(this,
                    "Đã cập nhật lại toàn bộ số liệu thống kê mới nhất!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClose.addActionListener(e -> dispose()); 

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnClose);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChartWindow window = new ChartWindow();
            window.setVisible(true);
        });
    }
}
