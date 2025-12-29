package org.example.view;

import org.example.controller.ProductController;
import org.example.dto.request.ImportProductRequest;
import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.CategoryResponse;
import org.example.dto.response.ImportPreviewResponse;
import org.example.dto.response.ManufacturerResponse;
import org.example.dto.response.Product.ProductDetailResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.entity.enums.ProductStatus;
import org.example.repository.CategoryRepository;
import org.example.repository.ManufacturerReponsitory;
import org.example.repository.ProductRepository;
import org.example.repository.impl.CategoryRepositoryImpl;
import org.example.repository.impl.ManufacturerReponsitoryImpl;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.service.impl.ProductImportService;
import org.example.service.impl.ProductServiceImpl;
import org.example.utils.importer.template.ProductTemplateGenerator;
import org.example.view.base.BaseManagementPanel;
import org.example.view.base.UiFactory;
import org.example.view.base.UiTheme;
import org.example.view.dialog.ImportPreviewDialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * ============================
 * PRODUCT MANAGEMENT PANEL
 * ============================
 */
public class ProductManagementPanel extends BaseManagementPanel<ProductResponse> {

    private final ProductController controller;
    private final JFrame parentFrame;

    private JTextField keywordField;
    private JComboBox<String> statusFilter;

    private static final String[] COLS = {
            "STT", "Mã SP", "Tên sản phẩm",
            "Danh mục", "Nhà sản xuất",
            "Giá", "Toa thuốc", "Trạng thái"
    };

    public ProductManagementPanel() {
        this(null);
    }

