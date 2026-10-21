package vn.edu.ptit.oop.nhom01.ui.panels;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class LineChartPanel extends JPanel {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public LineChartPanel() {
        setLayout(new BorderLayout());
        initDataset();
        createChart();
        chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void initDataset() {
        dataset = new DefaultCategoryDataset();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        // Dữ liệu mô phỏng doanh thu 14 ngày gần nhất (Đề tài 4 - Bước 7)
        double[] baseRevenue = {
                12.5, 14.0, 11.2, 16.8, 18.5, 22.0, 25.4,
                15.2, 13.8, 17.0, 19.2, 23.5, 27.0, 24.8
        }; // Đơn vị: Triệu VNĐ

        for (int i = 13; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateLabel = date.format(formatter);
            dataset.addValue(baseRevenue[13 - i], "Doanh thu", dateLabel);
        }
    }

    private void createChart() {
        chart = ChartFactory.createLineChart(
                "Biến Động Doanh Thu Khách Sạn 14 Ngày Gần Nhất", // Tiêu đề
                "Ngày",                                           // Trục hoành
                "Doanh Thu (Triệu VNĐ)",                          // Trục tung
                dataset,
                PlotOrientation.VERTICAL,
                true,   // Hiển thị chú giải (Legend)
                true,   // Tooltips
                false   // URLs
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setRangeGridlinePaint(Color.GRAY);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(231, 76, 60)); // Màu đỏ cam nổi bật
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));  // Độ đậm nét vẽ
        renderer.setSeriesShapesVisible(0, true);           // Hiển thị điểm nút tròn tại mỗi ngày
        plot.setRenderer(renderer);
    }

  
    public void updateRevenueData(java.util.Map<LocalDate, Double> revenueMap) {
        if (revenueMap == null || revenueMap.isEmpty()) {
            refreshRevenueData();
            return;
        }
        dataset.clear();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (int i = 13; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateLabel = date.format(formatter);
            double val = revenueMap.getOrDefault(date, 0.0);
            double amountInMillions = val / 1_000_000.0;
            dataset.addValue(Math.round(amountInMillions * 10.0) / 10.0, "Doanh thu", dateLabel);
        }
    }

    public void refreshRevenueData() {
        dataset.clear();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        Random rand = new Random();

        for (int i = 13; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateLabel = date.format(formatter);
            double revenue = 10.0 + rand.nextDouble() * 20.0; // 10 - 30 triệu VNĐ
            dataset.addValue(Math.round(revenue * 10.0) / 10.0, "Doanh thu", dateLabel);
        }
    }
}
