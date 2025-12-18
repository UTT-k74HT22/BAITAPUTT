package org.example.view.base;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseManagementPanel<T> extends JPanel {

    protected JTable table;
    protected DefaultTableModel model;
    protected TableRowSorter<DefaultTableModel> sorter;

    protected JLabel totalLabel;

    // ---------- abstract contract ----------
    protected abstract String titleTotal();                 // "Tổng số nhân viên", "Tổng số khách hàng"...
    protected abstract String[] columns();
    protected abstract int[] columnWidths();
    protected abstract List<T> fetchData();
    protected abstract Object[] mapRow(T item, int stt);

    // optional hooks
    protected JPanel buildFilters() { return new JPanel(); }
    protected JPanel buildActions() { return new JPanel(); }
    protected void afterTableCreated() {}
    protected void applyFilters() {} // panel con override nếu dùng sorter filter

    public BaseManagementPanel() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        reload();
    }

    private JPanel buildToolbar() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setOpaque(false);

        JPanel filters = buildFilters();
        JPanel actions = buildActions();

        if (filters != null) root.add(filters);
        root.add(Box.createVerticalStrut(8));
        if (actions != null) root.add(actions);

        return root;
    }

    private JPanel buildTablePanel() {
        JPanel panel = UiFactory.cardPanel();

        model = new DefaultTableModel(columns(), 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        UiFactory.styleTable(table);
        UiFactory.styleHeader(table);

        // sorter (để search/filter)
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // widths
        int[] w = columnWidths();
        for (int i = 0; i < w.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        // default zebra renderer
        TableCellRenderer zebra = UiFactory.zebraCenterRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(zebra);
        }

        afterTableCreated();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);

        totalLabel = new JLabel(titleTotal() + ": 0");
        totalLabel.setFont(UiTheme.FONT_BOLD);
        totalLabel.setForeground(UiTheme.TEXT);

        p.add(totalLabel);
        return p;
    }

    // ---------- public API ----------
    public final void reload() {
        List<T> data = safe(fetchData());
        model.setRowCount(0);
        int stt = 1;
        for (T item : data) {
            model.addRow(mapRow(item, stt++));
        }
        totalLabel.setText(titleTotal() + ": " + data.size());
        applyFilters();
    }

    protected final int getSelectedModelRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return -1;
        return table.convertRowIndexToModel(viewRow);
    }

    protected final String getCellString(int modelRow, int col) {
        Object v = model.getValueAt(modelRow, col);
        return v == null ? "" : v.toString();
    }

    private List<T> safe(List<T> data) {
        return data == null ? new ArrayList<>() : data;
    }
}
