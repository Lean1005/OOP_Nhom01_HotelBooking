package vn.edu.ptit.oop.nhom08.ui.panels;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class BarChartPanel extends JPanel {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public BarChartPanel() {
        setLayout(new BorderLayout());
        initDataset();
        createChart();
        chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void initDataset() {
        dataset = new DefaultCategoryDataset();
        // Dữ liệu ban đầu theo 3 loại phòng của Đề tài 4
        dataset.addValue(82.5, "Công suất (%)", "Standard Room");
        dataset.addValue(68.0, "Công suất (%)", "Deluxe Room");
        dataset.addValue(90.0, "Công suất (%)", "Suite Room");
    }

    private void createChart() {
        chart = ChartFactory.createBarChart(
                "Thống Kê Công Suất Sử Dụng Phòng Khách Sạn", // Tiêu đề biểu đồ
                "Loại Phòng",                                // Nhãn trục hoành
                "Tỷ Lệ Đặt (%)",                             // Nhãn trục tung
                dataset,
                PlotOrientation.VERTICAL,
                true,   // Hiển thị chú giải (Legend)
                true,   // Hiển thị Tooltips
                false   // Không dùng URLs
        );

        // Tùy chỉnh màu sắc và đường nét giao diện
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 247, 250));
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(41, 128, 185)); // Màu xanh dương hiện đại
        renderer.setDrawBarOutline(false);
    }
    public void updateOccupancyData(double standard, double deluxe, double suite) {
        dataset.setValue(standard, "Công suất (%)", "Standard Room");
        dataset.setValue(deluxe, "Công suất (%)", "Deluxe Room");
        dataset.setValue(suite, "Công suất (%)", "Suite Room");
    }

    public void updateOccupancyData(java.util.Map<String, Double> rates) {
        if (rates == null || rates.isEmpty()) {
            simulateRandomData();
            return;
        }
        dataset.clear();
        for (java.util.Map.Entry<String, Double> entry : rates.entrySet()) {
            dataset.setValue(Math.round(entry.getValue() * 10.0) / 10.0, "Công suất (%)", entry.getKey());
        }
    }

    public void simulateRandomData() {
        Random rand = new Random();
        double standard = 50 + rand.nextDouble() * 45; // 50% - 95%
        double deluxe = 40 + rand.nextDouble() * 50;
        double suite = 60 + rand.nextDouble() * 40;
        updateOccupancyData(Math.round(standard * 10.0) / 10.0,
                Math.round(deluxe * 10.0) / 10.0,
                Math.round(suite * 10.0) / 10.0);
    }
}
