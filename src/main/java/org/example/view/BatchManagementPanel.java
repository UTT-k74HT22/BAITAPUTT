package org.example.view;

import org.example.controller.BatchController;
import org.example.dto.request.Batch.CreateBatchRequest;
import org.example.dto.request.Batch.UpdateBatchRequest;
import org.example.dto.response.Batch.BatchResponse;
import org.example.repository.impl.BatchRepositoryImpl;
import org.example.service.impl.BatchServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BatchManagementPanel extends BaseManagementPanel<BatchResponse> {

    private static final int WARNING_DAYS = 30;

    private final BatchController controller;
    private JTextField keywordField;

    private static final String[] COLS = {
            "STT",
            "Mã lô",
            "Sản phẩm",
            "Giá nhập",
            "Ngày SX",
            "Hạn dùng",
            "Nhà cung cấp",
            "Trạng thái",
            "Cảnh báo"
    };

    // ================= CONSTRUCTOR =================
    public BatchManagementPanel() {
        this.controller = new BatchController(
                new BatchServiceImpl(new BatchRepositoryImpl())
        );
        reload();
        applyWarningHighlight(); // ← gọi tô màu cột cảnh báo
        showExpiryPopupIfNeeded();
    }

    // ================= BASE TABLE =================
    @Override
    protected String titleTotal() {
        return "Tổng số lô hàng";
    }

    @Override
    protected String[] columns() {
        return COLS;
    }

    @Override
    protected int[] columnWidths() {
        // Cập nhật đúng 9 cột
        return new int[]{60, 140, 220, 120, 120, 120, 160, 120, 140};
    }

    @Override
    protected List<BatchResponse> fetchData() {
        return controller.getALl(); // fix naming
    }

    @Override
    protected Object[] mapRow(BatchResponse b, int stt) {
        return new Object[]{
                stt,
                b.getBatchCode(),
                b.getProductName(),
                b.getImportPrice(),
                b.getManufactureDate(),
                b.getExpiryDate(),
                b.getSupplierName(),
                b.getStatus(),
                resolveWarning(b.getExpiryDate())
        };
    }

    // ================= FILTER =================
    @Override
    protected JPanel buildFilters() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        keywordField = UiFactory.textField(20, 220);
        JButton searchBtn =
                UiFactory.button("Tìm", UiTheme.PRIMARY, e -> applyFilters());

        row.add(new JLabel("Từ khóa:"));
        row.add(keywordField);
        row.add(searchBtn);

        return row;
    }

    @Override
    protected void applyFilters() {
        if (sorter == null) return;

        String kw = keywordField.getText().trim().toLowerCase();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                if (kw.isEmpty()) return true;

                String hay = (
                        e.getValue(1) + " " +
                                e.getValue(2) + " " +
                                e.getValue(6)
                ).toString().toLowerCase();

                return hay.contains(kw);
            }
        });
    }

    // ================= ACTION BUTTONS =================
    @Override
    protected JPanel buildActions() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        row.add(UiFactory.button("Thêm lô", new Color(40,167,69),
                e -> openCreateDialog()));

        row.add(UiFactory.button("Sửa", new Color(255,193,7),
                e -> openEdit()));

        row.add(UiFactory.button("Ngưng sử dụng", new Color(220,53,69),
                e -> disableBatch()));

        row.add(UiFactory.button("Refresh", new Color(108,117,125),
                e -> reload()));

        row.add(UiFactory.button("Lô sắp hết hạn", new Color(255,140,0),
                e -> showExpiringOnly()));
        return row;
    }

    // ================= WARNING LOGIC =================
    private String resolveWarning(LocalDate expiry) {
        LocalDate today = LocalDate.now();
        if (expiry.isBefore(today)) return "HẾT HẠN";
        if (!expiry.isAfter(today.plusDays(WARNING_DAYS))) return "SẮP HẾT HẠN";
        return "BÌNH THƯỜNG";
    }

    private void applyWarningHighlight() {
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);

                String warning = table.getValueAt(row, 8).toString();

                if (warning.equals("HẾT HẠN"))
                    c.setBackground(new Color(255, 200, 200));
                else if (warning.equals("SẮP HẾT HẠN"))
                    c.setBackground(new Color(255, 245, 180));
                else
                    c.setBackground(Color.WHITE);

                if (isSelected)
                    c.setBackground(table.getSelectionBackground());

                return c;
            }
        });
    }
    private void showExpiringOnly() {
        if (model == null) return;

        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(WARNING_DAYS);

        // Lọc các lô sắp hết hạn hoặc đã hết hạn
        List<BatchResponse> list = controller.getALl().stream()
                .filter(b -> {
                    LocalDate expiry = b.getExpiryDate();
                    return expiry.isBefore(today) || !expiry.isAfter(warningDate);
                })
                .toList();

        // Xóa dữ liệu cũ trong table
        model.setRowCount(0);

        // Thêm dòng mới từ list đã lọc
        int stt = 1;
        for (BatchResponse b : list) {
            model.addRow(new Object[]{
                    stt++,
                    b.getBatchCode(),
                    b.getProductName(),
                    b.getImportPrice(),
                    b.getManufactureDate(),
                    b.getExpiryDate(),
                    b.getSupplierName(),
                    b.getStatus(),
                    resolveWarning(b.getExpiryDate()) // cột cảnh báo cuối
            });
        }

        // Cập nhật label tổng số
        totalLabel.setText("Lô sắp hết hạn / đã hết hạn: " + list.size());

        // Nếu bạn muốn auto scroll lên đầu bảng khi lọc
        if (!list.isEmpty()) {
            table.scrollRectToVisible(table.getCellRect(0, 0, true));
        }
    }

    private void showExpiryPopupIfNeeded() {
        List<BatchResponse> list = controller.getALl().stream()
                .filter(b -> resolveWarning(b.getExpiryDate()).contains("HẾT"))
                .collect(Collectors.toList());

        if (!list.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Có " + list.size() + " lô hàng sắp hết hạn hoặc đã hết hạn!",
                    "Cảnh báo hết hạn",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void openCreateDialog() {
        JDialog d = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Thêm lô hàng",
                true
        );

        JTextField batchCode = new JTextField();
        JTextField productId = new JTextField();
        JTextField importPrice = new JTextField();
        JTextField supplier = new JTextField();
        JTextField manufactureDate = new JTextField("2025-01-01");
        JTextField expiryDate = new JTextField("2026-01-01");

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"ACTIVE", "EXPIRED", "BLOCKED", "DEPLETED"}
        );

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Mã lô *")); form.add(batchCode);
        form.add(new JLabel("Product ID *")); form.add(productId);
        form.add(new JLabel("Giá nhập *")); form.add(importPrice);
        form.add(new JLabel("Nhà cung cấp")); form.add(supplier);
        form.add(new JLabel("Ngày SX")); form.add(manufactureDate);
        form.add(new JLabel("Hạn dùng")); form.add(expiryDate);
        form.add(new JLabel("Trạng thái")); form.add(statusBox);

        JButton save = new JButton("Lưu");
        save.addActionListener(e -> {
            try {
                CreateBatchRequest r = new CreateBatchRequest();
                r.setBatchCode(batchCode.getText().trim());
                r.setProductId(Long.parseLong(productId.getText().trim()));
                r.setImportPrice(new BigDecimal(importPrice.getText().trim()));
                r.setSupplierName(supplier.getText().trim());
                r.setManufactureDate(LocalDate.parse(manufactureDate.getText().trim()));
                r.setExpiryDate(LocalDate.parse(expiryDate.getText().trim()));
                r.setStatus(statusBox.getSelectedItem().toString());

                controller.create(r);
                d.dispose();
                reload();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(save);
        actions.add(new JButton("Hủy") {{ addActionListener(e -> d.dispose()); }});

        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void openEdit() {
        int row = getSelectedModelRow();
        if (row < 0) return;

        BatchResponse b = controller.getALl().get(row);

        JDialog d = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Cập nhật lô",
                true
        );

        JTextField expiryDate = new JTextField(b.getExpiryDate().toString());
        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"ACTIVE", "EXPIRED", "BLOCKED", "DEPLETED"}
        );
        statusBox.setSelectedItem(b.getStatus());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Hạn dùng mới")); form.add(expiryDate);
        form.add(new JLabel("Trạng thái")); form.add(statusBox);

        JButton save = new JButton("Lưu");
        save.addActionListener(e -> {
            try {
                UpdateBatchRequest r = new UpdateBatchRequest();
                r.setExpiryDate(LocalDate.parse(expiryDate.getText().trim()));
                r.setStatus(statusBox.getSelectedItem().toString());

                controller.update(b.getId(), r);
                d.dispose();
                reload();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(save);
        actions.add(new JButton("Hủy") {{ addActionListener(e -> d.dispose()); }});

        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void disableBatch() {
        int row = getSelectedModelRow();
        if (row < 0) return;

        Long batchId = controller.getALl().get(row).getId();

        if (JOptionPane.showConfirmDialog(
                this,
                "Chuyển lô này sang trạng thái BLOCKED?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {
            controller.disable(batchId);
            reload();
        }
    }
}
