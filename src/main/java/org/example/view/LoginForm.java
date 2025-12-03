package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private JTextField username;
    private JPasswordField password;

    private JButton btnLogin;

    public LoginForm() {
        setTitle("Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400,400);
        setLocationRelativeTo(null);

        //call load components
        loadComponents();
    }

    private void loadComponents() {
        //Panel chính
        JPanel jPanel = new JPanel();
        jPanel.setBackground(new Color(230,230,235));
        jPanel.setLayout(null);

        //Set JLabel user
        JLabel lbUser = new JLabel("Nhập username:");
        lbUser.setBounds(30, 30, 100, 25);
        jPanel.add(lbUser);

        //Set TextField user
        username = new JTextField();
        username.setBounds(130, 30, 220, 30);
        jPanel.add(username);

        //Set JLabel password
        JLabel lbPass = new JLabel("Nhập passsword:");
        lbPass.setBounds(30, 90, 100, 25);
        jPanel.add(lbPass);

        //Set TextField password
        password = new JPasswordField();
        password.setBounds(130, 90, 220, 30);
        jPanel.add(password);

        //Set Button login
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBounds(130, 140, 120, 35);
        jPanel.add(btnLogin);

        //Add panel vào frame
        add(jPanel);

        //Event cho nút Đăng nhập
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });
    }

    private void onLogin() {
        String user = username.getText().trim();
        String pass = new String(this.password.getPassword()).trim();

        if ("admin".equals(user) && "123456".equals(pass)) {
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

            //load Student Table Frame
            StudentTableFrame frame = new StudentTableFrame();
            frame.setVisible(true);

            //close login form
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tài khoản hoặc mật khẩu!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
