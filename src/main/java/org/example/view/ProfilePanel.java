package org.example.view;

import org.example.entity.enums.Role;
import org.example.dto.response.ProfileResponse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private int row = 0;

    public ProfilePanel(ProfileResponse profile) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setOpaque(true);

        // ===== Wrapper =====
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new EmptyBorder(30, 40, 30, 40));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setBackground(Color.WHITE);
        wrapper.setOpaque(true);

        // ===== Title =====
        JLabel title = new JLabel("Thông tin cá nhân");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(title);

        wrapper.add(Box.createVerticalStrut(20));

        // ===== Content =====
        JPanel content = new JPanel(new GridBagLayout());
        content.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.setBackground(Color.WHITE);
        content.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.WEST;

        addRow(content, gbc, "Tên đăng nhập", profile.getUsername());

        if (profile.getRole() == Role.ADMIN) {
            addRow(content, gbc, "Vai trò", profile.getRole().toString());
        }

        addRow(content, gbc, "Họ tên", profile.getFullName());
        addRow(content, gbc, "Số điện thoại", profile.getPhone());
        addRow(content, gbc, "Email", profile.getEmail());
        addRow(content, gbc, "Địa chỉ", profile.getAddress());

        if (profile.getPosition() != null)
            addRow(content, gbc, "Chức vụ", profile.getPosition());

        if (profile.getDepartment() != null)
            addRow(content, gbc, "Phòng ban", profile.getDepartment());

        if (profile.getCustomerType() != null)
            addRow(content, gbc, "Loại khách hàng", profile.getCustomerType());

        wrapper.add(content);

        // ===== Buttons =====
        wrapper.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setOpaque(true);

        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        JButton btnEditProfile = new JButton("Sửa thông tin");

        buttonPanel.add(btnChangePassword);
        buttonPanel.add(btnEditProfile);
        wrapper.add(buttonPanel);

        // ===== Add wrapper =====
        add(wrapper, BorderLayout.CENTER);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridy = row++;

        gbc.gridx = 0;
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLabel.setPreferredSize(new Dimension(160, 22));
        panel.add(lblLabel, gbc);

        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value != null ? value : "-");
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblValue, gbc);
    }
}
