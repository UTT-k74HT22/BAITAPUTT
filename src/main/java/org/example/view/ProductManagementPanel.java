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
 *
 * Vai trò:
 * - Chỉ xử lý UI
 * - Không gọi Repository trực tiếp (trừ import batch)
 * - Không viết SQL
 * - CRUD + PDF đi qua Controller
 */
public class ProductManagementPanel
        extends BaseManagementPanel<ProductResponse> {

    private final ProductController controller;

    private JTextField keywordField;
    private JComboBox<String> statusFilter;

    private static final String[] COLS = {
            "STT",
            "Mã sản phẩm",
            "Tên sản phẩm",
            "Danh mục",
            "Nhà sản xuất",
            "Giá",
            "Toa thuốc",
            "Trạng thái"
    };

    // ================= CONSTRUCTOR =================

    public ProductManagementPanel() {
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
        return new int[]{60, 140, 240, 180, 180, 120, 100, 120};
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
        JButton searchBtn =
                UiFactory.button("Tìm", UiTheme.PRIMARY, e -> applyFilters());

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
                if (!"ALL".equals(st)
                        && !e.getValue(7).toString().equalsIgnoreCase(st)) return false;

                return true;
            }
        });
    }

    // ================= ACTION BUTTONS =================

    @Override
    protected JPanel buildActions() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);

        row.add(UiFactory.button("Thêm", new Color(40, 167, 69),
                e -> openProductDialog(null)));
        row.add(UiFactory.button("Sửa", new Color(255, 193, 7),
                e -> openEdit()));
        row.add(UiFactory.button("Xóa", new Color(220, 53, 69),
                e -> deleteProduct()));
        row.add(UiFactory.button("Chi tiết", new Color(108, 117, 125),
                e -> openDetail()));
        row.add(UiFactory.button("Refresh", new Color(108, 117, 125),
                e -> reload()));
        row.add(UiFactory.button("In PDF", new Color(23, 162, 184),
                e -> exportPdf()));

        row.add(Box.createHorizontalStrut(20));

        row.add(UiFactory.button("Download Template", new Color(23, 162, 184),
                e -> downloadTemplate()));
        row.add(UiFactory.button("Import Excel", new Color(102, 16, 242),
                e -> importFromExcel()));

        return row;
    }

    // ================= DETAIL =================

    private void openDetail() {
        int row = getSelectedModelRow();
        if (row < 0) return;

        String code = getCellString(row, 1);
        ProductDetailResponse d = controller.getDetail(code);

        JTextArea area = new JTextArea(10, 40);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setText("""
                Mã sản phẩm: %s
                Barcode: %s
                Tên sản phẩm: %s
                Danh mục: %s
                Nhà sản xuất: %s
                Giá bán: %s
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

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Chi tiết sản phẩm",
                JOptionPane.INFORMATION_MESSAGE
        );
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
        if (JOptionPane.showConfirmDialog(
                this,
                "Xóa sản phẩm [" + code + "] ?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {
            controller.delete(code);
            reload();
        }
    }

    // ================= EXPORT PDF =================

    private void exportPdf() {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn một sản phẩm",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String code = getCellString(row, 1);

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("product-" + code + ".pdf"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        controller.exportProductDetailPdf(
                code,
                chooser.getSelectedFile().getAbsolutePath()
        );

        JOptionPane.showMessageDialog(
                this,
                "Xuất PDF thành công!",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================= ADD / EDIT DIALOG =================

    private void openProductDialog(String editCode) {
        boolean edit = editCode != null;

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                edit ? "Sửa sản phẩm" : "Thêm sản phẩm",
                true
        );

        JTextField code = new JTextField();
        JTextField name = new JTextField();
        JTextField price = new JTextField();
        JTextArea desc = new JTextArea(3, 20);

        JComboBox<ProductStatus> statusBox =
                new JComboBox<>(ProductStatus.values());

        List<CategoryResponse> categories = controller.getAllCategories();
        JComboBox<CategoryResponse> categoryBox =
                new JComboBox<>(categories.toArray(new CategoryResponse[0]));

        List<ManufacturerResponse> manufacturers =
                controller.getAllManufacturers();
        JComboBox<ManufacturerResponse> manufacturerBox =
                new JComboBox<>(manufacturers.toArray(new ManufacturerResponse[0]));

        if (edit) {
            ProductDetailResponse p = controller.getDetail(editCode);
            code.setText(p.getCode());
            code.setEnabled(false);
            name.setText(p.getName());
            price.setText(p.getStandardPrice().toString());
            desc.setText(p.getDescription());
            statusBox.setSelectedItem(p.getStatus());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Mã *")); form.add(code);
        form.add(new JLabel("Tên *")); form.add(name);
        form.add(new JLabel("Danh mục")); form.add(categoryBox);
        form.add(new JLabel("Nhà sản xuất")); form.add(manufacturerBox);
        form.add(new JLabel("Giá")); form.add(price);
        form.add(new JLabel("Trạng thái")); form.add(statusBox);
        form.add(new JLabel("Mô tả")); form.add(new JScrollPane(desc));

        JButton save = new JButton("Lưu");
        save.addActionListener(e -> {
            try {
                BigDecimal priceValue = new BigDecimal(price.getText().trim());
                CategoryResponse c = (CategoryResponse) categoryBox.getSelectedItem();
                ManufacturerResponse m = (ManufacturerResponse) manufacturerBox.getSelectedItem();

                if (edit) {
                    UpdateProductRequest r = new UpdateProductRequest();
                    r.setName(name.getText().trim());
                    r.setStandardPrice(priceValue);
                    r.setDescription(desc.getText().trim());
                    r.setStatus((ProductStatus) statusBox.getSelectedItem());
                    r.setCategoryId(c.getId());
                    r.setManufacturerId(m != null ? m.getId() : null);
                    controller.update(editCode, r);
                } else {
                    CreateProductRequest r = new CreateProductRequest();
                    r.setCode(code.getText().trim());
                    r.setName(name.getText().trim());
                    r.setStandardPrice(priceValue);
                    r.setDescription(desc.getText().trim());
                    r.setStatus((ProductStatus) statusBox.getSelectedItem());
                    r.setCategoryId(c.getId());
                    r.setManufacturerId(m != null ? m.getId() : null);
                    controller.create(r);
                }

                dialog.dispose();
                reload();

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

    // ================= IMPORT / TEMPLATE =================

    private void downloadTemplate() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Product_Import_Template.xlsx"));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            ProductTemplateGenerator.generateTemplate(chooser.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Tạo template thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importFromExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx", "xls"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            CategoryRepository categoryRepo = new CategoryRepositoryImpl();
            ManufacturerReponsitory manufacturerRepo = new ManufacturerReponsitoryImpl();
            ProductRepository productRepo = new ProductRepositoryImpl();
            ProductImportService importService =
                    new ProductImportService(productRepo, categoryRepo, manufacturerRepo);

            ImportPreviewResponse<ImportProductRequest> preview =
                    importService.previewFromFile(chooser.getSelectedFile());

            ImportPreviewDialog<ImportProductRequest> dialog =
                    new ImportPreviewDialog<>(
                            (Frame) SwingUtilities.getWindowAncestor(this),
                            preview,
                            new String[]{
                                    "Code", "Name", "Category", "Manufacturer",
                                    "Barcode", "Dosage", "Unit", "Price", "Rx"
                            },
                            dto -> new Object[]{
                                    dto.getCode(),
                                    dto.getName(),
                                    dto.getCategoryCode(),
                                    dto.getManufacturerCode(),
                                    dto.getBarcode(),
                                    dto.getDosageForm(),
                                    dto.getUnit(),
                                    dto.getStandardPrice(),
                                    dto.getRequiresPrescription() ? "Yes" : "No"
                            }
                    );

            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                importService.applyImport(dialog.getValidData());
                reload();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi import", JOptionPane.ERROR_MESSAGE);
        }
    }
}
