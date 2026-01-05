package org.example.view;

import org.example.controller.InventoryController;
import org.example.dto.request.UpdateInventoryThresholdRequest;
import org.example.dto.response.InventoryResponse;
import org.example.dto.response.WarehouseResponse;
import org.example.controller.WarehouseController;
import org.example.service.impl.InventoryServiceImpl;
import org.example.service.impl.WarehouseServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ============================
 * INVENTORY MANAGEMENT PANEL
 * ============================
 * 
 * Quản lý tồn kho:
 * - Xem tồn kho tất cả sản phẩm
 * - Lọc theo kho
 * - Cảnh báo sắp hết hàng / sắp hết hạn
 * - Cập nhật ngưỡng min/max
 */
public class InventoryManagementPanel extends BaseManagementPanel<InventoryResponse> {

    private InventoryController inventoryController;
    private WarehouseController warehouseController;

    private JComboBox<WarehouseOption> warehouseFilter;
    private JComboBox<String> stockStatusFilter;
    private JTextField keywordField;

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
        "Trạng thái"
    };

    // ================= CONSTRUCTOR =================

    public InventoryManagementPanel() {
        reload();
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
        return new int[]{50, 120, 100, 180, 100, 90, 80, 80, 80, 60, 60, 120};
    }

    @Override
    protected List<InventoryResponse> fetchData() {
        return inventoryController.getAll();
    }

    @Override
    protected Object[] mapRow(InventoryResponse item, int stt) {
        String status = "";
        if (Boolean.TRUE.equals(item.getIsLowStock())) {
            status = "Sắp hết";
        } else if (Boolean.TRUE.equals(item.getIsNearExpiry())) {
            status = "Sắp hết hạn";
        } else if (Boolean.TRUE.equals(item.getIsOverStock())) {
            status = "Dư thừa";
        } else {
            status = "Bình thường";
        }

        return new Object[]{
            stt,
            item.getWarehouseName(),
            item.getProductCode(),
            item.getProductName(),
            item.getBatchCode() != null ? item.getBatchCode() : "-",
            item.getExpiryDate() != null ? item.getExpiryDate() : "-",
            item.getQuantityOnHand(),
            item.getQuantityReserved(),
            item.getQuantityAvailable(),
            item.getMinThreshold(),
            item.getMaxThreshold(),
            status
        };
    }

    // ================= FILTER =================

    @Override
    protected JPanel buildFilters() {
        // Lazy initialization to avoid NPE during super() constructor call
        if (inventoryController == null) {
            inventoryController = new InventoryController(new InventoryServiceImpl());
        }
        if (warehouseController == null) {
            warehouseController = new WarehouseController();
        }

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        // Keyword search
        keywordField = UiFactory.textField(20, 240);
        JButton searchBtn = UiFactory.button("Tìm", UiTheme.PRIMARY, e -> applyFilters());

        // Warehouse filter
        List<WarehouseResponse> warehouses = warehouseController.getAllWarehouses();
        WarehouseOption[] warehouseOptions = new WarehouseOption[warehouses.size() + 1];
        warehouseOptions[0] = new WarehouseOption(null, "TẤT CẢ KHO");
        for (int i = 0; i < warehouses.size(); i++) {
            WarehouseResponse w = warehouses.get(i);
            warehouseOptions[i + 1] = new WarehouseOption(w.getId(), w.getName());
        }
        warehouseFilter = new JComboBox<>(warehouseOptions);
        warehouseFilter.setPreferredSize(new Dimension(200, 34));
        warehouseFilter.addActionListener(e -> applyFilters());

        // Stock status filter
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

        String kw = keywordField.getText().trim().toLowerCase();
        WarehouseOption selectedWarehouse = (WarehouseOption) warehouseFilter.getSelectedItem();
        String status = String.valueOf(stockStatusFilter.getSelectedItem());

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                String haystack = (
                    e.getValue(1) + " " +  // Kho
                    e.getValue(2) + " " +  // Mã sản phẩm
                    e.getValue(3) + " " +  // Tên sản phẩm
                    e.getValue(4)          // Mã lô
                ).toLowerCase();

                if (!kw.isEmpty() && !haystack.contains(kw)) return false;

                if (selectedWarehouse != null && selectedWarehouse.id != null) {
                    if (!e.getValue(1).toString().contains(selectedWarehouse.name)) return false;
                }

                if (!"TẤT CẢ".equals(status)) {
                    String currentStatus = e.getValue(11).toString();
                    
                    // Map từ giá trị combobox sang trạng thái trong bảng
                    String expectedStatus = switch(status) {
                        case "SẮP HẾT HÀNG" -> "Sắp hết";
                        case "SẮP HẾT HẠN" -> "Sắp hết hạn";
                        case "DƯ THỪA" -> "Dư thừa";
                        case "BÌNH THƯỜNG" -> "Bình thường";
                        default -> "";
                    };
                    
                    if (!currentStatus.equals(expectedStatus)) return false;
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

        row.add(UiFactory.button("Cập nhật ngưỡng", new Color(255, 193, 7),
            e -> updateThresholds()));
        row.add(UiFactory.button("Xem sắp hết hàng", new Color(220, 53, 69),
            e -> showLowStock()));
        row.add(UiFactory.button("Xem sắp hết hạn", new Color(255, 87, 34),
            e -> showNearExpiry()));
        row.add(UiFactory.button("Refresh", new Color(108, 117, 125),
            e -> reload()));

        return row;
    }

    // ================= ACTIONS =================

    /**
     * Cập nhật ngưỡng min/max
     */
    private void updateThresholds() {
        int row = getSelectedModelRow();
        if (row < 0) return;

        // Lấy inventory item ID từ data
        List<InventoryResponse> data = fetchData();
        if (row >= data.size()) return;
        InventoryResponse item = data.get(row);

        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Cập nhật ngưỡng tồn kho",
            true
        );

        JTextField minField = new JTextField(String.valueOf(item.getMinThreshold()));
        JTextField maxField = new JTextField(String.valueOf(item.getMaxThreshold()));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Sản phẩm:")); 
        form.add(new JLabel(item.getProductName()));
        form.add(new JLabel("Kho:")); 
        form.add(new JLabel(item.getWarehouseName()));
        form.add(new JLabel("Min Threshold *:")); 
        form.add(minField);
        form.add(new JLabel("Max Threshold *:")); 
        form.add(maxField);

        JButton save = new JButton("Lưu");
        save.addActionListener(e -> {
            try {
                UpdateInventoryThresholdRequest request = new UpdateInventoryThresholdRequest();
                request.setMinThreshold(Integer.parseInt(minField.getText().trim()));
                request.setMaxThreshold(Integer.parseInt(maxField.getText().trim()));

                inventoryController.updateThresholds(item.getId(), request);

                dialog.dispose();
                reload();
                JOptionPane.showMessageDialog(this, "Cập nhật ngưỡng thành công!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(save);
        actions.add(new JButton("Hủy") {{
            addActionListener(e -> dialog.dispose());
        }});

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Hiển thị danh sách sắp hết hàng
     */
    private void showLowStock() {
        List<InventoryResponse> lowStockItems = inventoryController.getLowStockItems();
        
        if (lowStockItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không có sản phẩm nào sắp hết hàng", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Set filter to low stock
        stockStatusFilter.setSelectedItem("SẮP HẾT HÀNG");
    }

    /**
     * Hiển thị danh sách sắp hết hạn
     */
    private void showNearExpiry() {
        List<InventoryResponse> nearExpiryItems = inventoryController.getNearExpiryItems();
        
        if (nearExpiryItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không có sản phẩm nào sắp hết hạn", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Set filter to near expiry
        stockStatusFilter.setSelectedItem("SẮP HẾT HẠN");
    }

    // ================= HELPER =================

    /**
     * Helper class cho warehouse filter
     */
    private static class WarehouseOption {
        final Long id;
        final String name;

        WarehouseOption(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
