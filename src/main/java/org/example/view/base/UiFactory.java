package org.example.view.base;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UiFactory {
    private UiFactory(){}

    public static JPanel cardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        return p;
    }

    public static JTextField textField(int cols, int width) {
        JTextField tf = new JTextField(cols);
        tf.setPreferredSize(new Dimension(width, 34));
        tf.setFont(UiTheme.FONT_BASE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JButton button(String text, Color bg, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(UiTheme.FONT_BOLD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.addActionListener(al);
        return b;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(UiTheme.FONT_BASE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UiTheme.SELECT);
        table.setSelectionForeground(UiTheme.TEXT);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);
    }

    public static void styleHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.CENTER);
        r.setBackground(UiTheme.PRIMARY);
        r.setForeground(Color.WHITE);
        r.setFont(UiTheme.FONT_BOLD);
        r.setBorder(new EmptyBorder(8, 10, 8, 10));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(r);
        }
    }

    public static DefaultTableCellRenderer zebraCenterRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setForeground(UiTheme.TEXT);
                setBackground(sel ? UiTheme.SELECT : (r % 2 == 0 ? Color.WHITE : UiTheme.ROW_ALT));
                return this;
            }
        };
    }
}
