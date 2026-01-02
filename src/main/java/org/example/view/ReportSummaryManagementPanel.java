package org.example.view;

import org.example.controller.ReportSummaryController;
import org.example.dto.response.ReportSummaryResponse;
import org.example.service.ReportSummaryService;
import org.example.service.impl.ReportSummaryServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ReportSummaryManagementPanel extends JPanel {

    private final ReportSummaryController controller;

    private Map<String, Number> quantityData;
    private Map<String, Number> revenueData;

    // ===================== CONSTRUCTOR =====================
    public ReportSummaryManagementPanel() {
        this(new ReportSummaryController(new ReportSummaryServiceImpl()));
    }

    public ReportSummaryManagementPanel(ReportSummaryController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        loadData();
        initUI();

    }

    /* ================= LOAD DATA ================= */

    private void loadData() {
        ReportSummaryResponse d = controller.getSummary();

        quantityData = new LinkedHashMap<>();
        quantityData.put("SP", d.getTotalProducts());
        quantityData.put("DM", d.getTotalCategories());
        quantityData.put("Kho", d.getTotalWarehouses());
        quantityData.put("Tồn", d.getTotalStock());
        quantityData.put("Hết hạn", d.getExpiredBatches());

        revenueData = new LinkedHashMap<>();
        revenueData.put("Doanh thu (triệu)", d.getTotalRevenue().doubleValue() / 1_000_000
        );

    }

    /* ================= UI ================= */

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(createTitle(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
    }

    private JLabel createTitle() {
        JLabel title = new JLabel("BÁO CÁO TỔNG QUAN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        return title;
    }

    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        panel.add(createSummaryCards(), BorderLayout.NORTH);
        panel.add(createCharts(), BorderLayout.CENTER);

        return panel;
    }

    /* ================= SUMMARY ================= */

    private JPanel createSummaryCards() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 16, 16));
        panel.setOpaque(false);

        panel.add(createCard("Tổng sản phẩm", quantityData.get("SP")));
        panel.add(createCard("Tổng danh mục", quantityData.get("DM")));
        panel.add(createCard("Tổng kho", quantityData.get("Kho")));
        panel.add(createCard("Tổng tồn kho", quantityData.get("Tồn")));
        panel.add(createCard("Lô hết hạn", quantityData.get("Hết hạn")));
        panel.add(createCard("Doanh thu", formatVND(revenueData.values().iterator().next())));

        return panel;
    }

    private JPanel createCard(String title, Object value) {
        JLabel label = new JLabel(title + ": " + value);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(14, 14, 14, 14)
        ));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private String formatVND(Object value) {
        NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return vnd.format(((Number) value).doubleValue() * 1_000_000);
    }

    /* ================= CHART AREA ================= */

    private JPanel createCharts() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);

        panel.add(new BarChartPanel(
                "SỐ LƯỢNG",
                quantityData,
                new Color(13, 110, 253),
                false
        ));

        panel.add(new BarChartPanel(
                "DOANH THU (TRIỆU ĐỒNG)",
                revenueData,
                new Color(25, 135, 84),
                true
        ));

        return panel;
    }

    /* ================= BASE BAR CHART ================= */

    static class BarChartPanel extends JPanel {

        private final String title;
        private final Map<String, Number> data;
        private final Color barColor;
        private final boolean decimal;

        BarChartPanel(String title,
                      Map<String, Number> data,
                      Color barColor,
                      boolean decimal) {

            this.title = title;
            this.data = data;
            this.barColor = barColor;
            this.decimal = decimal;

            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    new EmptyBorder(16, 16, 16, 16)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int top = 30;
            int bottom = 50;
            int left = 40;

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString(title, 10, 20);

            double max = data.values().stream()
                    .mapToDouble(Number::doubleValue)
                    .max().orElse(1);

            int chartHeight = h - top - bottom;
            int availableWidth = w - left - 40;

            int barWidth;
            if (decimal) {
                barWidth = Math.min(120, availableWidth); // giới hạn cột doanh thu
            } else {
                barWidth = availableWidth / data.size();
            }


            int i = 0;
            for (var e : data.entrySet()) {
                double value = e.getValue().doubleValue();
                int barHeight = (int) ((value / max) * chartHeight);

                int x;
                if (decimal) {
                    x = left + (availableWidth - barWidth) / 2;
                } else {
                    x = left + i * barWidth;
                }

                int y = h - bottom - barHeight;

                g2.setColor(barColor);
                g2.fillRoundRect(
                        x + 12,
                        y,
                        barWidth - 24,
                        barHeight,
                        12,
                        12
                );


                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

                String txt = decimal
                        ? String.format("%.1f", value)
                        : String.valueOf((long) value);

                g2.drawString(txt, x + barWidth / 2 - 12, y - 6);
                g2.drawString(e.getKey(), x + barWidth / 2 - 20, h - 20);

                i++;
            }
        }
    }
}
