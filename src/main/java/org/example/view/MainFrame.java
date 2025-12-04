package org.example.view;

import org.example.view.layout.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;

    public MainFrame(String username, String role) {

        setTitle("Student Manager");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Layout components
        add(new Header(username, role), BorderLayout.NORTH);
        SideBar sidebar = new SideBar(role);
        add(sidebar, BorderLayout.WEST);
        add(new Footer(), BorderLayout.SOUTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        // Example action
        sidebar.getMenu("Quản lý sinh viên").addActionListener(e -> {
            setContent(new StudentTableFrame());
        });

        // Role ADMIN mới có
        if (sidebar.getMenu("Quản lý tài khoản") != null) {
            sidebar.getMenu("Quản lý tài khoản").addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Trang quản lý tài khoản (ADMIN)");
            });
        }
    }

    public void setContent(StudentTableFrame panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
