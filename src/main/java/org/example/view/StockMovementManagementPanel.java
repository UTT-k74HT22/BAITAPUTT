package org.example.view;

import org.example.controller.StockMovementController;
import org.example.controller.WarehouseController;
import org.example.controller.ProductController;
import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.response.StockMovementResponse;
import org.example.dto.response.WarehouseResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.entity.enums.StockMovementType;
import org.example.service.impl.StockMovementServiceImpl;
import org.example.service.impl.WarehouseServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ============================
 * STOCK MOVEMENT MANAGEMENT PANEL
 * ============================
 * 
 * Quản lý xuất nhập kho:
 * - Xem lịch sử xuất nhập kho
 * - Tạo phiếu nhập kho (IMPORT)
 * - Tạo phiếu xuất kho (EXPORT)
 * - Điều chỉnh tồn kho (ADJUST)
 * - Lọc theo kho, loại giao dịch, thời gian
 */
public class StockMovementManagementPanel extends BaseManagementPanel<StockMovementResponse> {

    private StockMovementController stockMovementController;
    private WarehouseController warehouseController;
    private ProductController productController;

    private JComboBox<WarehouseOption> warehouseFilter;
    private JComboBox<StockMovementType> movementTypeFilter;
    private JTextField keywordField;

    private static final String[] COLS = {
        "STT",
        "Loại",
        "Ngày giờ",
        "Kho",
        "Mã SP",
        "Tên sản phẩm",
        "Mã lô",
        "SL",
        "Trước",
        "Sau",
        "Người thực hiện",
        "Ghi chú"
    };

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ================= CONSTRUCTOR =================

    public StockMovementManagementPanel() {
        reload();
    }

    // ================= BASE TABLE =================

    @Override
    protected String titleTotal() {
        return "Tổng số giao dịch";
    }

    @Override
    protected String[] columns() {
        return COLS;
    }

    @Override
    protected int[] columnWidths() {
        return new int[]{50, 80, 120, 120, 80, 180, 100, 60, 60, 60, 140, 200};
    }

    @Override
    protected List<StockMovementResponse> fetchData() {
        return stockMovementController.getAll();
    }

    @Override
    protected Object[] mapRow(StockMovementResponse item, int stt) {
        String movementTypeDisplay = switch (item.getMovementType()) {
            case IMPORT -> "Nhập";
            case EXPORT -> "Xuất";
            case ADJUST -> "Điều chỉnh";
            case TRANSFER -> "Chuyển";
        };

        return new Object[]{
            stt,
            movementTypeDisplay,
            item.getMovementDate() != null ? item.getMovementDate().format(DTF) : "-",
            item.getWarehouseName(),
            item.getProductCode(),
            item.getProductName(),
            item.getBatchCode() != null ? item.getBatchCode() : "-",
            item.getQuantity(),
            item.getQuantityBefore() != null ? item.getQuantityBefore() : "-",
            item.getQuantityAfter() != null ? item.getQuantityAfter() : "-",
            item.getPerformedByFullName() != null ? item.getPerformedByFullName() : "-",
            item.getNote() != null ? item.getNote() : "-"
        };
    }

    // ================= FILTER =================

