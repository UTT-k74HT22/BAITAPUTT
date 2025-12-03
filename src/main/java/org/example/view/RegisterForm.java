package org.example.view;

import org.example.entity.dto.RegisterRequest;
import org.example.service.AuthService;
import org.example.service.impl.AuthServiceImpl;

import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;

    private final AuthService authService;

    public RegisterForm() {
        this.authService = new AuthServiceImpl();

        setTitle("Đăng kí tài khoản");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // chỉ đóng form này, không tắt app
        setSize(400, 260);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(245, 245, 250));
        add(container);

        // ===== TITLE =====
        JLabel title = new JLabel("ĐĂNG KÝ TÀI KHOẢN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        container.add(title, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        container.add(formPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(18);
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(18);
        formPanel.add(txtPassword, gbc);

        // Confirm password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Nhập lại password:"), gbc);

        gbc.gridx = 1;
        txtConfirmPassword = new JPasswordField(18);
        formPanel.add(txtConfirmPassword, gbc);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 250));
        container.add(buttonPanel, BorderLayout.SOUTH);

        btnRegister = new JButton("Đăng ký");
        btnCancel = new JButton("Hủy");

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        // Events
        btnRegister.addActionListener(e -> onRegister());
        btnCancel.addActionListener(e -> dispose());

        // Enter = đăng ký
        getRootPane().setDefaultButton(btnRegister);
    }

    private void onRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirm  = new String(txtConfirmPassword.getPassword()).trim();

        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirm);

        try {
            authService.register(request);
            JOptionPane.showMessageDialog(
                    this,
                    "Đăng ký tài khoản thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose(); // đóng form đăng ký
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Lỗi đăng ký",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
