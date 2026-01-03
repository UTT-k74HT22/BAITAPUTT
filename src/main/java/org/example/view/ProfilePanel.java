package org.example.view;

import org.example.entity.enums.Role;
import org.example.dto.response.ProfileResponse;
import org.example.controller.ProfileController;
import org.example.dto.request.UpdateUserProfileRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private final ProfileController profileController;
    private final long accountId;

    private ProfileResponse currentProfile; // lưu dữ liệu hiện tại

    public ProfilePanel(ProfileResponse profile, ProfileController profileController, long accountId) {
        this.profileController = profileController;
        this.accountId = accountId;
        this.currentProfile = profile;

        setBackground(Color.WHITE);
        setOpaque(true);

        showProfile(currentProfile);
    }

    // ===== Hiển thị profile =====
    private void showProfile(ProfileResponse profile) {
        removeAll(); // xóa component cũ

        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(new EmptyBorder(30, 40, 30, 40));
        wrapper.setBackground(Color.WHITE);

        // ===== Title =====
        JLabel title = new JLabel("Thông tin cá nhân");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(20));

        // ===== Content =====
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        row = addRow(content, gbc, row, "Tên đăng nhập", profile.getUsername());
        if (profile.getRole() == Role.ADMIN) {
            row = addRow(content, gbc, row, "Vai trò", profile.getRole().toString());
        }
        row = addRow(content, gbc, row, "Họ tên", profile.getFullName());
        row = addRow(content, gbc, row, "Số điện thoại", profile.getPhone());
        row = addRow(content, gbc, row, "Email", profile.getEmail());
        row = addRow(content, gbc, row, "Địa chỉ", profile.getAddress());
        if (profile.getPosition() != null)
            row = addRow(content, gbc, row, "Chức vụ", profile.getPosition());
        if (profile.getDepartment() != null)
            row = addRow(content, gbc, row, "Phòng ban", profile.getDepartment());
        if (profile.getCustomerType() != null)
            row = addRow(content, gbc, row, "Loại khách hàng", profile.getCustomerType());

        wrapper.add(content);

        // ===== Buttons =====
        wrapper.add(Box.createVerticalStrut(25));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnRefresh = new JButton("Refresh");
        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        JButton btnEditProfile = new JButton("Sửa thông tin");
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnChangePassword);
        buttonPanel.add(btnEditProfile);
        wrapper.add(buttonPanel);

        // ===== Sự kiện sửa profile =====
        btnRefresh.addActionListener(e -> refreshProfile());
        btnEditProfile.addActionListener(e -> openEditProfileDialog());
        btnChangePassword.addActionListener(e -> openChangePassword());

        add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private int addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLabel.setPreferredSize(new Dimension(160, 22));
        panel.add(lblLabel, gbc);

        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value != null ? value : "-");
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblValue, gbc);

        return row + 1;
    }

    // ===== Popup sửa profile =====
    private void openEditProfileDialog() {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Sửa thông tin cá nhân",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        // Họ tên
        gbc.gridy = row++;
        content.add(new JLabel("Họ tên"), gbc);
        JTextField txtFullName = new JTextField(currentProfile.getFullName());
        gbc.gridy = row++;
        content.add(txtFullName, gbc);

        // SĐT
        gbc.gridy = row++;
        content.add(new JLabel("Số điện thoại"), gbc);
        JTextField txtPhone = new JTextField(currentProfile.getPhone());
        gbc.gridy = row++;
        content.add(txtPhone, gbc);

        // Email
        gbc.gridy = row++;
        content.add(new JLabel("Email"), gbc);
        JTextField txtEmail = new JTextField(currentProfile.getEmail());
        gbc.gridy = row++;
        content.add(txtEmail, gbc);

        // Địa chỉ
        gbc.gridy = row++;
        content.add(new JLabel("Địa chỉ"), gbc);
        JTextField txtAddress = new JTextField(currentProfile.getAddress());
        gbc.gridy = row++;
        content.add(txtAddress, gbc);

        dialog.add(content, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String fullName = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String address = txtAddress.getText().trim();

            if (fullName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Họ tên không được để trống");
                return;
            }

            UpdateUserProfileRequest req = new UpdateUserProfileRequest();
            req.setFullName(fullName);
            req.setPhone(phone);
            req.setEmail(email);
            req.setAddress(address);

            try {
                profileController.updateMyProfile(accountId, req);

                currentProfile = profileController.loadProfile(currentProfile.getUsername(), currentProfile.getRole());

                showProfile(currentProfile);

                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Cập nhật thất bại: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    public void openChangePassword() {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Đổi mật khẩu",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        // Mật khẩu cũ
        gbc.gridy = row++;
        content.add(new JLabel("Mật khẩu cũ"), gbc);
        JPasswordField txtOld = new JPasswordField();
        gbc.gridy = row++;
        content.add(txtOld, gbc);

        // Mật khẩu mới
        gbc.gridy = row++;
        content.add(new JLabel("Mật khẩu mới"), gbc);
        JPasswordField txtNew = new JPasswordField();
        gbc.gridy = row++;
        content.add(txtNew, gbc);

        // Nhập lại mật khẩu mới
        gbc.gridy = row++;
        content.add(new JLabel("Nhập lại mật khẩu"), gbc);
        JPasswordField txtConfirm = new JPasswordField();
        gbc.gridy = row++;
        content.add(txtConfirm, gbc);

        dialog.add(content, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Đổi");
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String oldPass = new String(txtOld.getPassword()).trim();
            String newPass = new String(txtNew.getPassword()).trim();
            String confirm = new String(txtConfirm.getPassword()).trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Không được để trống các trường");
                return;
            }

            if (!newPass.equals(confirm)) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu mới và xác nhận không khớp");
                return;
            }

            try {
                profileController.changePassword(accountId, oldPass, newPass);
                JOptionPane.showMessageDialog(dialog, "Đổi mật khẩu thành công!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Đổi mật khẩu thất bại: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }


    private void refreshProfile() {
        currentProfile = profileController.loadProfile(
                currentProfile.getUsername(),
                currentProfile.getRole()
        );
        showProfile(currentProfile);
    }
}
