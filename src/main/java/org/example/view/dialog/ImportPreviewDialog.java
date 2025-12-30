package org.example.view.dialog;

import org.example.dto.response.importer.ImportPreviewResponse;
import org.example.dto.response.importer.ImportRowData;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

/**
 * Dialog để preview dữ liệu import trước khi apply
 * Hiển thị cả dòng hợp lệ và dòng lỗi
 */
public class ImportPreviewDialog<T> extends JDialog {

    private JTable previewTable;
    private JLabel statusLabel;
    private JButton importButton;
    private JButton cancelButton;

    private ImportPreviewResponse<T> previewData;
    private boolean confirmed = false;
    private Function<T, Object[]> dataExtractor;
    private int dataColumnCount; // Số cột dữ liệu (không tính Row và Status)

    /**
     * @param parent Frame cha
     * @param previewData Dữ liệu preview
     * @param columnNames Tên các cột dữ liệu (không bao gồm Row và Status)
     * @param dataExtractor Function để extract data từ DTO thành mảng Object[]
     */
    public ImportPreviewDialog(Frame parent,
                              ImportPreviewResponse<T> previewData,
                              String[] columnNames,
                              Function<T, Object[]> dataExtractor) {
        super(parent, "Import Preview - Review Data", true);
        this.previewData = previewData;
        this.dataExtractor = dataExtractor;
        this.dataColumnCount = columnNames.length;

        setSize(1100, 650);
        setLocationRelativeTo(parent);
        initComponents(columnNames);
    }

    private void initComponents(String[] columnNames) {
        setLayout(new BorderLayout(10, 10));

        // Top Panel - Status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel();
        updateStatusLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(statusLabel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] extendedColumns = new String[columnNames.length + 2];
        extendedColumns[0] = "Row";
        extendedColumns[1] = "Status";
        System.arraycopy(columnNames, 0, extendedColumns, 2, columnNames.length);

        DefaultTableModel model = new DefaultTableModel(extendedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        previewTable = new JTable(model);
        previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        previewTable.setRowHeight(25);

        // Set column widths
        previewTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Row
        previewTable.getColumnModel().getColumn(1).setPreferredWidth(400); // Status
        for (int i = 2; i < extendedColumns.length; i++) {
            previewTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }

        // Custom renderer for status column
        previewTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                if (status.startsWith("✓")) {
                    c.setForeground(new Color(0, 128, 0)); // Green
                } else {
                    c.setForeground(new Color(200, 0, 0)); // Red
                }
                return c;
            }
        });

        loadTableData(model);

        JScrollPane scrollPane = new JScrollPane(previewTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        importButton = new JButton("Import Valid Rows (" + previewData.getValidCount() + ")");
        importButton.setEnabled(previewData.getValidCount() > 0);
        importButton.setBackground(new Color(46, 125, 50));
        importButton.setForeground(Color.WHITE);
        importButton.setFocusPainted(false);
        importButton.setPreferredSize(new Dimension(200, 35));
        importButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn import " + previewData.getValidCount() + " dòng hợp lệ?",
                "Xác nhận Import",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateStatusLabel() {
        String text = String.format(
            "<html>Tổng số dòng: <b>%d</b> | Hợp lệ: <b style='color:green;'>%d</b> | Lỗi: <b style='color:red;'>%d</b></html>",
            previewData.getTotalRows(),
            previewData.getValidCount(),
            previewData.getInvalidCount()
        );
        statusLabel.setText(text);
    }

    private void loadTableData(DefaultTableModel model) {
        // Add valid rows
        for (ImportRowData<T> rowData : previewData.getValidRows()) {
            Object[] row = buildTableRow(rowData, "✓ Valid");
            model.addRow(row);
        }

        // Add invalid rows
        for (ImportRowData<T> rowData : previewData.getInvalidRows()) {
            String errorMsg = "✗ " + rowData.getErrorSummary();
            Object[] row = buildTableRow(rowData, errorMsg);
            model.addRow(row);
        }
    }

    private Object[] buildTableRow(ImportRowData<T> rowData, String status) {
        // Handle null data (khi có lỗi parse row)
        Object[] dataValues;
        if (rowData.getData() == null) {
            // Tạo mảng rỗng với placeholder
            dataValues = new Object[dataColumnCount];
            for (int i = 0; i < dataValues.length; i++) {
                dataValues[i] = "-";
            }
        } else {
            dataValues = dataExtractor.apply(rowData.getData());
        }

        Object[] row = new Object[dataValues.length + 2];
        row[0] = rowData.getRowIndex();
        row[1] = status;
        System.arraycopy(dataValues, 0, row, 2, dataValues.length);
        return row;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<T> getValidData() {
        return previewData.getValidRows().stream()
            .map(ImportRowData::getData)
            .toList();
    }
}