    public ProductManagementPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.controller = new ProductController(new ProductServiceImpl());
        reload();
    }

    // ================= BASE TABLE =================

    @Override
    protected String titleTotal() {
        return "Tổng số sản phẩm";
    }

    @Override
    protected String[] columns() {
        return COLS;
    }

    @Override
    protected int[] columnWidths() {
        return new int[]{60, 120, 220, 160, 160, 110, 100, 120};
    }

    @Override
    protected List<ProductResponse> fetchData() {
        return controller.getAll();
    }

    @Override
    protected Object[] mapRow(ProductResponse p, int stt) {
        return new Object[]{
                stt,
                p.getCode(),
                p.getName(),
                p.getCategoryName(),
                p.getManufacturerName(),
                p.getStandardPrice(),
                p.isRequiresPrescription() ? "Có" : "Không",
                p.getStatus()
        };
    }

    // ================= FILTER =================

    @Override
    protected JPanel buildFilters() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        keywordField = UiFactory.textField(20, 240);
        JButton searchBtn = UiFactory.button("Tìm", UiTheme.PRIMARY, e -> applyFilters());

        statusFilter = new JComboBox<>(new String[]{
                "ALL",
                ProductStatus.ACTIVE.name(),
                ProductStatus.INACTIVE.name(),
                ProductStatus.DISCONTINUED.name()
        });
        statusFilter.setPreferredSize(new Dimension(160, 34));
        statusFilter.addActionListener(e -> applyFilters());

        row.add(new JLabel("Từ khóa:"));
        row.add(keywordField);
        row.add(searchBtn);
        row.add(new JLabel("Trạng thái:"));
        row.add(statusFilter);

        return row;
    }

    @Override
    protected void applyFilters() {
        if (sorter == null) return;

        String kw = keywordField.getText().trim().toLowerCase();
        String st = String.valueOf(statusFilter.getSelectedItem());

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                String haystack = (
                        e.getValue(1) + " " +
                                e.getValue(2) + " " +
                                e.getValue(3) + " " +
                                e.getValue(4)
                ).toLowerCase();

                if (!kw.isEmpty() && !haystack.contains(kw)) return false;
                if (!"ALL".equals(st) && !e.getValue(7).toString().equalsIgnoreCase(st)) return false;
                return true;
            }
        });
    }

    // ================= ACTION BUTTONS =================

    @Override
    protected JPanel buildActions() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        row.add(UiFactory.button("Thêm", new Color(40,167,69), e -> openProductDialog(null)));
        row.add(UiFactory.button("Sửa", new Color(255,193,7), e -> openEdit()));
        row.add(UiFactory.button("Xóa", new Color(220,53,69), e -> deleteProduct()));
        row.add(UiFactory.button("Chi tiết", new Color(108,117,125), e -> openDetail()));
        row.add(UiFactory.button("Refresh", new Color(108,117,125), e -> reload()));

        // ✅ THÊM CHỨC NĂNG XUẤT PDF
        row.add(UiFactory.button("Xuất PDF", new Color(0, 123, 255), e -> exportPdf()));

        row.add(Box.createHorizontalStrut(20));
        row.add(UiFactory.button("Download Template", new Color(23,162,184), e -> downloadTemplate()));
        row.add(UiFactory.button("Import Excel", new Color(102,16,242), e -> importFromExcel()));

        return row;
    }

    // ================= DETAIL =================

    private void openDetail() {
        int row = getSelectedModelRow();
        if (row < 0) return;

        String code = getCellString(row, 1);
        ProductDetailResponse d = controller.getDetail(code);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setText("""
                Mã: %s
                Barcode: %s
                Tên: %s
                Danh mục: %s
                Nhà sản xuất: %s
                Giá: %s
                Toa thuốc: %s
                Mô tả:
                %s
                """.formatted(
                d.getCode(),
                d.getBarcode(),
                d.getName(),
                d.getCategoryName(),
                d.getManufacturerName(),
                d.getStandardPrice(),
                d.isRequiresPrescription() ? "Có" : "Không",
                d.getDescription()
        ));

        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Chi tiết sản phẩm", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= CRUD =================

    private void openEdit() {
        int row = getSelectedModelRow();
        if (row < 0) return;
        openProductDialog(getCellString(row, 1));
    }

    private void deleteProduct() {
        int row = getSelectedModelRow();
        if (row < 0) return;

        String code = getCellString(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Xóa [" + code + "] ?", "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            controller.delete(code);
            reload();
        }
    }

    // ================= ADD / EDIT DIALOG =================

    private void openProductDialog(String editCode) {
        boolean edit = editCode != null;

        Window w = SwingUtilities.getWindowAncestor(this);
        JDialog d = new JDialog((Frame) w, edit ? "Sửa sản phẩm" : "Thêm sản phẩm", true);
        d.setLayout(new BorderLayout());

        JTextField code = new JTextField();
        JTextField name = new JTextField();
        JTextField barcode = new JTextField();
        JTextField dosage = new JTextField();
        JTextField unit = new JTextField();
        JTextField price = new JTextField();
        JTextArea desc = new JTextArea(4, 20);

        JCheckBox rx = new JCheckBox("Cần toa thuốc");

        JComboBox<ProductStatus> statusBox = new JComboBox<>(ProductStatus.values());

        List<CategoryResponse> categories = controller.getAllCategories();
        JComboBox<CategoryResponse> categoryBox = new JComboBox<>(categories.toArray(new CategoryResponse[0]));
        categoryBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponse c) setText(c.getName());
                return this;
            }
        });

        List<ManufacturerResponse> manufacturers = controller.getAllManufacturers();
        JComboBox<ManufacturerResponse> manufacturerBox = new JComboBox<>(manufacturers.toArray(new ManufacturerResponse[0]));
        manufacturerBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ManufacturerResponse m) setText(m.getName());
                return this;
            }
        });

        if (edit) {
            ProductDetailResponse p = controller.getDetail(editCode);
            code.setText(p.getCode());
            code.setEnabled(false);
            name.setText(p.getName());
            barcode.setText(p.getBarcode());
            dosage.setText(p.getDosageForm());
            unit.setText(p.getUnit());
            price.setText(p.getStandardPrice().toString());
            desc.setText(p.getDescription());
            rx.setSelected(p.isRequiresPrescription());
            statusBox.setSelectedItem(p.getStatus());

            categories.stream().filter(c -> c.getId().equals(p.getCategoryId())).findFirst().ifPresent(categoryBox::setSelectedItem);
            manufacturers.stream().filter(m -> m.getId().equals(p.getManufacturerId())).findFirst().ifPresent(manufacturerBox::setSelectedItem);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Mã *")); form.add(code);
        form.add(new JLabel("Tên *")); form.add(name);
        form.add(new JLabel("Barcode")); form.add(barcode);
        form.add(new JLabel("Danh mục *")); form.add(categoryBox);
        form.add(new JLabel("Nhà sản xuất")); form.add(manufacturerBox);
        form.add(new JLabel("Dạng bào chế")); form.add(dosage);
        form.add(new JLabel("Đơn vị")); form.add(unit);
        form.add(new JLabel("Giá *")); form.add(price);
        form.add(new JLabel("Trạng thái")); form.add(statusBox);
        form.add(new JLabel("")); form.add(rx);
        form.add(new JLabel("Mô tả")); form.add(new JScrollPane(desc));

        JButton save = new JButton("Lưu");
        save.addActionListener(e -> {
            try {
                if (name.getText().isBlank()) throw new IllegalArgumentException("Tên không được để trống");
                BigDecimal priceValue = new BigDecimal(price.getText().trim());

                CategoryResponse c = (CategoryResponse) categoryBox.getSelectedItem();
                ManufacturerResponse m = (ManufacturerResponse) manufacturerBox.getSelectedItem();

                if (edit) {
                    UpdateProductRequest r = new UpdateProductRequest();
                    r.setName(name.getText().trim());
                    r.setBarcode(barcode.getText().trim());
                    r.setDosageForm(dosage.getText().trim());
                    r.setUnit(unit.getText().trim());
                    r.setDescription(desc.getText().trim());
                    r.setStandardPrice(priceValue);
                    r.setRequiresPrescription(rx.isSelected());
                    r.setStatus((ProductStatus) statusBox.getSelectedItem());
                    r.setCategoryId(c.getId());
                    r.setManufacturerId(m != null ? m.getId() : null);
                    controller.update(editCode, r);
                } else {
                    CreateProductRequest r = new CreateProductRequest();
                    r.setCode(code.getText().trim());
                    r.setName(name.getText().trim());
                    r.setBarcode(barcode.getText().trim());
                    r.setDosageForm(dosage.getText().trim());
                    r.setUnit(unit.getText().trim());
                    r.setDescription(desc.getText().trim());
                    r.setStandardPrice(priceValue);
                    r.setRequiresPrescription(rx.isSelected());
                    r.setStatus((ProductStatus) statusBox.getSelectedItem());
                    r.setCategoryId(c.getId());
                    r.setManufacturerId(m != null ? m.getId() : null);
                    controller.create(r);
                }

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
        d.setLocationRelativeTo(w);
        d.setVisible(true);
    }

    // ================= EXPORT PDF =================

    private void exportPdf() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = getCellString(row, 1);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file PDF");
        chooser.setSelectedFile(new File("product-" + code + ".pdf"));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".pdf")) path += ".pdf";

        try {
            controller.exportProductDetailPdf(code, path);
            JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file.getParentFile());
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= TEMPLATE DOWNLOAD =================

    private void downloadTemplate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Template Excel");
        fileChooser.setSelectedFile(new File("Product_Import_Template.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xlsx")) path += ".xlsx";

            try {
                ProductTemplateGenerator.generateTemplate(path);
                JOptionPane.showMessageDialog(this, "Tạo template thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file.getParentFile());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo template: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================= IMPORT EXCEL =================

    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để import");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx, *.xls)", "xlsx", "xls"));

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fileChooser.getSelectedFile();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            CategoryRepository categoryRepo = new CategoryRepositoryImpl();
            ManufacturerReponsitory manufacturerRepo = new ManufacturerReponsitoryImpl();
            ProductRepository productRepo = new ProductRepositoryImpl();
            ProductImportService importService = new ProductImportService(productRepo, categoryRepo, manufacturerRepo);

            ImportPreviewResponse<ImportProductRequest> preview = importService.previewFromFile(file);
            setCursor(Cursor.getDefaultCursor());

            ImportPreviewDialog<ImportProductRequest> dialog = new ImportPreviewDialog<>(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    preview,
                    new String[]{"Code","Name","Category","Manufacturer","Barcode","Dosage","Unit","Price","Rx"},
                    dto -> new Object[]{ dto.getCode(), dto.getName(), dto.getCategoryCode(), dto.getManufacturerCode(),
                            dto.getBarcode(), dto.getDosageForm(), dto.getUnit(), dto.getStandardPrice(),
                            dto.getRequiresPrescription() ? "Yes" : "No"}
            );

            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                importService.applyImport(dialog.getValidData());
                reload();
            }

        } catch (Exception ex) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi import", JOptionPane.ERROR_MESSAGE);
        }
    }
}
