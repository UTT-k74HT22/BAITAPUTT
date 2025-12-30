package org.example.view;

import org.example.controller.ReportDetailController;
import org.example.dto.response.ReportDetailResponse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportDetailManagementPanel extends JPanel {

    private final ReportDetailController controller = new ReportDetailController();

    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);

    private final JLabel lblSummary = new JLabel();
    private final StockChartPanel chartPanel = new StockChartPanel();

    private final JTextField txtWarehouse = new JTextField(12);
    private final JTextField txtProduct = new JTextField(12);
    private final JSpinner spFromDate;
    private final JSpinner spToDate;

    public ReportDetailManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        /* ================= FILTER ================= */
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filter.setBorder(BorderFactory.createTitledBorder("Thống kê chi tiết"));

        filter.add(new JLabel("Kho:"));
        filter.add(txtWarehouse);
        filter.add(new JLabel("Mã SP:"));
        filter.add(txtProduct);

        Date today = new Date();
        spFromDate = new JSpinner(new SpinnerDateModel(today, null, null, Calendar.DAY_OF_MONTH));
        spToDate = new JSpinner(new SpinnerDateModel(today, null, null, Calendar.DAY_OF_MONTH));
        spFromDate.setEditor(new JSpinner.DateEditor(spFromDate, "yyyy-MM-dd"));
        spToDate.setEditor(new JSpinner.DateEditor(spToDate, "yyyy-MM-dd"));

        filter.add(new JLabel("Từ ngày:"));
        filter.add(spFromDate);
        filter.add(new JLabel("Đến ngày:"));
        filter.add(spToDate);

        JButton btnLoad = new JButton("Load Report");
        btnLoad.addActionListener(e -> loadReport());
        filter.add(btnLoad);

        add(filter, BorderLayout.NORTH);

        /* ================= TABLE ================= */
        String[] cols = {
                "Kho", "Mã SP", "Tên SP", "Batch",
                "Tồn kho", "Đặt trước", "Min", "Max",
                "Loại GD", "Số lượng", "Ngày"
        };
        model.setColumnIdentifiers(cols);

        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);

        int[] widths = {160, 100, 200, 100, 80, 80, 60, 60, 100, 80, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            table.getColumnModel().getColumn(i).setCellRenderer(new StockAlertRenderer());
        }

        JScrollPane tableScroll = new JScrollPane(table);

        /* ================= CHART ================= */
        chartPanel.setPreferredSize(new Dimension(800, 220));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Biểu đồ Nhập – Xuất – Tồn"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, chartPanel);
        split.setResizeWeight(0.7);
        add(split, BorderLayout.CENTER);

        /* ================= FOOTER ================= */
        lblSummary.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblSummary.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(lblSummary, BorderLayout.SOUTH);

        /* ================= DEFAULT LOAD ================= */
        Calendar cal = Calendar.getInstance();
        spToDate.setValue(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -7);
        spFromDate.setValue(cal.getTime());

        loadReport();
    }

    /* ================= LOAD DATA ================= */
    private void loadReport() {
        model.setRowCount(0);

        String warehouse = txtWarehouse.getText().isBlank() ? null : txtWarehouse.getText();
        String product = txtProduct.getText().isBlank() ? null : txtProduct.getText();

        List<ReportDetailResponse> data =
                controller.getReport(warehouse, product,
                        (Date) spFromDate.getValue(),
                        (Date) spToDate.getValue());

        if (data == null) return;

        int totalOnHand = 0;
        int totalIn = 0;
        int totalOut = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (ReportDetailResponse r : data) {
            int onHand = safeInt(r.getQuantityOnHand());
            int qty = safeInt(r.getMovementQuantity());

            if ("IN".equalsIgnoreCase(r.getMovementType())) totalIn += qty;
            if ("OUT".equalsIgnoreCase(r.getMovementType())) totalOut += qty;

            totalOnHand += onHand;

            model.addRow(new Object[]{
                    safe(r.getWarehouseName()),
                    safe(r.getProductCode()),
                    safe(r.getProductName()),
                    safe(r.getBatchCode()),
                    onHand,
                    safeInt(r.getQuantityReserved()),
                    safeInt(r.getMinThreshold()),
                    safeInt(r.getMaxThreshold()),
                    safe(r.getMovementType()),
                    qty,
                    r.getMovementDate() != null ? sdf.format(r.getMovementDate()) : ""
            });
        }

        lblSummary.setText(
                "Tồn kho: " + totalOnHand +
                        " | Nhập: " + totalIn +
                        " | Xuất: " + totalOut
        );

        chartPanel.updateData(totalOnHand, totalIn, totalOut);
    }

    private int safeInt(Integer v) { return v != null ? v : 0; }
    private String safe(String s) { return s != null ? s : ""; }

    /* ================= RENDERER CẢNH BÁO ================= */
    private static class StockAlertRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focus, int row, int col) {

            Component c = super.getTableCellRendererComponent(table, value, selected, focus, row, col);

            int onHand = (int) table.getValueAt(row, 4);
            int min = (int) table.getValueAt(row, 6);

            if (!selected) {
                if (onHand <= min) {
                    c.setBackground(new Color(255, 180, 180)); // đỏ
                } else if (onHand <= min * 1.2) {
                    c.setBackground(new Color(255, 240, 180)); // vàng
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 245, 255));
                }
            }
            setHorizontalAlignment(col >= 4 ? RIGHT : LEFT);
            return c;
        }
    }

    /* ================= BIỂU ĐỒ ================= */
    private static class StockChartPanel extends JPanel {

        private int onHand, inQty, outQty;

        void updateData(int onHand, int inQty, int outQty) {
            this.onHand = onHand;
            this.inQty = inQty;
            this.outQty = outQty;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (onHand + inQty + outQty == 0) return;

            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            int baseY = h - 40;
            int barW = 80;

            int max = Math.max(onHand, Math.max(inQty, outQty));
            int scale = (h - 80) / max;

            drawBar(g2, w / 4, baseY, barW, inQty * scale, Color.BLUE, "Nhập");
            drawBar(g2, w / 2, baseY, barW, outQty * scale, Color.RED, "Xuất");
            drawBar(g2, 3 * w / 4, baseY, barW, onHand * scale, Color.GREEN, "Tồn");
        }

        private void drawBar(Graphics2D g, int x, int baseY, int w, int h, Color c, String label) {
            g.setColor(c);
            g.fillRect(x - w / 2, baseY - h, w, h);
            g.setColor(Color.BLACK);
            g.drawString(label, x - 15, baseY + 15);
        }
    }
}
