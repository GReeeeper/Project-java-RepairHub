package presentation;

import javax.swing.*;
import java.awt.*;
import metier.models.User;
import metier.services.BusinessService;
import metier.services.UserService;

public class LoginPanel extends JPanel {

    private final UserService userService = new UserService();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public interface LoginCallback {
        void onLoginSuccess(User user);
    }

    private LoginCallback callback;

    public void setLoginCallback(LoginCallback callback) {
        this.callback = callback;
    }

    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {
            try {
                User user = userService.login(usernameField.getText(), new String(passwordField.getPassword()));
                if (callback != null) callback.onLoginSuccess(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
            }
        });

        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(loginBtn, gbc);
    }
}
