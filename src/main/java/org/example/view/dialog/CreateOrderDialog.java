package org.example.view.dialog;

import org.example.controller.CategoryController;
import org.example.controller.OrderController;
import org.example.controller.ProductController;
import org.example.dto.request.Order.CreateOrderWithItemsRequest;
import org.example.dto.request.Order.OrderItemRequest;
import org.example.dto.response.CategoryResponse;
import org.example.service.impl.CategoryServiceImpl;
import org.example.service.impl.ProductServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderDialog extends JDialog {

    private final CategoryController categoryController =
            new CategoryController(new CategoryServiceImpl());

    private final ProductController productController =
            new ProductController(new ProductServiceImpl());

    private final OrderController orderController = new OrderController();

    private final Long employeeId;

    private JTextField txtCustomerId;
    private JTextField txtShipping;
    private JTextField txtDiscount;
    private JLabel lblTotal;

    private JComboBox<CategoryResponse> cbCategory;
    private JTable tblProducts;
    private DefaultTableModel productModel;

    private JComboBox<String> cbBatch;
    private JSpinner spQuantity;
    private JTextField txtUnitPrice;
    private JButton btnAddItem;

    private JTable tblOrderItems;
    private DefaultTableModel orderItemModel;

    private final List<OrderItemRequest> items = new ArrayList<>();

    private boolean submitted = false;

    public CreateOrderDialog(Window owner, Long employeeId) {
        super(owner, "Tạo đơn hàng", ModalityType.APPLICATION_MODAL);
        this.employeeId = employeeId;

        setSize(1000, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));

        add(buildOrderInfoPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadCategories();
    }

    private JPanel buildOrderInfoPanel() {
        JPanel p = new JPanel(new GridLayout(2, 4, 8, 8));
        p.setBorder(BorderFactory.createTitledBorder("Thông tin đơn hàng"));

        txtCustomerId = new JTextField();
        txtShipping = new JTextField();
        txtDiscount = new JTextField("0");
        lblTotal = new JLabel("0");

        p.add(new JLabel("ID Khách hàng"));
        p.add(txtCustomerId);
        p.add(new JLabel("Địa chỉ giao"));
        p.add(txtShipping);

        p.add(new JLabel("Giảm giá"));
        p.add(txtDiscount);
        p.add(new JLabel("Tổng tiền"));
        p.add(lblTotal);

        return p;
    }

    private JPanel buildCenterPanel() {
        JPanel p = new JPanel(new GridLayout(1, 3, 8, 8));
        p.add(buildCatalogPanel());
        p.add(buildAddItemPanel());
        p.add(buildOrderItemsPanel());
        return p;
    }

    private JPanel buildCatalogPanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createTitledBorder("Danh mục & Sản phẩm"));

        cbCategory = new JComboBox<>();
        p.add(cbCategory, BorderLayout.NORTH);

        productModel = new DefaultTableModel(
                new String[]{"ID", "Tên SP", "Giá"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblProducts = new JTable(productModel);
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(tblProducts), BorderLayout.CENTER);

        cbCategory.addActionListener(e -> onCategoryChanged());
        tblProducts.getSelectionModel().addListSelectionListener(e -> onProductSelected());

        return p;
    }

    private JPanel buildAddItemPanel() {
        JPanel p = new JPanel(new GridLayout(8, 1, 6, 6));
        p.setBorder(BorderFactory.createTitledBorder("Thêm sản phẩm"));

        cbBatch = new JComboBox<>();
        spQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        txtUnitPrice = new JTextField();
        btnAddItem = new JButton("+ Thêm");

        btnAddItem.addActionListener(e -> addItemToOrder());

        p.add(new JLabel("Batch"));
        p.add(cbBatch);
        p.add(new JLabel("Số lượng"));
        p.add(spQuantity);
        p.add(new JLabel("Đơn giá"));
        p.add(txtUnitPrice);
        p.add(new JLabel(""));
        p.add(btnAddItem);

        return p;
    }

    private JPanel buildOrderItemsPanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createTitledBorder("Sản phẩm đã chọn"));

        orderItemModel = new DefaultTableModel(
                new String[]{"SP", "Batch", "SL", "Giá", "Thành tiền", "Ghi chú"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblOrderItems = new JTable(orderItemModel);
        tblOrderItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(tblOrderItems), BorderLayout.CENTER);

        // nhấn 2 lần để sửa ghi chú d
        tblOrderItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editNoteAtSelectedRow();
                }
            }
        });

        return p;
    }

    private JPanel buildBottomPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));

        JButton btnDelete = new JButton("Xóa SP");
        JButton btnDeleteAll = new JButton("Xóa tất cả");
        JButton btnCreate = new JButton("Tạo đơn");
        JButton btnCancel = new JButton("Hủy");

        btnDelete.addActionListener(e -> deleteSelectedItem());
        btnDeleteAll.addActionListener(e -> deleteAllItems());
        btnCreate.addActionListener(e -> submitOrder());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnDeleteAll);
        p.add(btnDelete);
        p.add(btnCancel);
        p.add(btnCreate);

        return p;
    }

    private void loadCategories() {
        cbCategory.removeAllItems();
        categoryController.getAllForOrder().forEach(cbCategory::addItem);
    }

    private void onCategoryChanged() {
        CategoryResponse c = (CategoryResponse) cbCategory.getSelectedItem();
        if (c == null) return;

        productModel.setRowCount(0);
        productController.getByCategory(c.getId())
                .forEach(p -> productModel.addRow(
                        new Object[]{p.getId(), p.getName(), p.getStandardPrice()}
                ));
    }

    private void onProductSelected() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) return;

        try {
            BigDecimal price = (BigDecimal) productModel.getValueAt(row, 2);
            txtUnitPrice.setText(price.toString());
        } catch (Exception ignored) {
            txtUnitPrice.setText("0");
        }

        cbBatch.removeAllItems();
        cbBatch.addItem("DEFAULT");
    }

    private void addItemToOrder() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chưa chọn sản phẩm");
            return;
        }

        BigDecimal unitPrice;
        try {
            unitPrice = new BigDecimal(txtUnitPrice.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ");
            return;
        }

        int quantity = (Integer) spQuantity.getValue();
        String batch = (String) cbBatch.getSelectedItem();

        String note = JOptionPane.showInputDialog(this, "Nhập ghi chú:", "");
        if (note == null) note = "";

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(Long.parseLong(productModel.getValueAt(row, 0).toString()));
        item.setBatchId(null);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setNote(note);

        items.add(item);

        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        orderItemModel.addRow(new Object[]{
                productModel.getValueAt(row, 1),
                batch,
                quantity,
                unitPrice,
                lineTotal,
                note
        });

        updateTotal();
    }

    private void deleteSelectedItem() {
        int row = tblOrderItems.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chưa chọn dòng cần xóa");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa sản phẩm?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        items.remove(row);
        orderItemModel.removeRow(row);
        updateTotal();
    }

    private void deleteAllItems() {
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa toàn bộ sản phẩm trong đơn?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        items.clear();
        orderItemModel.setRowCount(0);
        updateTotal();
    }

    private void editNoteAtSelectedRow() {
        int row = tblOrderItems.getSelectedRow();
        if (row < 0) return;

        String current = items.get(row).getNote();
        String newNote = JOptionPane.showInputDialog(this, "Sửa ghi chú:", current);
        if (newNote == null) return;

        items.get(row).setNote(newNote);
        orderItemModel.setValueAt(newNote, row, 5);
    }

    private void updateTotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItemRequest i : items) {
            subtotal = subtotal.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
        }

        BigDecimal discount;
        try {
            discount = new BigDecimal(txtDiscount.getText());
        } catch (Exception e) {
            discount = BigDecimal.ZERO;
        }

        if (discount.compareTo(subtotal) > 0) discount = BigDecimal.ZERO;

        lblTotal.setText(subtotal.subtract(discount).toString());
    }

    private void submitOrder() {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn hàng chưa có sản phẩm");
            return;
        }

        long customerId;
        try {
            customerId = Long.parseLong(txtCustomerId.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Customer ID không hợp lệ");
            return;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItemRequest i : items) {
            subtotal = subtotal.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
        }

        BigDecimal discount;
        try {
            discount = new BigDecimal(txtDiscount.getText());
        } catch (Exception e) {
            discount = BigDecimal.ZERO;
        }

        // ---- ĐIỀU KIỆN GIẢM GIÁ ----
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(this, "Giảm giá không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (discount.compareTo(subtotal) > 0) {
            JOptionPane.showMessageDialog(this, "Giảm giá phải nhỏ hơn hoặc bằng tổng giá trị đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal total = subtotal.subtract(discount);

        CreateOrderWithItemsRequest req = new CreateOrderWithItemsRequest();
        req.setCustomerId(customerId);
        req.setShippingAddress(txtShipping.getText());
        req.setDiscount(discount);
        req.setItems(items);

        try {
            orderController.createOrder(req, employeeId);
            submitted = true;
            JOptionPane.showMessageDialog(this, "Tạo đơn hàng thành công");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
