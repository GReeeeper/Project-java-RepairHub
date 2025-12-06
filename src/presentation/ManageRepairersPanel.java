package presentation;

import metier.models.User;
import metier.models.Repair;
import metier.models.Shop;
import metier.services.BusinessService;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ManageRepairersPanel extends JPanel {
    private final User owner;
    private final BusinessService service;
    private JPanel listPanel;

    public ManageRepairersPanel(User owner, BusinessService service) {
        this.owner = owner;
        this.service = service;
        setLayout(new BorderLayout(10,10));
        setBackground(Theme.DASHBOARD_BG1);
        JLabel lblTitle = new JLabel("Manage Your Shop's Repairers", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        add(lblTitle, BorderLayout.NORTH);
        JButton btnAdd = new JButton("Add Repairer");
        btnAdd.setToolTipText("Add a new repairer to your shop");
        btnAdd.setBackground(Theme.ACCENT);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setForeground(Theme.BUTTON_TEXT);
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        add(btnPanel, BorderLayout.SOUTH);
        // List area
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(20, 28, 40)); // Same as loans panel
        updateRepairersList();
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER, 1));
        scroll.getViewport().setBackground(new Color(20, 28, 40)); // Same as loans panel
        add(scroll, BorderLayout.CENTER);
        btnAdd.addActionListener(e -> {
            // Create a single-page add repairer dialog
            JDialog addDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Repairer", true);
            addDialog.setSize(400, 300);
            addDialog.setLocationRelativeTo(this);
            
            JPanel addPanel = new JPanel(new GridBagLayout());
            addPanel.setBackground(Theme.DASHBOARD_BG1);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Username
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel lblUsername = new JLabel("Username:");
            lblUsername.setForeground(Theme.TEXT_PRIMARY);
            addPanel.add(lblUsername, gbc);
            gbc.gridx = 1;
            JTextField usernameField = new JTextField(20);
            usernameField.setBackground(Theme.INPUT_BG);
            usernameField.setForeground(Theme.INPUT_TEXT);
            usernameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
            addPanel.add(usernameField, gbc);
            
            // Password
            gbc.gridx = 0; gbc.gridy = 1;
            JLabel lblPassword = new JLabel("Password:");
            lblPassword.setForeground(Theme.TEXT_PRIMARY);
            addPanel.add(lblPassword, gbc);
            gbc.gridx = 1;
            JPasswordField passwordField = new JPasswordField(20);
            passwordField.setBackground(Theme.INPUT_BG);
            passwordField.setForeground(Theme.INPUT_TEXT);
            passwordField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
            addPanel.add(passwordField, gbc);
            
            // Email
            gbc.gridx = 0; gbc.gridy = 2;
            JLabel lblEmail = new JLabel("Email:");
            lblEmail.setForeground(Theme.TEXT_PRIMARY);
            addPanel.add(lblEmail, gbc);
            gbc.gridx = 1;
            JTextField emailField = new JTextField(20);
            emailField.setBackground(Theme.INPUT_BG);
            emailField.setForeground(Theme.INPUT_TEXT);
            emailField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
            addPanel.add(emailField, gbc);
            
            // Phone
            gbc.gridx = 0; gbc.gridy = 3;
            JLabel lblPhone = new JLabel("Phone:");
            lblPhone.setForeground(Theme.TEXT_PRIMARY);
            addPanel.add(lblPhone, gbc);
            gbc.gridx = 1;
            JTextField phoneField = new JTextField(20);
            phoneField.setBackground(Theme.INPUT_BG);
            phoneField.setForeground(Theme.INPUT_TEXT);
            phoneField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
            addPanel.add(phoneField, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            JPanel addRepairerBtnPanel = new JPanel();
            addRepairerBtnPanel.setOpaque(false);
            JButton submitBtn = new JButton("Add Repairer");
            submitBtn.setBackground(Theme.ACCENT);
            submitBtn.setForeground(Theme.BUTTON_TEXT);
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(Theme.ACCENT_DARK);
            cancelBtn.setForeground(Theme.BUTTON_TEXT);
            addRepairerBtnPanel.add(submitBtn);
            addRepairerBtnPanel.add(cancelBtn);
            addPanel.add(addRepairerBtnPanel, gbc);
            
            addDialog.getContentPane().add(addPanel);
            
            submitBtn.addActionListener(ev -> {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                
                if(username.isBlank() || password.isBlank()) {
                    JOptionPane.showMessageDialog(addDialog, "Username and password required.");
                    return;
                }
                try {
                    service.addRepairer(username, password, email, phone, service.getShopByOwner(owner));
                    updateRepairersList();
                    JOptionPane.showMessageDialog(addDialog, "Repairer added.");
                    addDialog.dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(addDialog, ex.getMessage());
                }
            });
            
            cancelBtn.addActionListener(ev -> addDialog.dispose());
            
            addDialog.setVisible(true);
        });
    }

    private void updateRepairersList() {
        listPanel.removeAll();
        List<User> repairers = getRepairersForShop();
        if(repairers.isEmpty()) {
            JLabel lbl = new JLabel("No repairers yet.", SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lbl.setForeground(Theme.TEXT_MUTED);
            listPanel.add(lbl);
        } else {
            for (User r : repairers) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 10, 5, 10),
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER)));
                card.setBackground(new Color(25, 35, 50)); // Same as loans panel rows
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                JTextArea info = new JTextArea();
                info.setEditable(false);
                info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                info.setBackground(new Color(25, 35, 50)); // Same as loans panel rows
                info.setForeground(Theme.TEXT_PRIMARY);
                info.setText("Username: " + r.getUsername() +
                        "\nRole: " + r.getRole() +
                        "\nEmail: " + (r.getEmail() != null ? r.getEmail() : "-") +
                        "\nPhone: " + (r.getPhone() != null ? r.getPhone() : "-"));
                card.add(info, BorderLayout.CENTER);
                JPanel actions = new JPanel();
                actions.setOpaque(false);
                JButton btnUpdate = new JButton("Update");
                btnUpdate.setBackground(Theme.ACCENT);
                btnUpdate.setForeground(Theme.BUTTON_TEXT);
                btnUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                JButton btnDelete = new JButton("Delete");
                btnDelete.setBackground(Theme.ACCENT_DARK);
                btnDelete.setForeground(Theme.BUTTON_TEXT);
                btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                JButton btnShowRepairs = new JButton("Show Repairs");
                btnShowRepairs.setBackground(Theme.ACCENT_DARK);
                btnShowRepairs.setForeground(Theme.BUTTON_TEXT);
                btnShowRepairs.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btnUpdate.setToolTipText("Edit repairer's info except password.");
                btnDelete.setToolTipText("Remove this repairer from your shop.");
                btnShowRepairs.setToolTipText("See all repair requests assigned to this repairer.");
                actions.add(btnUpdate); actions.add(btnDelete); actions.add(btnShowRepairs);
                card.add(actions, BorderLayout.EAST);
                btnUpdate.addActionListener(e -> {
                    JTextField emailF = new JTextField(r.getEmail());
                    JTextField phoneF = new JTextField(r.getPhone());
                    JComboBox<User.Role> roleBox = new JComboBox<>(User.Role.values());
                    roleBox.setSelectedItem(r.getRole());
                    JPanel form = new JPanel(new GridLayout(0,2));
                    form.add(new JLabel("Email:")); form.add(emailF);
                    form.add(new JLabel("Phone:")); form.add(phoneF);
                    form.add(new JLabel("Role:")); form.add(roleBox);
                    int res = JOptionPane.showConfirmDialog(this, form, "Edit Repairer", JOptionPane.OK_CANCEL_OPTION);
                    if(res == JOptionPane.OK_OPTION) {
                        r.setEmail(emailF.getText());
                        r.setPhone(phoneF.getText());
                        r.setRole((User.Role)roleBox.getSelectedItem());
                        JOptionPane.showMessageDialog(this, "Repairer info updated.");
                        updateRepairersList();
                    }
                });
                btnDelete.addActionListener(e -> {
                    if(JOptionPane.showConfirmDialog(this, "Remove " + r.getUsername() + " from your shop?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        Shop shop = service.getShopByOwner(owner);
                        if (shop != null) {
                            service.removeRepairer(r, shop);
                            updateRepairersList();
                            JOptionPane.showMessageDialog(this, r.getUsername() + " removed from your shop.");
                        }
                    }
                });
                btnShowRepairs.addActionListener(e -> {
                    List<Repair> repairs = service.getRepairsByRepairer(r);
                    JTextArea ta = new JTextArea(20,50); ta.setEditable(false);
                    if(repairs.isEmpty()) ta.setText("No repair requests assigned.");
                    else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("--- Repair Requests for ").append(r.getUsername()).append(" ---\n");
                        for(var rep : repairs) sb.append("Code: ").append(rep.getCode())
                            .append(" | Device: ").append(rep.getDevice().getBrand()).append(" ").append(rep.getDevice().getModel())
                            .append(" | Client: ").append(rep.getClientName())
                            .append(" | Status: ").append(rep.getStatus())
                            .append(" | Cost: ").append(rep.getTotalCost())
                            .append(" | Desc: ").append(rep.getDescription()).append("\n");
                        ta.setText(sb.toString());
                    }
                    JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Repairs for " + r.getUsername(), JOptionPane.PLAIN_MESSAGE);
                });
                listPanel.add(card);
            }
        }
        revalidate();
        repaint();
    }
    private List<User> getRepairersForShop() {
        Shop shop = service.getShopByOwner(owner);
        return shop != null ? shop.getRepairers() : new ArrayList<>();
    }
}
