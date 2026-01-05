package org.example.view;

import org.example.controller.StockMovementController;
import org.example.controller.WarehouseController;
import org.example.controller.ProductController;
import org.example.controller.BatchController;

import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.request.StockImportRequest;

import org.example.dto.response.StockMovementResponse;
import org.example.dto.response.WarehouseResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.dto.response.Batch.BatchResponse;
import org.example.dto.response.ImportPreviewResponse;
import org.example.dto.response.ImportRowData;

import org.example.entity.enums.StockMovementType;

import org.example.repository.impl.*;
import org.example.service.impl.StockMovementServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.service.impl.BatchServiceImpl;
import org.example.service.impl.StockImportService;

import org.example.utils.importer.template.StockMovementTemplateGenerator;

import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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
 * - Chuyển kho (TRANSFER)
 * - Lọc theo kho, loại giao dịch, từ khóa
 * - Import Excel + tải template
 */
public class StockMovementManagementPanel extends BaseManagementPanel<StockMovementResponse> {

    private StockMovementController stockMovementController;
    private WarehouseController warehouseController;
    private ProductController productController;
    private BatchController batchController;

    private StockMovementController smCtrl() {
        if (stockMovementController == null) {
            stockMovementController = new StockMovementController(new StockMovementServiceImpl());
        }
        return stockMovementController;
    }

    private WarehouseController whCtrl() {
        if (warehouseController == null) {
            warehouseController = new WarehouseController();
        }
        return warehouseController;
    }

    private ProductController pCtrl() {
        if (productController == null) {
            productController = new ProductController(new ProductServiceImpl());
        }
        return productController;
    }

    private BatchController bCtrl() {
        if (batchController == null) {
            batchController = new BatchController(new BatchServiceImpl(new BatchRepositoryImpl()));
        }
        return batchController;
    }

    // ===== filter UI =====
    private JComboBox<WarehouseOption> warehouseFilter;
    private JComboBox<StockMovementType> movementTypeFilter;
    private JTextField keywordField;

