package org.example.view;

import org.example.entity.enums.Role;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {

    public ProfilePanel(String username, Role role) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 24, 24, 24));

        JLabel title = new JLabel("THÔNG TIN CÁ NHÂN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblUser = new JLabel("Tên đăng nhập: " + username);
        JLabel lblRole = new JLabel("Vai trò: " + role);

        JButton btnChangePassword = new JButton("Đổi mật khẩu");

        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnChangePassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(15));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(8));
        card.add(lblRole);
        card.add(Box.createVerticalStrut(20));
        card.add(btnChangePassword);

        add(card, BorderLayout.NORTH);

        // TODO: xử lý đổi mật khẩu sau
        btnChangePassword.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Chức năng đổi mật khẩu")
        );
    }
}
