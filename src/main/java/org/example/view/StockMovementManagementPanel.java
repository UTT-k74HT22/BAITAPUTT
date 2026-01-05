package org.example.view;

import org.example.controller.StockMovementController;
import org.example.controller.WarehouseController;
import org.example.controller.ProductController;
import org.example.controller.BatchController;
import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.response.StockMovementResponse;
import org.example.dto.response.WarehouseResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.dto.response.Batch.BatchResponse;
import org.example.entity.enums.StockMovementType;
import org.example.repository.impl.BatchRepositoryImpl;
import org.example.service.impl.StockMovementServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.service.impl.BatchServiceImpl;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;
import org.example.service.impl.StockImportService;
import org.example.dto.request.StockImportRequest;
import org.example.dto.response.ImportPreviewResponse;
import org.example.dto.response.ImportRowData;
import org.example.utils.importer.template.StockMovementTemplateGenerator;
import org.example.repository.impl.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private BatchController batchController;

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

        // Hiển thị quantity theo nghĩa vụ từng loại giao dịch
        String quantityDisplay;
        if (item.getMovementType() == StockMovementType.ADJUST) {
            // ADJUST: hiển thị delta (after - before) để thấy rõ tăng/giảm bao nhiêu
            int before = item.getQuantityBefore() != null ? item.getQuantityBefore() : 0;
            int after = item.getQuantityAfter() != null ? item.getQuantityAfter() : 0;
            int delta = after - before;
            quantityDisplay = String.format("%+d", delta); // +5 hoặc -3
        } else if (item.getMovementType() == StockMovementType.EXPORT ||
                   item.getMovementType() == StockMovementType.TRANSFER) {
            // EXPORT/TRANSFER: hiển thị với dấu âm
            quantityDisplay = "-" + item.getQuantity();
        } else {
            // IMPORT: hiển thị với dấu dương
            quantityDisplay = "+" + item.getQuantity();
        }

        return new Object[]{
            stt,
            movementTypeDisplay,
            item.getMovementDate() != null ? item.getMovementDate().format(DTF) : "-",
            item.getWarehouseName(),
            item.getProductCode(),
            item.getProductName(),
            item.getBatchCode() != null ? item.getBatchCode() : "-",
            quantityDisplay, // Hiển thị rõ ràng +/- hoặc delta
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
        if (batchController == null) {
            batchController = new BatchController(new BatchServiceImpl(new BatchRepositoryImpl()));
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
        
        // ⭐ THÊM CÁC NÚT IMPORT EXCEL
        row.add(UiFactory.button("📥 Import Excel", new Color(0, 123, 255),
            e -> handleImportExcel()));
        row.add(UiFactory.button("📄 Tải Template", new Color(108, 117, 125),
            e -> downloadTemplate()));
        
        row.add(UiFactory.button("Refresh", new Color(108, 117, 125),
            e -> reload()));

        return row;
    }

    // ================= MOVEMENT DIALOG =================

    /**
     * Dialog tạo phiếu xuất/nhập kho
     */
    private void openMovementDialog(StockMovementType movementType) {
        // Ensure controllers are initialized
        if (batchController == null) {
            batchController = new BatchController(new BatchServiceImpl(new BatchRepositoryImpl()));
        }
        if (warehouseController == null) {
            warehouseController = new WarehouseController();
        }
        if (productController == null) {
            productController = new ProductController(new ProductServiceImpl());
        }
        
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

        // Batch ComboBox - initially empty, will be populated when product is selected
        JComboBox<BatchResponse> batchBox = new JComboBox<>();
        batchBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                         int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    BatchResponse b = (BatchResponse) value;
                    setText(String.format("%s | Giá: %,.0f | HSD: %s | SL còn: %s",
                        b.getBatchCode(),
                        b.getImportPrice(),
                        b.getExpiryDate(),
                        b.getStatus()
                    ));
                }
                return this;
            }
        });
        batchBox.setEnabled(false);

        // When product is selected, load its batches
        productBox.addActionListener(e -> {
            ProductResponse selectedProduct = (ProductResponse) productBox.getSelectedItem();
            batchBox.removeAllItems();
            if (selectedProduct != null) {
                try {
                    System.out.println("Loading batches for product ID: " + selectedProduct.getId());
                    List<BatchResponse> batches = batchController.getByProduct(selectedProduct.getId());
                    System.out.println("Found " + batches.size() + " batches");
                    
                    // Filter only ACTIVE batches
                    batches.stream()
                        .filter(b -> "ACTIVE".equals(b.getStatus()))
                        .forEach(batch -> {
                            System.out.println("Adding batch: " + batch.getBatchCode());
                            batchBox.addItem(batch);
                        });
                    batchBox.setEnabled(batchBox.getItemCount() > 0);
                    
                    if (batchBox.getItemCount() == 0) {
                        System.out.println("No ACTIVE batches found for this product");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                        "Lỗi khi tải danh sách lô hàng: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                batchBox.setEnabled(false);
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
        
        // Add test button to check batches
        JButton testBatchBtn = new JButton("Kiểm tra lô hàng");
        testBatchBtn.addActionListener(e -> {
            ProductResponse selectedProduct = (ProductResponse) productBox.getSelectedItem();
            if (selectedProduct != null) {
                List<BatchResponse> allBatches = batchController.getByProduct(selectedProduct.getId());
                StringBuilder info = new StringBuilder();
                info.append("Tổng số lô: ").append(allBatches.size()).append("\n\n");
                for (BatchResponse b : allBatches) {
                    info.append("Mã lô: ").append(b.getBatchCode()).append("\n");
                    info.append("Trạng thái: ").append(b.getStatus()).append("\n");
                    info.append("Giá nhập: ").append(b.getImportPrice()).append("\n");
                    info.append("HSD: ").append(b.getExpiryDate()).append("\n\n");
                }
                JOptionPane.showMessageDialog(dialog, info.toString(), "Thông tin lô hàng", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        form.add(new JLabel("Lô hàng *:")); 
        JPanel batchPanel = new JPanel(new BorderLayout(5, 0));
        batchPanel.add(batchBox, BorderLayout.CENTER);
        batchPanel.add(testBatchBtn, BorderLayout.EAST);
        form.add(batchPanel);
        
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
                BatchResponse selectedBatch = (BatchResponse) batchBox.getSelectedItem();
                
                if (selectedWarehouse == null || selectedProduct == null) {
                    throw new IllegalArgumentException("Vui lòng chọn kho và sản phẩm");
                }

                if (selectedBatch == null) {
                    throw new IllegalArgumentException("Vui lòng chọn lô hàng");
                }

                CreateStockMovementRequest request = new CreateStockMovementRequest();
                request.setMovementType(movementType);
                request.setWarehouseId(selectedWarehouse.getId());
                request.setProductId(selectedProduct.getId());
                request.setBatchId(selectedBatch.getId());
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

    // ================= IMPORT EXCEL =================

    /**
     * Download template Excel để import
     */
    private void downloadTemplate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Lưu Template Excel");
        fileChooser.setSelectedFile(new File("stock_movement_template.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        
        File file = fileChooser.getSelectedFile();
        
        // Kiểm tra đường dẫn hợp lệ
        if (file == null || file.getPath().contains("::")) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng chọn thư mục cụ thể để lưu file.\nKhông thể lưu vào 'This PC' hoặc vị trí ảo.",
                "Đường dẫn không hợp lệ",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }
        
        try {
            StockMovementTemplateGenerator generator = new StockMovementTemplateGenerator();
            byte[] templateBytes = generator.generate();
            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(templateBytes);
            }
            
            int choice = JOptionPane.showOptionDialog(
                this,
                "Template đã được lưu thành công!\n\n" +
                "⚠️ LƯU Ý: Nếu mở file Excel, hãy ĐÓNG Excel trước khi Import.\n" +
                "File Excel đang mở sẽ bị khóa và không thể đọc được.\n\n" +
                "Bạn có muốn mở file ngay bây giờ?",
                "Thành công",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Mở file", "Đóng"},
                "Đóng"  // Changed default to "Đóng"
            );
            
            if (choice == 0) {
                Desktop.getDesktop().open(file);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi tạo template: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    /**
     * Import dữ liệu từ file Excel
     */
    private void handleImportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để import");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        
        File selectedFile = fileChooser.getSelectedFile();
        
        // Show loading dialog
        JDialog loadingDialog = createLoadingDialog("Đang đọc file Excel...");
        
        // Import in background thread
        SwingWorker<ImportPreviewResponse<StockImportRequest>, Void> worker = 
            new SwingWorker<>() {
                @Override
                protected ImportPreviewResponse<StockImportRequest> doInBackground() throws Exception {
                    StockImportService service = new StockImportService(
                        new StockMovementServiceImpl(),
                        new BatchRepositoryImpl(),
                        new ProductRepositoryImpl(),
                        new WarehouseRepositoryImpl(),
                        new InventoryRepositoryImpl()
                    );
                    return service.previewFromFile(selectedFile);
                }
                
                @Override
                protected void done() {
                    loadingDialog.dispose();
                    
                    try {
                        ImportPreviewResponse<StockImportRequest> preview = get();
                        showImportPreviewDialog(preview, selectedFile);
                        
                    } catch (Exception e) {
                        String errorMsg = e.getMessage();
                        Throwable cause = e.getCause();
                        
                        // Check if file is locked by another process
                        if (cause instanceof java.io.FileNotFoundException && 
                            (errorMsg.contains("being used by another process") || 
                             (cause.getMessage() != null && cause.getMessage().contains("being used by another process")))) {
                            JOptionPane.showMessageDialog(
                                StockMovementManagementPanel.this,
                                "❌ FILE ĐANG BỊ KHÓA!\n\n" +
                                "File Excel đang được mở bởi chương trình khác (có thể là Excel).\n\n" +
                                "Giải pháp:\n" +
                                "1. Đóng file Excel nếu đang mở\n" +
                                "2. Đóng tất cả chương trình đang sử dụng file này\n" +
                                "3. Thử lại\n\n" +
                                "File: " + selectedFile.getName(),
                                "File đang được sử dụng",
                                JOptionPane.WARNING_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                StockMovementManagementPanel.this,
                                "Lỗi khi đọc file: " + errorMsg,
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                        e.printStackTrace();
                    }
                }
            };
        
        worker.execute();
        loadingDialog.setVisible(true);
    }

    /**
     * Hiển thị preview trước khi import
     */
    private void showImportPreviewDialog(
        ImportPreviewResponse<StockImportRequest> preview, File file) {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Xem trước Import - " + file.getName(),
            true
        );
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Tổng quan"));
        summaryPanel.setBackground(Color.WHITE);
        
        JLabel validLabel = new JLabel("✅ Hợp lệ: " + preview.getValidCount() + " dòng");
        validLabel.setFont(new Font("Arial", Font.BOLD, 14));
        validLabel.setForeground(new Color(40, 167, 69));
        
        JLabel invalidLabel = new JLabel("❌ Lỗi: " + preview.getInvalidCount() + " dòng");
        invalidLabel.setFont(new Font("Arial", Font.BOLD, 14));
        invalidLabel.setForeground(new Color(220, 53, 69));
        
        summaryPanel.add(validLabel);
        summaryPanel.add(invalidLabel);
        
        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Valid data
        if (!preview.getValidRows().isEmpty()) {
            JTable validTable = createImportPreviewTable(preview.getValidRows(), false);
            JScrollPane validScroll = new JScrollPane(validTable);
            tabbedPane.addTab("✅ Hợp lệ (" + preview.getValidCount() + ")", validScroll);
        }
        
        // Tab 2: Invalid data
        if (!preview.getInvalidRows().isEmpty()) {
            JTable invalidTable = createImportPreviewTable(preview.getInvalidRows(), true);
            JScrollPane invalidScroll = new JScrollPane(invalidTable);
            tabbedPane.addTab("❌ Lỗi (" + preview.getInvalidCount() + ")", invalidScroll);
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnImport = UiFactory.button(
            "Import " + preview.getValidCount() + " dòng", 
            new Color(40, 167, 69),
            e -> {
                dialog.dispose();
                executeImport(preview);
            }
        );
        btnImport.setEnabled(preview.getValidCount() > 0);
        
        JButton btnCancel = UiFactory.button("Hủy", new Color(108, 117, 125), e -> dialog.dispose());
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnImport);
        
        dialog.add(summaryPanel, BorderLayout.NORTH);
        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Tạo bảng preview dữ liệu import
     */
    private JTable createImportPreviewTable(
        List<ImportRowData<StockImportRequest>> rows,
        boolean showErrors
    ) {
        String[] columns;
        if (showErrors) {
            columns = new String[]{"Dòng", "Loại", "Kho ID", "SP ID", "Lô ID", "Số lượng", "❌ Lỗi"};
        } else {
            columns = new String[]{"Dòng", "Loại", "Kho ID", "SP ID", "Lô ID", "Số lượng", "Ghi chú"};
        }
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (var row : rows) {
            StockImportRequest data = row.getData();
            
            String movementTypeDisplay = data.getMovementType() != null 
                ? switch (data.getMovementType()) {
                    case IMPORT -> "Nhập";
                    case EXPORT -> "Xuất";
                    case ADJUST -> "Điều chỉnh";
                    case TRANSFER -> "Chuyển";
                }
                : "-";
            
            if (showErrors) {
                // Convert List<ImportError> to String
                StringBuilder errorStr = new StringBuilder();
                for (var error : row.getErrors()) {
                    errorStr.append(error.getColumnName()).append(": ").append(error.getMessage()).append("\n");
                }
                model.addRow(new Object[]{
                    row.getRowIndex(),
                    movementTypeDisplay,
                    data.getWarehouseId() != null ? data.getWarehouseId() : "-",
                    data.getProductId() != null ? data.getProductId() : "-",
                    data.getBatchId() != null ? data.getBatchId() : "-",
                    data.getQuantity() != null ? data.getQuantity() : "-",
                    errorStr.toString().trim()
                });
            } else {
                model.addRow(new Object[]{
                    row.getRowIndex(),
                    movementTypeDisplay,
                    data.getWarehouseId(),
                    data.getProductId(),
                    data.getBatchId(),
                    data.getQuantity(),
                    data.getNote() != null ? data.getNote() : "-"
                });
            }
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(showErrors ? 60 : 25);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(showErrors ? 400 : 200);
        
        return table;
    }

    /**
     * Thực hiện import sau khi user confirm
     */
    private void executeImport(ImportPreviewResponse<StockImportRequest> preview) {
        JDialog loadingDialog = createLoadingDialog("Đang import dữ liệu vào database...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                StockImportService service = new StockImportService(
                    new StockMovementServiceImpl(),
                    new BatchRepositoryImpl(),
                    new ProductRepositoryImpl(),
                    new WarehouseRepositoryImpl(),
                    new InventoryRepositoryImpl()
                );
                
                // Extract valid data from preview
                List<StockImportRequest> validData = new ArrayList<>();
                for (var row : preview.getValidRows()) {
                    validData.add(row.getData());
                }
                
                service.applyImport(validData);
                return null;
            }
            
            @Override
            protected void done() {
                loadingDialog.dispose();
                
                try {
                    get();
                    
                    JOptionPane.showMessageDialog(
                        StockMovementManagementPanel.this,
                        String.format(
                            "✅ Import thành công %d bản ghi!\n\n" +
                            "Dữ liệu đã được cập nhật vào hệ thống.",
                            preview.getValidCount()
                        ),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    reload();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        StockMovementManagementPanel.this,
                        "❌ Lỗi khi import:\n" + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
        loadingDialog.setVisible(true);
    }

    /**
     * Tạo dialog loading với progress bar
     */
    private JDialog createLoadingDialog(String message) {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Đang xử lý...",
            true
        );
        dialog.setLayout(new BorderLayout(10, 10));
        
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        return dialog;
    }
}