    @Override
    protected JPanel buildFilters() {
        // Lazy initialization to avoid NPE during super() constructor call
        if (stockMovementController == null) {
            stockMovementController = new StockMovementController(new StockMovementServiceImpl());
        }
        if (warehouseController == null) {
            warehouseController = new WarehouseController();
        }
        if (productController == null) {
            productController = new ProductController(new ProductServiceImpl());
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

        // Movement type filter
        StockMovementType[] movementTypes = new StockMovementType[5];
        movementTypes[0] = null; // ALL
        movementTypes[1] = StockMovementType.IMPORT;
        movementTypes[2] = StockMovementType.EXPORT;
        movementTypes[3] = StockMovementType.ADJUST;
        movementTypes[4] = StockMovementType.TRANSFER;
        
        movementTypeFilter = new JComboBox<>(movementTypes);
        movementTypeFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                         int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("TẤT CẢ LOẠI");
                } else {
                    StockMovementType type = (StockMovementType) value;
                    setText(switch (type) {
                        case IMPORT -> "Nhập kho";
                        case EXPORT -> "Xuất kho";
                        case ADJUST -> "Điều chỉnh";
                        case TRANSFER -> "Chuyển kho";
                    });
                }
                return this;
            }
        });
        movementTypeFilter.setPreferredSize(new Dimension(180, 34));
        movementTypeFilter.addActionListener(e -> applyFilters());

        row.add(new JLabel("Từ khóa:"));
        row.add(keywordField);
        row.add(searchBtn);
        row.add(new JLabel("Kho:"));
        row.add(warehouseFilter);
        row.add(new JLabel("Loại:"));
        row.add(movementTypeFilter);

        return row;
    }

    @Override
    protected void applyFilters() {
        if (sorter == null) return;

        String kw = keywordField.getText().trim().toLowerCase();
        WarehouseOption selectedWarehouse = (WarehouseOption) warehouseFilter.getSelectedItem();
        StockMovementType selectedType = (StockMovementType) movementTypeFilter.getSelectedItem();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                String haystack = (
                    e.getValue(3) + " " +  // Kho
                    e.getValue(4) + " " +  // Mã SP
                    e.getValue(5) + " " +  // Tên SP
                    e.getValue(6) + " " +  // Mã lô
                    e.getValue(10) + " " + // Người thực hiện
                    e.getValue(11)         // Ghi chú
                ).toLowerCase();

                if (!kw.isEmpty() && !haystack.contains(kw)) return false;

                if (selectedWarehouse != null && selectedWarehouse.id != null) {
                    if (!e.getValue(3).toString().contains(selectedWarehouse.name)) return false;
                }

                if (selectedType != null) {
                    String typeDisplay = e.getValue(1).toString();
                    String expectedDisplay = switch (selectedType) {
                        case IMPORT -> "Nhập";
                        case EXPORT -> "Xuất";
                        case ADJUST -> "Điều chỉnh";
                        case TRANSFER -> "Chuyển";
                    };
                    if (!typeDisplay.equals(expectedDisplay)) return false;
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

        row.add(UiFactory.button("Nhập kho", new Color(40, 167, 69),
            e -> openMovementDialog(StockMovementType.IMPORT)));
        row.add(UiFactory.button("Xuất kho", new Color(220, 53, 69),
            e -> openMovementDialog(StockMovementType.EXPORT)));
        row.add(UiFactory.button("Điều chỉnh", new Color(255, 193, 7),
            e -> openMovementDialog(StockMovementType.ADJUST)));
        row.add(UiFactory.button("Refresh", new Color(108, 117, 125),
            e -> reload()));

        return row;
    }

    // ================= MOVEMENT DIALOG =================

    /**
     * Dialog tạo phiếu xuất/nhập kho
     */
    private void openMovementDialog(StockMovementType movementType) {
        String title = switch (movementType) {
            case IMPORT -> "Tạo phiếu nhập kho";
            case EXPORT -> "Tạo phiếu xuất kho";
            case ADJUST -> "Điều chỉnh tồn kho";
            case TRANSFER -> "Chuyển kho";
        };

        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            title,
            true
        );

        // Load data for dropdowns
        List<WarehouseResponse> warehouses = warehouseController.getAllWarehouses();
        List<ProductResponse> products = productController.getAll();

        JComboBox<WarehouseResponse> warehouseBox = new JComboBox<>(warehouses.toArray(new WarehouseResponse[0]));
        warehouseBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                         int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    WarehouseResponse w = (WarehouseResponse) value;
                    setText(w.getCode() + " - " + w.getName());
                }
                return this;
            }
        });

        JComboBox<ProductResponse> productBox = new JComboBox<>(products.toArray(new ProductResponse[0]));
        productBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                         int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    ProductResponse p = (ProductResponse) value;
                    setText(p.getCode() + " - " + p.getName());
                }
                return this;
            }
        });

        JTextField quantityField = new JTextField();
        JTextArea noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Loại:")); 
        form.add(new JLabel(title));
        form.add(new JLabel("Kho *:")); 
        form.add(warehouseBox);
        form.add(new JLabel("Sản phẩm *:")); 
        form.add(productBox);
        
        if (movementType == StockMovementType.ADJUST) {
            form.add(new JLabel("Số lượng mới *:"));
            form.add(quantityField);
            form.add(new JLabel("(Điều chỉnh = set giá trị tuyệt đối)"));
            form.add(new JLabel(""));
        } else {
            form.add(new JLabel("Số lượng *:")); 
            form.add(quantityField);
        }
        
        form.add(new JLabel("Ghi chú:")); 
        form.add(new JScrollPane(noteArea));

        JButton save = new JButton("Lưu");
        save.addActionListener(e -> {
            try {
                WarehouseResponse selectedWarehouse = (WarehouseResponse) warehouseBox.getSelectedItem();
                ProductResponse selectedProduct = (ProductResponse) productBox.getSelectedItem();
                
                if (selectedWarehouse == null || selectedProduct == null) {
                    throw new IllegalArgumentException("Vui lòng chọn kho và sản phẩm");
                }

                CreateStockMovementRequest request = new CreateStockMovementRequest();
                request.setMovementType(movementType);
                request.setWarehouseId(selectedWarehouse.getId());
                request.setProductId(selectedProduct.getId());
                request.setQuantity(Integer.parseInt(quantityField.getText().trim()));
                request.setNote(noteArea.getText().trim());
                request.setPerformedByUserId(1L); // TODO: Get from logged in user

                stockMovementController.createMovement(request);

                dialog.dispose();
                reload();
                
                String successMsg = switch (movementType) {
                    case IMPORT -> "Nhập kho thành công!";
                    case EXPORT -> "Xuất kho thành công!";
                    case ADJUST -> "Điều chỉnh tồn kho thành công!";
                    case TRANSFER -> "Chuyển kho thành công!";
                };
                
                JOptionPane.showMessageDialog(this, successMsg);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Số lượng phải là số nguyên",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
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
