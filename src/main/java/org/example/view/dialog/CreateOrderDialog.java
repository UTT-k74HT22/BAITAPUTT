// language: java
package org.example.view.dialog;

import org.example.controller.CategoryController;
import org.example.controller.OrderController;
import org.example.controller.ProductController;
import org.example.controller.InventoryController;
import org.example.dto.request.Order.CreateOrderWithItemsRequest;
import org.example.dto.request.Order.OrderItemRequest;
import org.example.dto.response.Batch.BatchResponse;
import org.example.dto.response.CategoryResponse;
import org.example.dto.response.InventoryResponse;
import org.example.service.impl.CategoryServiceImpl;
import org.example.service.impl.InventoryServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.service.impl.AuthServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderDialog extends JDialog {

    private final InventoryController inventoryController =
            new InventoryController(new InventoryServiceImpl());

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

    private JComboBox<InventoryResponse> cbWarehouse;
    private JComboBox<BatchResponse> cbBatch;
    private JSpinner spQuantity;
    private JTextField txtUnitPrice;
    private JButton btnAddItem;

    private JTable tblOrderItems;
    private DefaultTableModel orderItemModel;

    private final List<OrderItemRequest> orderItems = new ArrayList<>();

    private boolean submitted = false;

    public CreateOrderDialog(Window owner, Long employeeId) {
        super(owner, "Tạo đơn hàng", ModalityType.APPLICATION_MODAL);
        this.employeeId = employeeId;

        setSize(1100, 650);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));

        add(buildOrderInfoPanel(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildActionPanel(), BorderLayout.SOUTH);

        loadCategories();
    }

    private JPanel buildOrderInfoPanel() {
        JPanel p = new JPanel(new GridLayout(2, 3, 8, 8));
        p.setBorder(BorderFactory.createTitledBorder("Thông tin đơn hàng"));

        txtCustomerId = new JTextField();
        txtShipping = new JTextField();
        txtDiscount = new JTextField("0");
        lblTotal = new JLabel("0");

        txtCustomerId.setHorizontalAlignment(SwingConstants.LEFT);
        txtDiscount.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));

        p.add(new JLabel("Customer ID:"));
        p.add(new JLabel("Shipping Address:"));
        p.add(new JLabel("Discount:"));

        p.add(txtCustomerId);
        p.add(txtShipping);
        p.add(txtDiscount);

        return p;
    }

    private JPanel buildMainPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 8));
        p.add(buildLeftPanel());
        p.add(buildOrderItemsPanel());
        return p;
    }

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.add(buildCatalogPanel(), BorderLayout.CENTER);
        p.add(buildAddItemPanel(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildCatalogPanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createTitledBorder("Danh mục & Sản phẩm"));

        cbCategory = new JComboBox<>();
        p.add(cbCategory, BorderLayout.NORTH);

        productModel = new DefaultTableModel(new String[]{"ID", "Tên SP", "Giá"}, 0) {
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
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Chọn kho & lô hàng"));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        formRow.setBackground(Color.WHITE);

        // Label + Combo Warehouse
        formRow.add(new JLabel("Kho:"));
        cbWarehouse = new JComboBox<>();
        cbWarehouse.setPreferredSize(new Dimension(160, 28));
        formRow.add(cbWarehouse);

        // Label + Combo Batch
        formRow.add(new JLabel("Lô:"));
        cbBatch = new JComboBox<>();
        cbBatch.setPreferredSize(new Dimension(140, 28));
        formRow.add(cbBatch);

        // Label + Spinner Quantity
        formRow.add(new JLabel("SL:"));
        spQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spQuantity.setPreferredSize(new Dimension(70, 28));
        formRow.add(spQuantity);

        panel.add(formRow);
        formRow.add(new JLabel("Đơn giá:"));
        txtUnitPrice = new JTextField();
        txtUnitPrice.setPreferredSize(new Dimension(120, 28));
        txtUnitPrice.setEditable(false);
        txtUnitPrice.setHorizontalAlignment(SwingConstants.RIGHT);
        formRow.add(txtUnitPrice);

        // Nút thêm
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        buttonRow.setBackground(Color.WHITE);

        btnAddItem = new JButton("+ Thêm vào đơn");
        btnAddItem.setFont(new Font("Arial", Font.BOLD, 13));
        btnAddItem.setPreferredSize(new Dimension(150, 32));
        btnAddItem.setFocusPainted(false);
        btnAddItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonRow.add(btnAddItem);

        btnAddItem.addActionListener(e -> addOrderItem());

        panel.add(buttonRow);

        return panel;
    }


    private JPanel buildOrderItemsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Sản phẩm trong đơn"));

        orderItemModel = new DefaultTableModel(
                new String[]{"Product", "Warehouse", "Batch", "Quantity", "Unit Price", "Line Total", "Note"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblOrderItems = new JTable(orderItemModel);
        p.add(new JScrollPane(tblOrderItems), BorderLayout.CENTER);

        lblTotal = new JLabel("0");
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.add(new JLabel("Tổng tiền: "), BorderLayout.WEST);
        totalRow.add(lblTotal, BorderLayout.CENTER);
        p.add(totalRow, BorderLayout.SOUTH);

        tblOrderItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) editNoteAtSelectedRow();
            }
        });

        return p;
    }

    private void editNoteAtSelectedRow() {
        int selectedRow = tblOrderItems.getSelectedRow();
        if (selectedRow >= 0) {
            // Get the current note (6th column, 0-based index)
            String currentNote = (String) orderItemModel.getValueAt(selectedRow, 6);

            // Show input dialog to edit the note
            String newNote = JOptionPane.showInputDialog(
                    this,
                    "Edit note:",
                    currentNote == null ? "" : currentNote
            );

            // If user didn't cancel and the note has changed
            if (newNote != null && !newNote.equals(currentNote)) {
                // Update the table
                orderItemModel.setValueAt(newNote, selectedRow, 6);

                // Update the order item in the list
                OrderItemRequest item = orderItems.get(selectedRow);
                item.setNote(newNote);
            }
        }
    }

    private JPanel buildActionPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnDelete = new JButton("🗑 Xóa dòng");
        JButton btnDeleteAll = new JButton("🚫 Xóa tất cả");
        JButton btnConfirm = new JButton("✔ Confirm Order");
        JButton btnCancel = new JButton("Hủy");

        btnDelete.addActionListener(e -> deleteSelectedItem());
        btnDeleteAll.addActionListener(e -> deleteAllItems());
        btnConfirm.addActionListener(e -> submitOrder());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnDeleteAll);
        p.add(btnDelete);
        p.add(btnCancel);
        p.add(btnConfirm);

        return p;
    }
    /**
     * Load categories into the combo box
     * */
    private void loadCategories() {
        cbCategory.removeAllItems();
        categoryController.getAllCategories().forEach(cbCategory::addItem);
    }

    private void onCategoryChanged() {
        CategoryResponse c = (CategoryResponse) cbCategory.getSelectedItem();
        if (c == null) return;

        productModel.setRowCount(0);
        productController.getByCategory(c.getId()).forEach(p ->
                productModel.addRow(new Object[]{p.getId(), p.getName(), p.getStandardPrice()})
        );
    }

    private void onProductSelected() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) return;

        // Hiển thị giá sản phẩm
        BigDecimal price = (BigDecimal) productModel.getValueAt(row, 2);
        txtUnitPrice.setText(price.toString());

        Long productId = Long.parseLong(productModel.getValueAt(row, 0).toString());

        // Load warehouse
        List<InventoryResponse> stockList = inventoryController.getByProduct(productId);
        cbWarehouse.removeAllItems();

        if (stockList.isEmpty()) {
            btnAddItem.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Sản phẩm này hiện không còn tồn kho ở kho nào!");
        } else {
            stockList.forEach(cbWarehouse::addItem);
            cbWarehouse.setSelectedIndex(0);
            btnAddItem.setEnabled(true);
        }

        // Load batch (không filter, vẫn show hết để nhìn thấy đỏ)
        List<BatchResponse> batchList = productController.getBatchesByProduct(productId);
        cbBatch.removeAllItems();

        if (batchList.isEmpty()) {
            btnAddItem.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Sản phẩm này không có lô (batch) trong hệ thống!");
        } else {
            batchList.forEach(cbBatch::addItem);
            cbBatch.setSelectedIndex(0);
            btnAddItem.setEnabled(true);
        }
    }

    private void addOrderItem() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chưa chọn sản phẩm!");
            return;
        }

        BatchResponse batch = (BatchResponse) cbBatch.getSelectedItem();
        InventoryResponse wh = (InventoryResponse) cbWarehouse.getSelectedItem();

        // Check null trước
        if (wh == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn kho!");
            return;
        }
        if (batch == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn lô!");
            return;
        }

        // Check expiry ở đây thôi
        if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Lô này đã hết hạn! Không thể thêm vào đơn.", "Batch hết hạn", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int qty = (Integer) spQuantity.getValue();
        BigDecimal price = (BigDecimal) productModel.getValueAt(row, 2);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(Long.parseLong(productModel.getValueAt(row, 0).toString()));
        item.setBatchId(batch.getId());
        item.setWarehouseId(wh.getId());
        item.setQuantity(qty);
        item.setUnitPrice(price);
        item.setNote("");

        orderItems.add(item);

        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));
        orderItemModel.addRow(new Object[]{
                productModel.getValueAt(row, 1),
                wh.getWarehouseName(),
                batch.getBatchCode(),
                qty,
                price,
                lineTotal,
                ""
        });

        updateTotal();
    }

    private void deleteSelectedItem() {
        int row = tblOrderItems.getSelectedRow();
        if (row >= 0) {
            orderItems.remove(row);
            orderItemModel.removeRow(row);
            updateTotal();
        }
    }

    private void deleteAllItems() {
        orderItems.clear();
        orderItemModel.setRowCount(0);
        updateTotal();
    }

    private void updateTotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItemRequest i : orderItems) {
            subtotal = subtotal.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
        }

        BigDecimal discount = new BigDecimal(txtDiscount.getText());
        lblTotal.setText(subtotal.subtract(discount).toString());
    }

    private void submitOrder() {
        try {
            if (orderItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chưa có sản phẩm trong đơn!");
                return;
            }

            String customerText = txtCustomerId.getText();
            if (customerText == null || customerText.isBlank()) {
                JOptionPane.showMessageDialog(this, "Customer ID chưa nhập!");
                return;
            }

            long customerId;
            try {
                customerId = Long.parseLong(customerText.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Customer ID không hợp lệ!");
                return;
            }

            // Resolve performer (employee) id: use passed employeeId, else try current user_profile id
            Long performerId = this.employeeId;
            if (performerId == null) {
                performerId = AuthServiceImpl.getCurrentUserProfileId();
            }
            if (performerId == null) {
                JOptionPane.showMessageDialog(this, "Người thực hiện (employee) chưa được thiết lập. Vui lòng đăng nhập hoặc cung cấp employeeId.");
                return;
            }

            CreateOrderWithItemsRequest req = new CreateOrderWithItemsRequest();
            req.setCustomerId(customerId);
            req.setShippingAddress(txtShipping.getText());
            req.setDiscount(new BigDecimal(txtDiscount.getText()));
            req.setItems(orderItems);

            // call controller with resolved performer id
            Long orderId = orderController.createOrder(req, performerId);

            submitted = true;
            JOptionPane.showMessageDialog(this, "Đơn hàng đã được tạo (Trạng thái: NEW)!");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo đơn: " + e.getMessage());
        }
    }

}