    // ===== columns =====
    private static final int COL_STT        = 0;
    private static final int COL_TYPE       = 1;
    private static final int COL_DATE       = 2;
    private static final int COL_WAREHOUSE  = 3;
    private static final int COL_PCODE      = 4;
    private static final int COL_PNAME      = 5;
    private static final int COL_BATCH      = 6;
    private static final int COL_QTY        = 7;
    private static final int COL_BEFORE     = 8;
    private static final int COL_AFTER      = 9;
    private static final int COL_BY         = 10;
    private static final int COL_NOTE       = 11;
    private static final int COL_ID_HIDDEN  = 12;

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
            "Ghi chú",
            "ID" // hidden
    };

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ================= CONSTRUCTOR =================
    public StockMovementManagementPanel() {
        super();
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
        return new int[]{50, 90, 140, 140, 90, 200, 110, 70, 70, 70, 160, 240, 0};
    }

    @Override
    protected List<StockMovementResponse> fetchData() {
        return smCtrl().getAll();
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
            int before = item.getQuantityBefore() != null ? item.getQuantityBefore() : 0;
            int after = item.getQuantityAfter() != null ? item.getQuantityAfter() : 0;
            int delta = after - before;
            quantityDisplay = String.format("%+d", delta);
        } else if (item.getMovementType() == StockMovementType.EXPORT ||
                item.getMovementType() == StockMovementType.TRANSFER) {
            quantityDisplay = "-" + item.getQuantity();
        } else {
            quantityDisplay = "+" + item.getQuantity();
        }

        return new Object[]{
                stt,
                movementTypeDisplay,
                item.getMovementDate() != null ? item.getMovementDate().format(DTF) : "-",
                item.getWarehouseName() != null ? item.getWarehouseName() : "-",
                item.getProductCode() != null ? item.getProductCode() : "-",
                item.getProductName() != null ? item.getProductName() : "-",
                item.getBatchCode() != null ? item.getBatchCode() : "-",
                quantityDisplay,
                item.getQuantityBefore() != null ? item.getQuantityBefore() : "-",
                item.getQuantityAfter() != null ? item.getQuantityAfter() : "-",
                item.getPerformedByFullName() != null ? item.getPerformedByFullName() : "-",
                item.getNote() != null ? item.getNote() : "-",
                item.getId() // hidden ID
        };
    }

    @Override
    protected void afterTableCreated() {
        sorter.setSortable(COL_STT, false);

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
        WarehouseOption[] warehouseOptions = new WarehouseOption[warehouses.size() + 1];
        warehouseOptions[0] = new WarehouseOption(null, "TẤT CẢ KHO");
        for (int i = 0; i < warehouses.size(); i++) {
            WarehouseResponse w = warehouses.get(i);
            warehouseOptions[i + 1] = new WarehouseOption(w.getId(), w.getName());
        }
        warehouseFilter = new JComboBox<>(warehouseOptions);
        warehouseFilter.setPreferredSize(new Dimension(220, 34));
        warehouseFilter.addActionListener(e -> applyFilters());

        // Movement type filter
        StockMovementType[] movementTypes = new StockMovementType[]{
                null, StockMovementType.IMPORT, StockMovementType.EXPORT, StockMovementType.ADJUST, StockMovementType.TRANSFER
        };
        movementTypeFilter = new JComboBox<>(movementTypes);
        movementTypeFilter.setPreferredSize(new Dimension(200, 34));
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

        final String kw = keywordField.getText().trim().toLowerCase();
        final WarehouseOption selectedWarehouse = (WarehouseOption) warehouseFilter.getSelectedItem();
        final StockMovementType selectedType = (StockMovementType) movementTypeFilter.getSelectedItem();

        final String expectedTypeDisplay = (selectedType == null) ? "" : switch (selectedType) {
            case IMPORT -> "Nhập";
            case EXPORT -> "Xuất";
            case ADJUST -> "Điều chỉnh";
            case TRANSFER -> "Chuyển";
        };

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                String haystack = (
                        e.getValue(COL_WAREHOUSE) + " " +
                                e.getValue(COL_PCODE) + " " +
                                e.getValue(COL_PNAME) + " " +
                                e.getValue(COL_BATCH) + " " +
                                e.getValue(COL_BY) + " " +
                                e.getValue(COL_NOTE)
                ).toLowerCase();

                if (!kw.isEmpty() && !haystack.contains(kw)) return false;

                // Kho
                if (selectedWarehouse != null && selectedWarehouse.id != null) {
                    String whName = String.valueOf(e.getValue(COL_WAREHOUSE));
                    if (!whName.equals(selectedWarehouse.name)) return false;
                }

                // Loại
                if (selectedType != null) {
                    String current = String.valueOf(e.getValue(COL_TYPE));
                    if (!current.equals(expectedTypeDisplay)) return false;
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
        row.add(UiFactory.button("Chuyển kho", new Color(23, 162, 184),
                e -> openMovementDialog(StockMovementType.TRANSFER)));

        row.add(UiFactory.button("Import Excel", new Color(0, 123, 255),
                e -> handleImportExcel()));
        row.add(UiFactory.button("Tải Template", new Color(108, 117, 125),
                e -> downloadTemplate()));

        row.add(UiFactory.button("Refresh", new Color(108, 117, 125),
                e -> reload()));

        return row;
    }

    // ================= MOVEMENT DIALOG =================

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
        dialog.setLayout(new BorderLayout());

        // Load data for dropdowns
        List<WarehouseResponse> warehouses = whCtrl().getAllWarehouses();
        List<ProductResponse> products = pCtrl().getAll();

        JComboBox<WarehouseResponse> warehouseBox = new JComboBox<>(warehouses.toArray(new WarehouseResponse[0]));
        warehouseBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    WarehouseResponse w = (WarehouseResponse) value;
                    String code = (w.getCode() != null ? w.getCode() : "");
                    setText(code.isBlank() ? w.getName() : (code + " - " + w.getName()));
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
                    String code = (p.getCode() != null ? p.getCode() : "");
                    setText(code.isBlank() ? p.getName() : (code + " - " + p.getName()));
                }
                return this;
            }
        });

        // Batch ComboBox
        JComboBox<BatchResponse> batchBox = new JComboBox<>();
        batchBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    BatchResponse b = (BatchResponse) value;
                    setText(String.format("%s | Giá: %,.0f | HSD: %s | Trạng thái: %s",
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

        // When product selected => load ACTIVE batches
        productBox.addActionListener(e -> {
            ProductResponse selectedProduct = (ProductResponse) productBox.getSelectedItem();
            batchBox.removeAllItems();
            if (selectedProduct != null) {
                try {
                    List<BatchResponse> batches = bCtrl().getByProduct(selectedProduct.getId());
                    batches.stream()
                            .filter(b -> "ACTIVE".equalsIgnoreCase(String.valueOf(b.getStatus())))
                            .forEach(batchBox::addItem);

                    batchBox.setEnabled(batchBox.getItemCount() > 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                            "Lỗi khi tải danh sách lô hàng: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    batchBox.setEnabled(false);
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

        JButton testBatchBtn = new JButton("Kiểm tra lô hàng");
        testBatchBtn.addActionListener(e -> {
            ProductResponse selectedProduct = (ProductResponse) productBox.getSelectedItem();
            if (selectedProduct != null) {
                List<BatchResponse> allBatches = bCtrl().getByProduct(selectedProduct.getId());
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

                int qty = Integer.parseInt(quantityField.getText().trim());
                if (qty < 0) {
                    throw new IllegalArgumentException("Số lượng phải >= 0");
                }
                if (movementType != StockMovementType.ADJUST && qty == 0) {
                    throw new IllegalArgumentException("Số lượng phải > 0");
                }

                CreateStockMovementRequest request = new CreateStockMovementRequest();
                request.setMovementType(movementType);
                request.setWarehouseId(selectedWarehouse.getId());
                request.setProductId(selectedProduct.getId());
                request.setBatchId(selectedBatch.getId());

                // ADJUST: service thường hiểu quantity là "giá trị mới"
                // EXPORT/IMPORT/TRANSFER: quantity là số lượng giao dịch
                request.setQuantity(qty);

                request.setNote(noteArea.getText().trim());
                request.setPerformedByUserId(1L); // TODO: lấy từ session user đang login

                smCtrl().createMovement(request);

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
                JOptionPane.showMessageDialog(dialog, "Số lượng phải là số nguyên", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Hủy");
        cancel.addActionListener(e -> dialog.dispose());
        actions.add(save);
        actions.add(cancel);

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

        @Override
        public String toString() {
            return name;
        }
    }

    // ================= IMPORT EXCEL =================

    private void downloadTemplate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Lưu Template Excel");
        fileChooser.setSelectedFile(new File("stock_movement_template.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();

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
                    "Đóng"
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

    private void handleImportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để import");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File selectedFile = fileChooser.getSelectedFile();

        JDialog loadingDialog = createLoadingDialog("Đang đọc file Excel...");

        SwingWorker<ImportPreviewResponse<StockImportRequest>, Void> worker = new SwingWorker<>() {
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

                    if (cause instanceof java.io.FileNotFoundException &&
                            (errorMsg != null && (errorMsg.contains("being used by another process")) ||
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

    private void showImportPreviewDialog(ImportPreviewResponse<StockImportRequest> preview, File file) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Xem trước Import - " + file.getName(),
                true
        );
        dialog.setLayout(new BorderLayout(10, 10));

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

        JTabbedPane tabbedPane = new JTabbedPane();

        if (!preview.getValidRows().isEmpty()) {
            JTable validTable = createImportPreviewTable(preview.getValidRows(), false);
            JScrollPane validScroll = new JScrollPane(validTable);
            tabbedPane.addTab("✅ Hợp lệ (" + preview.getValidCount() + ")", validScroll);
        }

        if (!preview.getInvalidRows().isEmpty()) {
            JTable invalidTable = createImportPreviewTable(preview.getInvalidRows(), true);
            JScrollPane invalidScroll = new JScrollPane(invalidTable);
            tabbedPane.addTab("❌ Lỗi (" + preview.getInvalidCount() + ")", invalidScroll);
        }

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

    private JTable createImportPreviewTable(List<ImportRowData<StockImportRequest>> rows, boolean showErrors) {
        String[] columns = showErrors
                ? new String[]{"Dòng", "Loại", "Kho ID", "SP ID", "Lô ID", "Số lượng", "❌ Lỗi"}
                : new String[]{"Dòng", "Loại", "Kho ID", "SP ID", "Lô ID", "Số lượng", "Ghi chú"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(showErrors ? 400 : 200);

        return table;
    }

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
                                    "✅ Import thành công %d bản ghi!\n\nDữ liệu đã được cập nhật vào hệ thống.",
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
        dialog.setSize(420, 160);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        return dialog;
    }
}
