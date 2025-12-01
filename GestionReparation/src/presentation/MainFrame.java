package presentation;

import metier.models.*;
import metier.services.BusinessService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final BusinessService service = new BusinessService();

    private static final Color PRIMARY_BG = Theme.DASHBOARD_BG1;
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);

    private String promptUser(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message);
    }

    private JPanel sidebar;
    private JPanel mainPanel;
    private User currentUser;
    private JLabel statusLabel = new JLabel();

    public void setMainPanel(JPanel content) {
        if (mainPanel != null) getContentPane().remove(mainPanel);
        mainPanel = content;
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        addStatusBar();
        revalidate();
        repaint();
    }

    private void addStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(Theme.DASHBOARD_BG1);
        statusLabel.setForeground(Theme.TEXT_MUTED);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));
        status.add(statusLabel, BorderLayout.WEST);
        getContentPane().add(status, BorderLayout.SOUTH);
        updateStatusBar();
    }

    private void updateStatusBar() {
        if (currentUser == null) {
            statusLabel.setText("Not logged in");
        } else {
            double completed = service.getCompletedTotalForUser(currentUser);
            double loans = service.getActiveLoanTotalForUser(currentUser);
            double net = completed - loans;
            statusLabel.setText(String.format("Completed: %.2f | Loans: %.2f | Net: %.2f", completed, loans, net));
        }
    }

    private void setupSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.DASHBOARD_BG2);
        sidebar.setPreferredSize(new Dimension(170, getHeight()));

        JButton btnDashboard = new JButton("Dashboard");
        JButton btnTrackReq = new JButton("Track Request");
        JButton btnLoans = new JButton("Loans & History");
        JButton btnManageRepairers = null;
        JButton btnLogout = new JButton("Logout");

        if (currentUser != null && (currentUser.getRole() == User.Role.OWNER || currentUser.getRole() == User.Role.BOTH)) {
            btnManageRepairers = new JButton("Manage Repairers");
            btnManageRepairers.setFont(MAIN_FONT);
            btnManageRepairers.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnManageRepairers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            sidebar.add(Box.createVerticalStrut(7));
            sidebar.add(btnManageRepairers);
            btnManageRepairers.addActionListener(e -> setMainPanel(new ManageRepairersPanel(currentUser, service)));
        }
        JButton[] allBtns = {btnDashboard, btnTrackReq, btnLoans, btnLogout};
        for (JButton btn : allBtns) {
            btn.setFont(MAIN_FONT);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            sidebar.add(Box.createVerticalStrut(7));
            sidebar.add(btn);
        }
        sidebar.add(Box.createVerticalGlue());
        btnDashboard.addActionListener(e -> setMainPanel(new DashboardPanel(currentUser, service)));
        btnTrackReq.addActionListener(e -> setMainPanel(new TrackRequestPanel(service.getRepairService())));
        btnLoans.addActionListener(e -> setMainPanel(new LoansPanel(currentUser, service)));
        btnLogout.addActionListener(e -> showLoginPanel());
    }

    private void showDashboard(User user) {
        getContentPane().removeAll();
        if (sidebar == null)
            setupSidebar();
        currentUser = user;
        mainPanel = new DashboardPanel(currentUser, service);
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public MainFrame() {
        setTitle("Repair Hub");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showLoginPanel();
    }

    private void showLoginPanel() {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PRIMARY_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,15,15,15);

        JTextField usernameField = new JTextField(15);
        usernameField.setFont(MAIN_FONT);
        usernameField.setBackground(Theme.INPUT_BG);
        usernameField.setForeground(Theme.INPUT_TEXT);
        usernameField.setCaretColor(Theme.INPUT_TEXT);
        usernameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(MAIN_FONT);
        passwordField.setBackground(Theme.INPUT_BG);
        passwordField.setForeground(Theme.INPUT_TEXT);
        passwordField.setCaretColor(Theme.INPUT_TEXT);
        passwordField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));

        JButton loginBtn = new JButton("Login");
        JButton signUpBtn = new JButton("Sign Up Owner");
        loginBtn.setFont(MAIN_FONT);
        signUpBtn.setFont(MAIN_FONT);
        loginBtn.setBackground(Theme.ACCENT);
        loginBtn.setForeground(Theme.BUTTON_TEXT);
        signUpBtn.setBackground(Theme.ACCENT_DARK);
        signUpBtn.setForeground(Theme.BUTTON_TEXT);

        JLabel userLbl = new JLabel("Username:");
        userLbl.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; panel.add(userLbl, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(usernameField, gbc);
        JLabel passLbl = new JLabel("Password:");
        passLbl.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(passLbl, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(loginBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(signUpBtn, gbc);

        signUpBtn.addActionListener(e -> {
            try {
                String username = promptUser(this, "Username:");
                String email = promptUser(this, "Email:");
                String phone = promptUser(this, "Phone:");
                String pass = promptUser(this, "Password:");
                String confirm = promptUser(this, "Confirm password:");
                int both = JOptionPane.showConfirmDialog(this, "Also a repairer?", "Owner Type", JOptionPane.YES_NO_OPTION);
                boolean isAlsoRepairer = both == JOptionPane.YES_OPTION;
                // validation in UI before passing to service
                if (username == null || username.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.");
                    return;
                }
                service.signUpOwner(username, email, phone, pass, confirm, isAlsoRepairer);
                JOptionPane.showMessageDialog(this, "✔ Owner Registered!");
                String shopName = promptUser(this, "Enter your shop name:");
                User owner = service.authenticate(username, pass);
                service.createShopForOwner(owner, shopName);
                JOptionPane.showMessageDialog(this, "✔ Shop created successfully!");
                showDashboard(owner);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        loginBtn.addActionListener(e -> {
            try {
                String uname = usernameField.getText();
                String pass = new String(passwordField.getPassword());
                if (uname.trim().isEmpty() || pass.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please provide both username and password.");
                    return;
                }
                User user = service.authenticate(uname, pass);
                if (user.getRole() == User.Role.OWNER || user.getRole() == User.Role.BOTH) {
                    if (service.getShopByOwner(user) == null) {
                        String shopName = promptUser(this, "Enter shop name:");
                        service.createShopForOwner(user, shopName);
                        JOptionPane.showMessageDialog(this, "✔ Shop created!");
                    }
                }
                showDashboard(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        JLabel clientLookupLabel = new JLabel("Client Repair Code Lookup");
        clientLookupLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        clientLookupLabel.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(clientLookupLabel, gbc);
        JTextField trackCodeField = new JTextField(13);
        trackCodeField.setBackground(Theme.INPUT_BG);
        trackCodeField.setForeground(Theme.INPUT_TEXT);
        trackCodeField.setCaretColor(Theme.INPUT_TEXT);
        trackCodeField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
        JButton btnTrack = new JButton("Track");
        btnTrack.setFont(MAIN_FONT);
        btnTrack.setBackground(Theme.ACCENT);
        btnTrack.setForeground(Theme.BUTTON_TEXT);
        JPanel trackPanel = new JPanel();
        trackPanel.add(trackCodeField); trackPanel.add(btnTrack);
        gbc.gridy = 4;
        panel.add(trackPanel, gbc);
        btnTrack.addActionListener(e -> {
            String code = trackCodeField.getText().trim();
            if(code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter your repair code.");
                return;
            }
            try {
                ClientTrackRequestPanel clientPanel = new ClientTrackRequestPanel(service.getRepairService(), code);
                JDialog dlg = new JDialog(this, "Track Your Request", true);
                dlg.getContentPane().add(clientPanel);
                dlg.setSize(440, 250);
                dlg.setLocationRelativeTo(this);
                dlg.setVisible(true);
            } catch (exception.InvalidInputException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
