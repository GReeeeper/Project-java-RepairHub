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
        ensureStatusBar();
        updateStatusBar();
        revalidate();
        repaint();
    }

    private void ensureStatusBar() {
        // Check if status bar already exists
        Component[] components = getContentPane().getComponents();
        boolean hasStatusBar = false;
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getComponentCount() > 0 && panel.getComponent(0) == statusLabel) {
                    hasStatusBar = true;
                    break;
                }
            }
        }
        
        if (!hasStatusBar) {
            JPanel status = new JPanel(new BorderLayout());
            status.setBackground(Theme.DASHBOARD_BG1);
            statusLabel.setForeground(Theme.TEXT_MUTED);
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));
            status.add(statusLabel, BorderLayout.WEST);
            getContentPane().add(status, BorderLayout.SOUTH);
        }
    }

    public void updateStatusBar() {
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
        ensureStatusBar();
        updateStatusBar();
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
                // Create a single-page signup dialog with all fields
                JDialog signupDialog = new JDialog(this, "Sign Up Owner", true);
                signupDialog.setModal(true);
                signupDialog.setSize(500, 600);
                signupDialog.setLocationRelativeTo(this);
                signupDialog.setResizable(false);
                signupDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                
                JPanel signupPanel = new JPanel(new GridBagLayout());
                signupPanel.setBackground(Theme.DASHBOARD_BG1);
                signupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                GridBagConstraints signupGbc = new GridBagConstraints();
                signupGbc.insets = new Insets(8, 10, 8, 10);
                signupGbc.anchor = GridBagConstraints.WEST;
                signupGbc.fill = GridBagConstraints.HORIZONTAL;
                
                // Username
                signupGbc.gridx = 0; signupGbc.gridy = 0;
                JLabel lblUsername = new JLabel("Username:");
                lblUsername.setForeground(Theme.TEXT_PRIMARY);
                signupPanel.add(lblUsername, signupGbc);
                signupGbc.gridx = 1;
                JTextField signupUsernameField = new JTextField(20);
                signupUsernameField.setBackground(Theme.INPUT_BG);
                signupUsernameField.setForeground(Theme.INPUT_TEXT);
                signupUsernameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                signupPanel.add(signupUsernameField, signupGbc);
                    
                // Email
                signupGbc.gridx = 0; signupGbc.gridy = 1;
                JLabel lblEmail = new JLabel("Email:");
                lblEmail.setForeground(Theme.TEXT_PRIMARY);
                signupPanel.add(lblEmail, signupGbc);
                signupGbc.gridx = 1;
                JTextField emailField = new JTextField(20);
                emailField.setBackground(Theme.INPUT_BG);
                emailField.setForeground(Theme.INPUT_TEXT);
                emailField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                signupPanel.add(emailField, signupGbc);
                
                // Phone
                signupGbc.gridx = 0; signupGbc.gridy = 2;
                JLabel lblPhone = new JLabel("Phone:");
                lblPhone.setForeground(Theme.TEXT_PRIMARY);
                signupPanel.add(lblPhone, signupGbc);
                signupGbc.gridx = 1;
                JTextField phoneField = new JTextField(20);
                phoneField.setBackground(Theme.INPUT_BG);
                phoneField.setForeground(Theme.INPUT_TEXT);
                phoneField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                signupPanel.add(phoneField, signupGbc);
                
                // Password
                signupGbc.gridx = 0; signupGbc.gridy = 3;
                JLabel lblPassword = new JLabel("Password:");
                lblPassword.setForeground(Theme.TEXT_PRIMARY);
                signupPanel.add(lblPassword, signupGbc);
                signupGbc.gridx = 1;
                JPasswordField signupPasswordField = new JPasswordField(20);
                signupPasswordField.setBackground(Theme.INPUT_BG);
                signupPasswordField.setForeground(Theme.INPUT_TEXT);
                signupPasswordField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                signupPanel.add(signupPasswordField, signupGbc);
                
                // Confirm Password
                signupGbc.gridx = 0; signupGbc.gridy = 4;
                JLabel lblConfirm = new JLabel("Confirm Password:");
                lblConfirm.setForeground(Theme.TEXT_PRIMARY);
                signupPanel.add(lblConfirm, signupGbc);
                signupGbc.gridx = 1;
                JPasswordField confirmField = new JPasswordField(20);
                confirmField.setBackground(Theme.INPUT_BG);
                confirmField.setForeground(Theme.INPUT_TEXT);
                confirmField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                signupPanel.add(confirmField, signupGbc);
                
                // Also a repairer checkbox
                signupGbc.gridx = 0; signupGbc.gridy = 5; signupGbc.gridwidth = 2;
                JCheckBox alsoRepairerCheck = new JCheckBox("Also a repairer?");
                alsoRepairerCheck.setForeground(Theme.TEXT_PRIMARY);
                alsoRepairerCheck.setBackground(Theme.DASHBOARD_BG1);
                signupPanel.add(alsoRepairerCheck, signupGbc);
                
                // Shop Name
                signupGbc.gridx = 0; signupGbc.gridy = 6; signupGbc.gridwidth = 1;
                JLabel lblShopName = new JLabel("Shop Name:");
                lblShopName.setForeground(Theme.TEXT_PRIMARY);
                signupPanel.add(lblShopName, signupGbc);
                signupGbc.gridx = 1;
                JTextField shopNameField = new JTextField(20);
                shopNameField.setBackground(Theme.INPUT_BG);
                shopNameField.setForeground(Theme.INPUT_TEXT);
                shopNameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                signupPanel.add(shopNameField, signupGbc);
                
                // Buttons
                signupGbc.gridx = 0; signupGbc.gridy = 7; signupGbc.gridwidth = 2;
                signupGbc.anchor = GridBagConstraints.CENTER;
                JPanel signupBtnPanel = new JPanel();
                signupBtnPanel.setOpaque(false);
                JButton submitBtn = new JButton("Sign Up");
                submitBtn.setBackground(Theme.ACCENT);
                submitBtn.setForeground(Theme.BUTTON_TEXT);
                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setBackground(Theme.ACCENT_DARK);
                cancelBtn.setForeground(Theme.BUTTON_TEXT);
                signupBtnPanel.add(submitBtn);
                signupBtnPanel.add(cancelBtn);
                signupPanel.add(signupBtnPanel, signupGbc);
                
                // Add title label at the top
                JLabel titleLabel = new JLabel("Create New Owner Account", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                titleLabel.setForeground(Theme.TEXT_PRIMARY);
                titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
                
                // Wrap in scroll pane to ensure all fields are accessible
                JScrollPane scrollPane = new JScrollPane(signupPanel);
                scrollPane.setBorder(null);
                scrollPane.setBackground(Theme.DASHBOARD_BG1);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                
                signupDialog.getContentPane().setLayout(new BorderLayout());
                signupDialog.getContentPane().add(titleLabel, BorderLayout.NORTH);
                signupDialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
                signupDialog.getContentPane().setBackground(Theme.DASHBOARD_BG1);
                
                // Set up action listeners before showing dialog
                submitBtn.addActionListener(ev -> {
                    String username = signupUsernameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String pass = new String(signupPasswordField.getPassword());
                    String confirm = new String(confirmField.getPassword());
                    boolean isAlsoRepairer = alsoRepairerCheck.isSelected();
                        
                    if (username.isEmpty() || pass.isEmpty()) {
                        JOptionPane.showMessageDialog(signupDialog, "Username and Password cannot be empty.");
                        return;
                    }
                    String shopName = shopNameField.getText().trim();
                    if (shopName.isEmpty()) {
                        JOptionPane.showMessageDialog(signupDialog, "Shop name is required.");
                        return;
                    }
                    try {
                        service.signUpOwner(username, email, phone, pass, confirm, isAlsoRepairer);
                        User owner = service.authenticate(username, pass);
                        service.createShopForOwner(owner, shopName);
                        JOptionPane.showMessageDialog(signupDialog, "✔ Owner Registered and Shop Created Successfully!");
                        signupDialog.dispose();
                        showDashboard(owner);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(signupDialog, ex.getMessage());
                    }
                });
                
                cancelBtn.addActionListener(ev -> signupDialog.dispose());
                
                // Validate and show the dialog
                signupDialog.validate();
                signupDialog.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating signup dialog: " + ex.getMessage());
                ex.printStackTrace();
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
        ensureStatusBar();
        updateStatusBar();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
