package org.example.view;

import org.example.controller.InventoryController;
import org.example.controller.WarehouseController;
import org.example.dto.request.UpdateInventoryThresholdRequest;
import org.example.dto.response.InventoryResponse;
import org.example.dto.response.WarehouseResponse;
import org.example.service.impl.InventoryServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class InventoryManagementPanel extends BaseManagementPanel<InventoryResponse> {

    private InventoryController inventoryController;
    private WarehouseController warehouseController;

    private InventoryController invCtrl() {
        if (inventoryController == null) {
            inventoryController = new InventoryController(new InventoryServiceImpl());
        }
        return inventoryController;
    }

    private WarehouseController whCtrl() {
        if (warehouseController == null) {
            warehouseController = new WarehouseController();
        }
        return warehouseController;
    }

    // ===== UI filter fields =====
    private JComboBox<WarehouseOption> warehouseFilter;
    private JComboBox<String> stockStatusFilter;
    private JTextField keywordField;

    // ===== columns =====
    // NOTE: thêm 1 cột ID ẩn ở cuối để lấy đúng record khi sort/filter
    private static final int COL_STT = 0;
    private static final int COL_WAREHOUSE = 1;
    private static final int COL_PRODUCT_CODE = 2;
    private static final int COL_PRODUCT_NAME = 3;
    private static final int COL_BATCH = 4;
    private static final int COL_EXPIRY = 5;
    private static final int COL_QTY_ON_HAND = 6;
    private static final int COL_QTY_RESERVED = 7;
    private static final int COL_QTY_AVAILABLE = 8;
    private static final int COL_MIN = 9;
    private static final int COL_MAX = 10;
    private static final int COL_STATUS = 11;
    private static final int COL_ID_HIDDEN = 12;

    private static final String[] COLS = {
            "STT",
            "Kho",
            "Mã sản phẩm",
            "Tên sản phẩm",
            "Mã lô",
            "HSD",
            "Tồn kho",
            "Đặt trước",
            "Khả dụng",
            "Min",
            "Max",
            "Trạng thái",
            "ID" // hidden
    };

    public InventoryManagementPanel() {
        super();
        reload();     // nạp data
    }

    // ================= BASE TABLE =================

    @Override
    protected String titleTotal() {
        return "Tổng số mục tồn kho";
    }

    @Override
    protected String[] columns() {
        return COLS;
    }

    @Override
    protected int[] columnWidths() {
        return new int[]{50, 120, 100, 180, 100, 90, 80, 80, 80, 60, 60, 120, 0};
    }

    @Override
    protected List<InventoryResponse> fetchData() {
        return invCtrl().getAll();
    }

    // Bổ sung cột mới nếu cần
    @Override
    protected Object[] mapRow(InventoryResponse item, int stt) {
        String status;
        if (Boolean.TRUE.equals(item.getIsLowStock())) status = "Sắp hết";
        else if (Boolean.TRUE.equals(item.getIsNearExpiry())) status = "Sắp hết hạn";
        else if (Boolean.TRUE.equals(item.getIsOverStock())) status = "Dư thừa";
        else status = "Bình thường";

        String batch = (item.getBatchCode() == null || item.getBatchCode().isBlank()) ? "-" : item.getBatchCode();
        String expiry = (item.getExpiryDate() == null) ? "-" : String.valueOf(item.getExpiryDate());

        return new Object[]{
                stt,
                item.getWarehouseName(),
                item.getProductCode(),
                item.getProductName(),
                batch,
                expiry,
                item.getQuantityOnHand(),
                item.getQuantityReserved(),
                item.getQuantityAvailable(),
                item.getMinThreshold(),
                item.getMaxThreshold(),
                status,
                item.getId() // hidden id
        };
    }

    /**
     * Các thao tác sau khi bảng được tạo thành công
     */
    @Override
    protected void afterTableCreated() {
        // 1) STT không cho sort
        sorter.setSortable(COL_STT, false);

        // 2) Ẩn cột ID khỏi view (vẫn nằm trong model để lấy)
        TableColumn idCol = table.getColumnModel().getColumn(COL_ID_HIDDEN);
        table.removeColumn(idCol);
    }

    // ================= FILTER =================

    @Override
    protected JPanel buildFilters() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        keywordField = UiFactory.textField(20, 240);
        JButton searchBtn = UiFactory.button("Tìm", UiTheme.PRIMARY, e -> applyFilters());

        // Warehouse filter
        List<WarehouseResponse> warehouses = whCtrl().getAllWarehouses();
        WarehouseOption[] options = new WarehouseOption[warehouses.size() + 1];
        options[0] = new WarehouseOption(null, "TẤT CẢ KHO");
        for (int i = 0; i < warehouses.size(); i++) {
            WarehouseResponse w = warehouses.get(i);
            options[i + 1] = new WarehouseOption(w.getId(), w.getName());
        }
        warehouseFilter = new JComboBox<>(options);
        warehouseFilter.setPreferredSize(new Dimension(200, 34));
        warehouseFilter.addActionListener(e -> applyFilters());

        stockStatusFilter = new JComboBox<>(new String[]{
                "TẤT CẢ",
                "SẮP HẾT HÀNG",
                "SẮP HẾT HẠN",
                "DƯ THỪA",
                "BÌNH THƯỜNG"
        });
        stockStatusFilter.setPreferredSize(new Dimension(180, 34));
        stockStatusFilter.addActionListener(e -> applyFilters());

        row.add(new JLabel("Từ khóa:"));
        row.add(keywordField);
        row.add(searchBtn);
        row.add(new JLabel("Kho:"));
        row.add(warehouseFilter);
        row.add(new JLabel("Trạng thái:"));
        row.add(stockStatusFilter);

        return row;
    }

    @Override
    protected void applyFilters() {
        if (sorter == null) return;

        final String kw = keywordField.getText().trim().toLowerCase();
        final WarehouseOption wh = (WarehouseOption) warehouseFilter.getSelectedItem();
        final String statusUi = String.valueOf(stockStatusFilter.getSelectedItem());

        final String expectedStatus = switch (statusUi) {
            case "SẮP HẾT HÀNG" -> "Sắp hết";
            case "SẮP HẾT HẠN" -> "Sắp hết hạn";
            case "DƯ THỪA" -> "Dư thừa";
            case "BÌNH THƯỜNG" -> "Bình thường";
            default -> "";
        };

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {

                String haystack = (
                        e.getValue(COL_WAREHOUSE) + " " +
                                e.getValue(COL_PRODUCT_CODE) + " " +
                                e.getValue(COL_PRODUCT_NAME) + " " +
                                e.getValue(COL_BATCH)
                ).toLowerCase();

                if (!kw.isEmpty() && !haystack.contains(kw)) return false;

                // Kho: so bằng equals theo name
                if (wh != null && wh.id != null) {
                    String whName = String.valueOf(e.getValue(COL_WAREHOUSE));
                    if (!whName.equals(wh.name)) return false;
                }

                if (!"TẤT CẢ".equals(statusUi)) {
                    String current = String.valueOf(e.getValue(COL_STATUS));
                    if (!current.equals(expectedStatus)) return false;
                }

                return true;
            }
        });
    }

    // ================= ACTION BUTTONS =================

    @Override
    protected JPanel buildActions() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        row.add(UiFactory.button("Cập nhật ngưỡng", new Color(255, 193, 7), e -> updateThresholds()));
        row.add(UiFactory.button("Sắp hết hàng", new Color(220, 53, 69), e -> {
            stockStatusFilter.setSelectedItem("SẮP HẾT HÀNG");
            applyFilters();
        }));
        row.add(UiFactory.button("Sắp hết hạn", new Color(255, 87, 34), e -> {
            stockStatusFilter.setSelectedItem("SẮP HẾT HẠN");
            applyFilters();
        }));
        row.add(UiFactory.button("Refresh", new Color(108, 117, 125), e -> reload()));

        return row;
    }

    // ================= ACTIONS =================

    private void updateThresholds() {
        int modelRow = getSelectedModelRow();
        if (modelRow < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để cập nhật");
            return;
        }

        // lấy id từ cột hidden trong MODEL
        Long inventoryId = (Long) model.getValueAt(modelRow, COL_ID_HIDDEN);

        String productName = String.valueOf(model.getValueAt(modelRow, COL_PRODUCT_NAME));
        String warehouseName = String.valueOf(model.getValueAt(modelRow, COL_WAREHOUSE));
        int currentMin = Integer.parseInt(String.valueOf(model.getValueAt(modelRow, COL_MIN)));
        int currentMax = Integer.parseInt(String.valueOf(model.getValueAt(modelRow, COL_MAX)));

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Cập nhật ngưỡng tồn kho",
                true
        );

        JTextField minField = new JTextField(String.valueOf(currentMin));
        JTextField maxField = new JTextField(String.valueOf(currentMax));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Sản phẩm:"));
        form.add(new JLabel(productName));
        form.add(new JLabel("Kho:"));
        form.add(new JLabel(warehouseName));
        form.add(new JLabel("Min Threshold *:"));
        form.add(minField);
        form.add(new JLabel("Max Threshold *:"));
        form.add(maxField);

        JButton save = new JButton("Lưu");
        save.addActionListener(e -> {
            try {
                int min = Integer.parseInt(minField.getText().trim());
                int max = Integer.parseInt(maxField.getText().trim());

                if (min < 0 || max < 0) throw new IllegalArgumentException("Min/Max phải >= 0");
                if (min > max) throw new IllegalArgumentException("Min không được lớn hơn Max");

                UpdateInventoryThresholdRequest request = new UpdateInventoryThresholdRequest();
                request.setMinThreshold(min);
                request.setMaxThreshold(max);

                invCtrl().updateThresholds(inventoryId, request);

                dialog.dispose();
                reload();
                JOptionPane.showMessageDialog(this, "Cập nhật ngưỡng thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Hủy");
        cancel.addActionListener(e -> dialog.dispose());
        actions.add(save);
        actions.add(cancel);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ================= HELPER =================

    private static class WarehouseOption {
        final Long id;
        final String name;

        WarehouseOption(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public String toString() {
            return name;
        }
    }
}
