package org.example.view;

import org.example.dto.RegisterRequest;
import org.example.service.AuthService;
import org.example.service.impl.AuthServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JLabel linkLogin;

    private final AuthService authService;

    public RegisterForm() {
        this.authService = new AuthServiceImpl();

        setTitle("Đăng kí tài khoản");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 280);
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

        // ===== REGISTER BUTTON =====
        btnRegister = new JButton("Đăng ký");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setBackground(new Color(70, 130, 180));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // hover effect
        btnRegister.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnRegister.setBackground(new Color(60, 120, 170));
            }
            public void mouseExited(MouseEvent evt) {
                btnRegister.setBackground(new Color(70, 130, 180));
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnRegister, gbc);

        // ===== LOGIN LINK =====
        linkLogin = new JLabel("<html><u>Đã có tài khoản? Đăng nhập ngay</u></html>");
        linkLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        linkLogin.setForeground(new Color(70, 130, 180));
        linkLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // hover effect for link
        linkLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // đóng form đăng ký
                new LoginForm().setVisible(true); // mở form đăng nhập
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                linkLogin.setForeground(new Color(50, 100, 150));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                linkLogin.setForeground(new Color(70, 130, 180));
            }
        });

        gbc.gridy = 4;
        gbc.insets = new Insets(10, 8, 15, 8);
        formPanel.add(linkLogin, gbc);

        // Events
        btnRegister.addActionListener(e -> onRegister());

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
            new LoginForm().setVisible(true); // tự động mở form login
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