package org.example.view;

import org.example.service.AuthService;
import org.example.service.impl.AuthServiceImpl;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private JTextField username;
    private JPasswordField password;
    private JButton btnLogin;

    // service
    private final AuthService authService;

    public LoginForm() {
        this.authService = new AuthServiceImpl(); // inject service

        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        // ===== PANEL OUTER =====
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(245, 245, 250));
        add(container);

        // ===== TITLE =====
        JLabel title = new JLabel("QUẢN LÝ SINH VIÊN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(50, 50, 70));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        container.add(title, BorderLayout.NORTH);

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        container.add(formPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Label Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        // Input Username
        gbc.gridx = 1;
        username = new JTextField(18);
        username.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(username, gbc);

        // Label Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        // Input Password
        gbc.gridx = 1;
        password = new JPasswordField(18);
        password.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(password, gbc);

        // ===== LOGIN BUTTON =====
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // hover effect
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(60, 120, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(70, 130, 180));
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnLogin, gbc);

        // ===== LOGIN EVENT =====
        btnLogin.addActionListener(e -> onLogin());

        // enter để login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void onLogin() {
        String user = username.getText().trim();
        String pass = new String(password.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ username và password!",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = authService.login(user, pass);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

            new StudentTableFrame().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tài khoản hoặc mật khẩu!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
