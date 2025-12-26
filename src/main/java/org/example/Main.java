package org.example;

import org.example.view.LoginForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
//        System.out.println(org.mindrot.jbcrypt.BCrypt.hashpw("123456789", org.mindrot.jbcrypt.BCrypt.gensalt(12)));

    }
}