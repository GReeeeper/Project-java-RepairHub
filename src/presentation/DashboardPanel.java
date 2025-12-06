package presentation;

import metier.models.User;
import metier.models.Shop;
import metier.services.BusinessService;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final User user;
    private final BusinessService service;
    
    public DashboardPanel(User user, BusinessService service) {
        this.user = user;
        this.service = service;
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Theme.DASHBOARD_BG1);
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if(g instanceof Graphics2D) {
                    Graphics2D g2 = (Graphics2D)g;
                    GradientPaint grad = new GradientPaint(0,0, Theme.DASHBOARD_BG1, 0,getHeight(), Theme.DASHBOARD_BG2);
                    g2.setPaint(grad);
                    g2.fillRect(0,0,getWidth(),getHeight());
                } else {
                    super.paintComponent(g);
                }
            }
        };
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        String shopName = "-";
        if (user != null && service != null && (user.getRole() == User.Role.OWNER || user.getRole() == User.Role.BOTH)) {
            Shop shop = service.getShopByOwner(user);
            if (shop != null) shopName = shop.getName();
        }
        JLabel lblShop = new JLabel("Shop: " + shopName, SwingConstants.CENTER);
        lblShop.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblShop.setForeground(Theme.TEXT_PRIMARY);
        header.add(Box.createVerticalStrut(8));
        header.add(lblShop);
        JLabel lblWelcome = new JLabel("Welcome " + (user != null ? user.getUsername() : "User") + "!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(Theme.TEXT_PRIMARY);
        header.add(Box.createVerticalStrut(5));
        header.add(lblWelcome);
        header.add(Box.createVerticalStrut(18));
        add(header, BorderLayout.NORTH);
        JPanel actionsPanel = new JPanel(new GridBagLayout());
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(0, 20, 13, 10);
        gbc.gridy = 0;
        if (user != null && user.getRole() == User.Role.BOTH) {
            // Owner who is also a repairer - show all 6 actions
            String[][] actionDefs = {
                {"View Shop", "See your shop's details and repairers list."},
                {"Add Repairer", "Add a new repairer to your shop."},
                {"Manage Repairers", "View/update/remove your shop's repairers."},
                {"Add Repair Request", "Add a new device repair for a client."},
                {"Show My Repairs", "See all repair requests you manage."},
                {"Update Status", "Change the status of a repair request."}
            };
            AnimatableButton[] btns = new AnimatableButton[actionDefs.length];
            JLabel[] hintLbls = new JLabel[actionDefs.length];
            for(int i=0; i<actionDefs.length; ++i) {
                btns[i] = new AnimatableButton(actionDefs[i][0]);
                hintLbls[i] = new JLabel(actionDefs[i][1]);
                hintLbls[i].setFont(new Font("Segoe UI", Font.ITALIC, 14));
                hintLbls[i].setForeground(Theme.TEXT_MUTED);
                btns[i].setHorizontalAlignment(SwingConstants.LEFT);
                btns[i].setAlignmentX(Component.LEFT_ALIGNMENT);
                gbc.gridx = 0; actionsPanel.add(btns[i], gbc);
                gbc.gridx = 1; actionsPanel.add(hintLbls[i], gbc);
                gbc.gridy++;
            }
            btns[0].addActionListener(e -> {
                JTextArea ta = new JTextArea(20,50); ta.setEditable(false);
                StringBuilder sb = new StringBuilder();
                var shop = service.getShopByOwner(user);
                if(shop == null) sb.append("No shop found.");
                else {
                    sb.append("Shop Info\n");
                    sb.append("Shop Name: ").append(shop.getName()).append("\n");
                    sb.append("Owner: ").append(user.getUsername()).append("\n\n");
                    
                    // Calculate sum of all repairers' net (completed - loans)
                    double totalNet = 0.0;
                    
                    // Include owner's net if they are also a repairer
                    if(user.getRole() == User.Role.BOTH || user.getRole() == User.Role.REPAIRER) {
                        double ownerCompleted = service.getCompletedTotalForUser(user);
                        double ownerLoans = service.getActiveLoanTotalForUser(user);
                        totalNet += (ownerCompleted - ownerLoans);
                    }
                    
                    // Add all repairers' net
                    for(var r : shop.getRepairers()) {
                        double completed = service.getCompletedTotalForUser(r);
                        double loans = service.getActiveLoanTotalForUser(r);
                        totalNet += (completed - loans);
                    }
                    
                    sb.append("Total Cash Available: ").append(String.format("%.2f", totalNet)).append("\n\n");
                    sb.append("Repairers:\n");
                    if(user.getRole() == User.Role.BOTH || user.getRole() == User.Role.REPAIRER) {
                        double ownerCompleted = service.getCompletedTotalForUser(user);
                        double ownerLoans = service.getActiveLoanTotalForUser(user);
                        double ownerNet = ownerCompleted - ownerLoans;
                        sb.append(" - ").append(user.getUsername()).append(" (Owner) - Net: ").append(String.format("%.2f", ownerNet)).append("\n");
                    }
                    for(var r : shop.getRepairers()) {
                        double completed = service.getCompletedTotalForUser(r);
                        double loans = service.getActiveLoanTotalForUser(r);
                        double net = completed - loans;
                        sb.append(" - ").append(r.getUsername()).append(" - Net: ").append(String.format("%.2f", net)).append("\n");
                    }
                }
                ta.setText(sb.toString());
                JOptionPane.showMessageDialog(null, new JScrollPane(ta), "Shop Info", JOptionPane.PLAIN_MESSAGE);
            });
            btns[1].addActionListener(e -> {
                // Create a single-page add repairer dialog
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                JDialog addDialog = new JDialog(topFrame, "Add Repairer", true);
                addDialog.setSize(400, 300);
                addDialog.setLocationRelativeTo(this);
                
                JPanel addPanel = new JPanel(new GridBagLayout());
                addPanel.setBackground(Theme.DASHBOARD_BG1);
                GridBagConstraints repairerGbc = new GridBagConstraints();
                repairerGbc.insets = new Insets(5, 10, 5, 10);
                repairerGbc.anchor = GridBagConstraints.WEST;
                
                // Username
                repairerGbc.gridx = 0; repairerGbc.gridy = 0;
                JLabel lblUsername = new JLabel("Username:");
                lblUsername.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblUsername, repairerGbc);
                repairerGbc.gridx = 1;
                JTextField repairerUsernameField = new JTextField(20);
                repairerUsernameField.setBackground(Theme.INPUT_BG);
                repairerUsernameField.setForeground(Theme.INPUT_TEXT);
                repairerUsernameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(repairerUsernameField, repairerGbc);
                
                // Password
                repairerGbc.gridx = 0; repairerGbc.gridy = 1;
                JLabel lblPassword = new JLabel("Password:");
                lblPassword.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblPassword, repairerGbc);
                repairerGbc.gridx = 1;
                JPasswordField repairerPasswordField = new JPasswordField(20);
                repairerPasswordField.setBackground(Theme.INPUT_BG);
                repairerPasswordField.setForeground(Theme.INPUT_TEXT);
                repairerPasswordField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(repairerPasswordField, repairerGbc);
                
                // Email
                repairerGbc.gridx = 0; repairerGbc.gridy = 2;
                JLabel lblEmail = new JLabel("Email:");
                lblEmail.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblEmail, repairerGbc);
                repairerGbc.gridx = 1;
                JTextField repairerEmailField = new JTextField(20);
                repairerEmailField.setBackground(Theme.INPUT_BG);
                repairerEmailField.setForeground(Theme.INPUT_TEXT);
                repairerEmailField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(repairerEmailField, repairerGbc);
                
                // Phone
                repairerGbc.gridx = 0; repairerGbc.gridy = 3;
                JLabel lblPhone = new JLabel("Phone:");
                lblPhone.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblPhone, repairerGbc);
                repairerGbc.gridx = 1;
                JTextField repairerPhoneField = new JTextField(20);
                repairerPhoneField.setBackground(Theme.INPUT_BG);
                repairerPhoneField.setForeground(Theme.INPUT_TEXT);
                repairerPhoneField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(repairerPhoneField, repairerGbc);
                
                // Buttons
                repairerGbc.gridx = 0; repairerGbc.gridy = 4; repairerGbc.gridwidth = 2;
                repairerGbc.anchor = GridBagConstraints.CENTER;
                JPanel btnPanel = new JPanel();
                btnPanel.setOpaque(false);
                JButton submitBtn = new JButton("Add Repairer");
                submitBtn.setBackground(Theme.ACCENT);
                submitBtn.setForeground(Theme.BUTTON_TEXT);
                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setBackground(Theme.ACCENT_DARK);
                cancelBtn.setForeground(Theme.BUTTON_TEXT);
                btnPanel.add(submitBtn);
                btnPanel.add(cancelBtn);
                addPanel.add(btnPanel, gbc);
                
                addDialog.getContentPane().add(addPanel);
                
                submitBtn.addActionListener(ev -> {
                    String username = repairerUsernameField.getText().trim();
                    String password = new String(repairerPasswordField.getPassword());
                    String email = repairerEmailField.getText().trim();
                    String phone = repairerPhoneField.getText().trim();
                    
                    if(username.isBlank() || password.isBlank()) {
                        JOptionPane.showMessageDialog(addDialog, "Username and Password required.");
                        return;
                    }
                    try {
                        service.addRepairer(username, password, email, phone, service.getShopByOwner(user));
                        JOptionPane.showMessageDialog(addDialog, "Repairer added.");
                        addDialog.dispose();
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(addDialog, ex.getMessage());
                    }
                });
                
                cancelBtn.addActionListener(ev -> addDialog.dispose());
                
                addDialog.setVisible(true);
            });
            btns[2].addActionListener(e -> {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (topFrame instanceof MainFrame) {
                    ((MainFrame)topFrame).setMainPanel(new ManageRepairersPanel(user, service));
                }
            });
            btns[3].addActionListener(e -> {
                // Create a single-page add repair request dialog
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                JDialog addDialog = new JDialog(topFrame, "Add Repair Request", true);
                addDialog.setSize(400, 500);
                addDialog.setLocationRelativeTo(this);
                
                JPanel addPanel = new JPanel(new GridBagLayout());
                addPanel.setBackground(Theme.DASHBOARD_BG1);
                GridBagConstraints repairGbc = new GridBagConstraints();
                repairGbc.insets = new Insets(5, 10, 5, 10);
                repairGbc.anchor = GridBagConstraints.WEST;
                
                // Client Name
                repairGbc.gridx = 0; repairGbc.gridy = 0;
                JLabel lblClientName = new JLabel("Client Name:");
                lblClientName.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblClientName, repairGbc);
                repairGbc.gridx = 1;
                JTextField clientNameField = new JTextField(20);
                clientNameField.setBackground(Theme.INPUT_BG);
                clientNameField.setForeground(Theme.INPUT_TEXT);
                clientNameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(clientNameField, repairGbc);
                
                // Device IMEI
                repairGbc.gridx = 0; repairGbc.gridy = 1;
                JLabel lblImei = new JLabel("Device IMEI:");
                lblImei.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblImei, repairGbc);
                repairGbc.gridx = 1;
                JTextField imeiField = new JTextField(20);
                imeiField.setBackground(Theme.INPUT_BG);
                imeiField.setForeground(Theme.INPUT_TEXT);
                imeiField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(imeiField, repairGbc);
                
                // Device Type
                repairGbc.gridx = 0; repairGbc.gridy = 2;
                JLabel lblType = new JLabel("Device Type:");
                lblType.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblType, repairGbc);
                repairGbc.gridx = 1;
                JTextField typeField = new JTextField(20);
                typeField.setBackground(Theme.INPUT_BG);
                typeField.setForeground(Theme.INPUT_TEXT);
                typeField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(typeField, repairGbc);
                
                // Brand
                repairGbc.gridx = 0; repairGbc.gridy = 3;
                JLabel lblBrand = new JLabel("Brand:");
                lblBrand.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblBrand, repairGbc);
                repairGbc.gridx = 1;
                JTextField brandField = new JTextField(20);
                brandField.setBackground(Theme.INPUT_BG);
                brandField.setForeground(Theme.INPUT_TEXT);
                brandField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(brandField, repairGbc);
                
                // Model
                repairGbc.gridx = 0; repairGbc.gridy = 4;
                JLabel lblModel = new JLabel("Model:");
                lblModel.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblModel, repairGbc);
                repairGbc.gridx = 1;
                JTextField modelField = new JTextField(20);
                modelField.setBackground(Theme.INPUT_BG);
                modelField.setForeground(Theme.INPUT_TEXT);
                modelField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(modelField, repairGbc);
                
                // Description
                repairGbc.gridx = 0; repairGbc.gridy = 5;
                JLabel lblDesc = new JLabel("Description:");
                lblDesc.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblDesc, repairGbc);
                repairGbc.gridx = 1;
                JTextArea descArea = new JTextArea(3, 20);
                descArea.setBackground(Theme.INPUT_BG);
                descArea.setForeground(Theme.INPUT_TEXT);
                descArea.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                descArea.setLineWrap(true);
                descArea.setWrapStyleWord(true);
                addPanel.add(new JScrollPane(descArea), repairGbc);
                
                // Cost
                repairGbc.gridx = 0; repairGbc.gridy = 6;
                JLabel lblCost = new JLabel("Repair Cost:");
                lblCost.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblCost, repairGbc);
                repairGbc.gridx = 1;
                JTextField costField = new JTextField(20);
                costField.setBackground(Theme.INPUT_BG);
                costField.setForeground(Theme.INPUT_TEXT);
                costField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(costField, repairGbc);
                
                // Add a filler to push buttons to bottom
                repairGbc.gridx = 0; repairGbc.gridy = 7; repairGbc.gridwidth = 2;
                repairGbc.weightx = 1.0; repairGbc.weighty = 1.0;
                repairGbc.fill = GridBagConstraints.BOTH;
                addPanel.add(Box.createVerticalGlue(), repairGbc);
                
                // Buttons - fixed at bottom
                repairGbc.gridx = 0; repairGbc.gridy = 8; repairGbc.gridwidth = 2;
                repairGbc.weightx = 0; repairGbc.weighty = 0;
                repairGbc.fill = GridBagConstraints.NONE;
                repairGbc.anchor = GridBagConstraints.CENTER;
                JPanel repairBtnPanel = new JPanel();
                repairBtnPanel.setOpaque(false);
                JButton submitBtn = new JButton("Add Request");
                submitBtn.setBackground(Theme.ACCENT);
                submitBtn.setForeground(Theme.BUTTON_TEXT);
                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setBackground(Theme.ACCENT_DARK);
                cancelBtn.setForeground(Theme.BUTTON_TEXT);
                repairBtnPanel.add(submitBtn);
                repairBtnPanel.add(cancelBtn);
                addPanel.add(repairBtnPanel, repairGbc);
                
                addDialog.getContentPane().setLayout(new BorderLayout());
                addDialog.getContentPane().add(addPanel, BorderLayout.CENTER);
                addDialog.setResizable(false);
                
                submitBtn.addActionListener(ev -> {
                    String clientName = clientNameField.getText().trim();
                    String imei = imeiField.getText().trim();
                    String type = typeField.getText().trim();
                    String brand = brandField.getText().trim();
                    String model = modelField.getText().trim();
                    String desc = descArea.getText().trim();
                    String costInput = costField.getText().trim();
                    
                    if(clientName.isBlank() || imei.isBlank() || type.isBlank() || brand.isBlank() || model.isBlank() || desc.isBlank() || costInput.isBlank()) {
                        JOptionPane.showMessageDialog(addDialog, "All fields are required.");
                        return;
                    }
                    try {
                        double cost = Double.parseDouble(costInput);
                        var device = new metier.models.Device(imei, type, brand, model);
                        String code = java.util.UUID.randomUUID().toString().substring(0,8);
                        var repair = new metier.models.Repair(code, user, cost, device, desc, clientName);
                        service.getRepairService().addRepair(repair);
                        JOptionPane.showMessageDialog(addDialog, "Request added! Code: " + code);
                        addDialog.dispose();
                    } catch (NumberFormatException nfx) {
                        JOptionPane.showMessageDialog(addDialog, "Cost must be a valid number.");
                    }
                });
                
                cancelBtn.addActionListener(ev -> addDialog.dispose());
                
                addDialog.setVisible(true);
            });
            btns[4].addActionListener(e -> {
                java.util.List<metier.models.Repair> repairs = service.getRepairsByRepairer(user);
                JTextArea ta = new JTextArea(20,50); ta.setEditable(false);
                if(repairs.isEmpty()) ta.setText("No repair requests yet.");
                else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("--- Repair Requests by You ---\n");
                    for(var r : repairs) sb.append("Code: ").append(r.getCode())
                        .append(" | Device: ").append(r.getDevice().getBrand()).append(" ").append(r.getDevice().getModel())
                        .append(" | Client: ").append(r.getClientName())
                        .append(" | Status: ").append(r.getStatus())
                        .append(" | Cost: ").append(r.getTotalCost())
                        .append(" | Desc: ").append(r.getDescription()).append("\n");
                    ta.setText(sb.toString());
                }
                JOptionPane.showMessageDialog(null, new JScrollPane(ta), "My Repairs", JOptionPane.PLAIN_MESSAGE);
            });
            btns[5].addActionListener(e -> showUpdateStatusDialog());
        } else if (user != null && user.getRole() == User.Role.OWNER) {
            // Owner only (not a repairer) - show only owner actions
            String[][] actionDefs = {
                {"View Shop", "See your shop's details and repairers list."},
                {"Add Repairer", "Add a new repairer to your shop."},
                {"Manage Repairers", "View/update/remove your shop's repairers."}
            };
            AnimatableButton[] btns = new AnimatableButton[actionDefs.length];
            JLabel[] hintLbls = new JLabel[actionDefs.length];
            for(int i=0; i<actionDefs.length; ++i) {
                btns[i] = new AnimatableButton(actionDefs[i][0]);
                hintLbls[i] = new JLabel(actionDefs[i][1]);
                hintLbls[i].setFont(new Font("Segoe UI", Font.ITALIC, 14));
                hintLbls[i].setForeground(Theme.TEXT_MUTED);
                btns[i].setHorizontalAlignment(SwingConstants.LEFT);
                btns[i].setAlignmentX(Component.LEFT_ALIGNMENT);
                gbc.gridx = 0; actionsPanel.add(btns[i], gbc);
                gbc.gridx = 1; actionsPanel.add(hintLbls[i], gbc);
                gbc.gridy++;
            }
            btns[0].addActionListener(e -> {
                JTextArea ta = new JTextArea(20,50); ta.setEditable(false);
                StringBuilder sb = new StringBuilder();
                var shop = service.getShopByOwner(user);
                if(shop == null) sb.append("No shop found.");
                else {
                    sb.append("Shop Info\n");
                    sb.append("Shop Name: ").append(shop.getName()).append("\n");
                    sb.append("Owner: ").append(user.getUsername()).append("\n\n");
                    
                    // Calculate sum of all repairers' net (completed - loans)
                    double totalNet = 0.0;
                    
                    // Add all repairers' net
                    for(var r : shop.getRepairers()) {
                        double completed = service.getCompletedTotalForUser(r);
                        double loans = service.getActiveLoanTotalForUser(r);
                        totalNet += (completed - loans);
                    }
                    
                    sb.append("Total Cash Available: ").append(String.format("%.2f", totalNet)).append("\n\n");
                    sb.append("Repairers:\n");
                    for(var r : shop.getRepairers()) {
                        double completed = service.getCompletedTotalForUser(r);
                        double loans = service.getActiveLoanTotalForUser(r);
                        double net = completed - loans;
                        sb.append(" - ").append(r.getUsername()).append(" - Net: ").append(String.format("%.2f", net)).append("\n");
                    }
                }
                ta.setText(sb.toString());
                JOptionPane.showMessageDialog(null, new JScrollPane(ta), "Shop Info", JOptionPane.PLAIN_MESSAGE);
            });
            btns[1].addActionListener(e -> {
                // Create a single-page add repairer dialog
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                JDialog addDialog = new JDialog(topFrame, "Add Repairer", true);
                addDialog.setSize(400, 300);
                addDialog.setLocationRelativeTo(this);
                
                JPanel addPanel = new JPanel(new GridBagLayout());
                addPanel.setBackground(Theme.DASHBOARD_BG1);
                GridBagConstraints repairerGbc = new GridBagConstraints();
                repairerGbc.insets = new Insets(5, 10, 5, 10);
                repairerGbc.anchor = GridBagConstraints.WEST;
                
                // Username
                repairerGbc.gridx = 0; repairerGbc.gridy = 0;
                JLabel lblUsername = new JLabel("Username:");
                lblUsername.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblUsername, repairerGbc);
                repairerGbc.gridx = 1;
                JTextField repairerUsernameField = new JTextField(20);
                repairerUsernameField.setBackground(Theme.INPUT_BG);
                repairerUsernameField.setForeground(Theme.INPUT_TEXT);
                repairerUsernameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(repairerUsernameField, repairerGbc);
                
                // Password
                repairerGbc.gridx = 0; repairerGbc.gridy = 1;
                JLabel lblPassword = new JLabel("Password:");
                lblPassword.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblPassword, repairerGbc);
                repairerGbc.gridx = 1;
                JPasswordField repairerPasswordField = new JPasswordField(20);
                repairerPasswordField.setBackground(Theme.INPUT_BG);
                repairerPasswordField.setForeground(Theme.INPUT_TEXT);
                repairerPasswordField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(repairerPasswordField, repairerGbc);
                
                // Email
                repairerGbc.gridx = 0; repairerGbc.gridy = 2;
                JLabel lblEmail = new JLabel("Email:");
                lblEmail.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblEmail, repairerGbc);
                repairerGbc.gridx = 1;
                JTextField repairerEmailField = new JTextField(20);
                repairerEmailField.setBackground(Theme.INPUT_BG);
                repairerEmailField.setForeground(Theme.INPUT_TEXT);
                repairerEmailField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(repairerEmailField, repairerGbc);
                
                // Phone
                repairerGbc.gridx = 0; repairerGbc.gridy = 3;
                JLabel lblPhone = new JLabel("Phone:");
                lblPhone.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblPhone, repairerGbc);
                repairerGbc.gridx = 1;
                JTextField repairerPhoneField = new JTextField(20);
                repairerPhoneField.setBackground(Theme.INPUT_BG);
                repairerPhoneField.setForeground(Theme.INPUT_TEXT);
                repairerPhoneField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(repairerPhoneField, repairerGbc);
                
                // Buttons
                repairerGbc.gridx = 0; repairerGbc.gridy = 4; repairerGbc.gridwidth = 2;
                repairerGbc.anchor = GridBagConstraints.CENTER;
                JPanel btnPanel = new JPanel();
                btnPanel.setOpaque(false);
                JButton submitBtn = new JButton("Add Repairer");
                submitBtn.setBackground(Theme.ACCENT);
                submitBtn.setForeground(Theme.BUTTON_TEXT);
                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setBackground(Theme.ACCENT_DARK);
                cancelBtn.setForeground(Theme.BUTTON_TEXT);
                btnPanel.add(submitBtn);
                btnPanel.add(cancelBtn);
                addPanel.add(btnPanel, repairerGbc);
                
                addDialog.getContentPane().add(addPanel);
                
                submitBtn.addActionListener(ev -> {
                    String username = repairerUsernameField.getText().trim();
                    String password = new String(repairerPasswordField.getPassword());
                    String email = repairerEmailField.getText().trim();
                    String phone = repairerPhoneField.getText().trim();
                    
                    if(username.isBlank() || password.isBlank()) {
                        JOptionPane.showMessageDialog(addDialog, "Username and Password required.");
                        return;
                    }
                    try {
                        service.addRepairer(username, password, email, phone, service.getShopByOwner(user));
                        JOptionPane.showMessageDialog(addDialog, "Repairer added.");
                        addDialog.dispose();
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(addDialog, ex.getMessage());
                    }
                });
                
                cancelBtn.addActionListener(ev -> addDialog.dispose());
                
                addDialog.setVisible(true);
            });
            btns[2].addActionListener(e -> {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (topFrame instanceof MainFrame) {
                    ((MainFrame)topFrame).setMainPanel(new ManageRepairersPanel(user, service));
                }
            });
        } else {
            String[][] actionDefs = {
                {"Add Repair Request", "Add a new device repair request for a client."},
                {"Show My Repairs", "See all repair requests you manage."},
                {"Update Status", "Change the status of a repair request."}
            };
            AnimatableButton[] btns = new AnimatableButton[actionDefs.length];
            JLabel[] hintLbls = new JLabel[actionDefs.length];
            for(int i=0; i<actionDefs.length; ++i) {
                btns[i] = new AnimatableButton(actionDefs[i][0]);
                hintLbls[i] = new JLabel(actionDefs[i][1]);
                hintLbls[i].setFont(new Font("Segoe UI", Font.ITALIC, 14));
                hintLbls[i].setForeground(Theme.TEXT_MUTED);
                btns[i].setHorizontalAlignment(SwingConstants.LEFT);
                btns[i].setAlignmentX(Component.LEFT_ALIGNMENT);
                gbc.gridx = 0; actionsPanel.add(btns[i], gbc);
                gbc.gridx = 1; actionsPanel.add(hintLbls[i], gbc);
                gbc.gridy++;
            }
            btns[0].addActionListener(e -> {
                // Check if repairer has a shop assigned
                if (user.getShop() == null) {
                    JOptionPane.showMessageDialog(this, "You must be assigned to a shop to add repairs!");
                    return;
                }
                
                // Create a single-page add repair request dialog
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                JDialog addDialog = new JDialog(topFrame, "Add Repair Request", true);
                addDialog.setSize(400, 500);
                addDialog.setLocationRelativeTo(this);
                
                JPanel addPanel = new JPanel(new GridBagLayout());
                addPanel.setBackground(Theme.DASHBOARD_BG1);
                GridBagConstraints repairerRepairGbc = new GridBagConstraints();
                repairerRepairGbc.insets = new Insets(5, 10, 5, 10);
                repairerRepairGbc.anchor = GridBagConstraints.WEST;
                
                // Client Name
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 0;
                JLabel lblClientName = new JLabel("Client Name:");
                lblClientName.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblClientName, repairerRepairGbc);
                repairerRepairGbc.gridx = 1;
                JTextField clientNameField = new JTextField(20);
                clientNameField.setBackground(Theme.INPUT_BG);
                clientNameField.setForeground(Theme.INPUT_TEXT);
                clientNameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(clientNameField, repairerRepairGbc);
                
                // Device IMEI
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 1;
                JLabel lblImei = new JLabel("Device IMEI:");
                lblImei.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblImei, repairerRepairGbc);
                repairerRepairGbc.gridx = 1;
                JTextField imeiField = new JTextField(20);
                imeiField.setBackground(Theme.INPUT_BG);
                imeiField.setForeground(Theme.INPUT_TEXT);
                imeiField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(imeiField, repairerRepairGbc);
                
                // Device Type
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 2;
                JLabel lblType = new JLabel("Device Type:");
                lblType.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblType, repairerRepairGbc);
                repairerRepairGbc.gridx = 1;
                JTextField typeField = new JTextField(20);
                typeField.setBackground(Theme.INPUT_BG);
                typeField.setForeground(Theme.INPUT_TEXT);
                typeField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(typeField, repairerRepairGbc);
                
                // Brand
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 3;
                JLabel lblBrand = new JLabel("Brand:");
                lblBrand.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblBrand, repairerRepairGbc);
                repairerRepairGbc.gridx = 1;
                JTextField brandField = new JTextField(20);
                brandField.setBackground(Theme.INPUT_BG);
                brandField.setForeground(Theme.INPUT_TEXT);
                brandField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(brandField, repairerRepairGbc);
                
                // Model
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 4;
                JLabel lblModel = new JLabel("Model:");
                lblModel.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblModel, repairerRepairGbc);
                repairerRepairGbc.gridx = 1;
                JTextField modelField = new JTextField(20);
                modelField.setBackground(Theme.INPUT_BG);
                modelField.setForeground(Theme.INPUT_TEXT);
                modelField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(modelField, repairerRepairGbc);
                
                // Description
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 5;
                JLabel lblDesc = new JLabel("Description:");
                lblDesc.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblDesc, repairerRepairGbc);
                repairerRepairGbc.gridx = 1;
                JTextArea descArea = new JTextArea(3, 20);
                descArea.setBackground(Theme.INPUT_BG);
                descArea.setForeground(Theme.INPUT_TEXT);
                descArea.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                descArea.setLineWrap(true);
                descArea.setWrapStyleWord(true);
                addPanel.add(new JScrollPane(descArea), repairerRepairGbc);
                
                // Cost
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 6;
                JLabel lblCost = new JLabel("Repair Cost:");
                lblCost.setForeground(Theme.TEXT_PRIMARY);
                addPanel.add(lblCost, repairerRepairGbc);
                repairerRepairGbc.gridx = 1;
                JTextField costField = new JTextField(20);
                costField.setBackground(Theme.INPUT_BG);
                costField.setForeground(Theme.INPUT_TEXT);
                costField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
                addPanel.add(costField, repairerRepairGbc);
                
                // Add a filler to push buttons to bottom
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 7; repairerRepairGbc.gridwidth = 2;
                repairerRepairGbc.weightx = 1.0; repairerRepairGbc.weighty = 1.0;
                repairerRepairGbc.fill = GridBagConstraints.BOTH;
                addPanel.add(Box.createVerticalGlue(), repairerRepairGbc);
                
                // Buttons - fixed at bottom
                repairerRepairGbc.gridx = 0; repairerRepairGbc.gridy = 8; repairerRepairGbc.gridwidth = 2;
                repairerRepairGbc.weightx = 0; repairerRepairGbc.weighty = 0;
                repairerRepairGbc.fill = GridBagConstraints.NONE;
                repairerRepairGbc.anchor = GridBagConstraints.CENTER;
                JPanel btnPanel = new JPanel();
                btnPanel.setOpaque(false);
                JButton submitBtn = new JButton("Add Request");
                submitBtn.setBackground(Theme.ACCENT);
                submitBtn.setForeground(Theme.BUTTON_TEXT);
                JButton cancelBtn = new JButton("Cancel");
                cancelBtn.setBackground(Theme.ACCENT_DARK);
                cancelBtn.setForeground(Theme.BUTTON_TEXT);
                btnPanel.add(submitBtn);
                btnPanel.add(cancelBtn);
                addPanel.add(btnPanel, repairerRepairGbc);
                
                addDialog.getContentPane().setLayout(new BorderLayout());
                addDialog.getContentPane().add(addPanel, BorderLayout.CENTER);
                addDialog.setResizable(false);
                
                submitBtn.addActionListener(ev -> {
                    String clientName = clientNameField.getText().trim();
                    String imei = imeiField.getText().trim();
                    String type = typeField.getText().trim();
                    String brand = brandField.getText().trim();
                    String model = modelField.getText().trim();
                    String desc = descArea.getText().trim();
                    String costInput = costField.getText().trim();
                    
                    if(clientName.isBlank() || imei.isBlank() || type.isBlank() || brand.isBlank() || model.isBlank() || desc.isBlank() || costInput.isBlank()) {
                        JOptionPane.showMessageDialog(addDialog, "All fields are required.");
                        return;
                    }
                    try {
                        double cost = Double.parseDouble(costInput);
                        var device = new metier.models.Device(imei, type, brand, model);
                        String code = java.util.UUID.randomUUID().toString().substring(0,8);
                        var repair = new metier.models.Repair(code, user, cost, device, desc, clientName);
                        service.getRepairService().addRepair(repair);
                        JOptionPane.showMessageDialog(addDialog, "Request added! Code: " + code);
                        addDialog.dispose();
                    } catch (NumberFormatException nfx) {
                        JOptionPane.showMessageDialog(addDialog, "Cost must be a valid number.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(addDialog, "Error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                
                cancelBtn.addActionListener(ev -> addDialog.dispose());
                
                addDialog.setVisible(true);
            });
            btns[1].addActionListener(e -> {
                java.util.List<metier.models.Repair> repairs = service.getRepairsByRepairer(user);
                JTextArea ta = new JTextArea(20,50); ta.setEditable(false);
                if(repairs.isEmpty()) ta.setText("No repair requests yet.");
                else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("--- Repair Requests by You ---\n");
                    for(var r : repairs) sb.append("Code: ").append(r.getCode())
                        .append(" | Device: ").append(r.getDevice().getBrand()).append(" ").append(r.getDevice().getModel())
                        .append(" | Client: ").append(r.getClientName())
                        .append(" | Status: ").append(r.getStatus())
                        .append(" | Cost: ").append(r.getTotalCost())
                        .append(" | Desc: ").append(r.getDescription()).append("\n");
                    ta.setText(sb.toString());
                }
                JOptionPane.showMessageDialog(null, new JScrollPane(ta), "My Repairs", JOptionPane.PLAIN_MESSAGE);
            });
            btns[2].addActionListener(e -> showUpdateStatusDialog());
        }
        actionsPanel.setPreferredSize(new Dimension(470, 380));
        add(actionsPanel, BorderLayout.CENTER);
    }
    
    private void showUpdateStatusDialog() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog statusDialog = new JDialog(topFrame, "Update Repair Status", true);
        statusDialog.setSize(800, 600);
        statusDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Theme.DASHBOARD_BG1);
        
        // Filter panel at top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Theme.DASHBOARD_BG1);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
        
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setForeground(Theme.TEXT_PRIMARY);
        JRadioButton filterByName = new JRadioButton("Client Name", true);
        filterByName.setForeground(Theme.TEXT_PRIMARY);
        filterByName.setBackground(Theme.DASHBOARD_BG1);
        JRadioButton filterByCode = new JRadioButton("Code");
        filterByCode.setForeground(Theme.TEXT_PRIMARY);
        filterByCode.setBackground(Theme.DASHBOARD_BG1);
        ButtonGroup filterGroup = new ButtonGroup();
        filterGroup.add(filterByName);
        filterGroup.add(filterByCode);
        
        JTextField filterField = new JTextField(20);
        filterField.setBackground(Theme.INPUT_BG);
        filterField.setForeground(Theme.INPUT_TEXT);
        filterField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterByName);
        filterPanel.add(filterByCode);
        filterPanel.add(filterField);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // List panel for repairs
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(20, 28, 40));
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(new Color(20, 28, 40));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Get all repairs for the user
        java.util.List<metier.models.Repair> allRepairs = service.getRepairsByRepairer(user);
        java.util.List<metier.models.Repair> filteredRepairs = new java.util.ArrayList<>(allRepairs);
        
        // Function to refresh the list
        java.util.function.Consumer<java.util.List<metier.models.Repair>> refreshList = repairs -> {
            listPanel.removeAll();
            if (repairs.isEmpty()) {
                JLabel none = new JLabel("No repair requests found.");
                none.setForeground(Theme.TEXT_MUTED);
                none.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                listPanel.add(none);
            } else {
                for (metier.models.Repair repair : repairs) {
                    JPanel repairRow = new JPanel(new BorderLayout(10, 0));
                    repairRow.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(5, 10, 5, 10),
                            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER)));
                    repairRow.setBackground(new Color(25, 35, 50));
                    repairRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                    repairRow.setPreferredSize(new Dimension(repairRow.getPreferredSize().width, 50));
                    
                    // Left side: Code, Client Name, Device, Cost
                    JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
                    infoPanel.setOpaque(false);
                    
                    JLabel codeLabel = new JLabel("Code: " + repair.getCode());
                    codeLabel.setForeground(Theme.TEXT_PRIMARY);
                    codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    codeLabel.setPreferredSize(new Dimension(100, 30));
                    
                    JLabel nameLabel = new JLabel("Client: " + repair.getClientName());
                    nameLabel.setForeground(Theme.TEXT_PRIMARY);
                    nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    nameLabel.setPreferredSize(new Dimension(150, 30));
                    
                    String deviceInfo = repair.getDevice().getBrand() + " " + repair.getDevice().getModel();
                    JLabel deviceLabel = new JLabel("Device: " + deviceInfo);
                    deviceLabel.setForeground(Theme.TEXT_MUTED);
                    deviceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    deviceLabel.setPreferredSize(new Dimension(150, 30));
                    
                    JLabel costLabel = new JLabel("Cost: " + String.format("%.2f", repair.getTotalCost()));
                    costLabel.setForeground(Theme.ACCENT);
                    costLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    costLabel.setPreferredSize(new Dimension(80, 30));
                    
                    infoPanel.add(codeLabel);
                    infoPanel.add(nameLabel);
                    infoPanel.add(deviceLabel);
                    infoPanel.add(costLabel);
                    
                    repairRow.add(infoPanel, BorderLayout.CENTER);
                    
                    // Right side: Status dropdown
                    JComboBox<metier.models.Repair.Status> statusCombo = new JComboBox<>(metier.models.Repair.Status.values());
                    statusCombo.setSelectedItem(repair.getStatus());
                    statusCombo.setBackground(Theme.INPUT_BG);
                    statusCombo.setForeground(Theme.INPUT_TEXT);
                    statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    statusCombo.setPreferredSize(new Dimension(150, 30));
                    
                    statusCombo.addActionListener(ev -> {
                        metier.models.Repair.Status newStatus = (metier.models.Repair.Status) statusCombo.getSelectedItem();
                        if (newStatus != repair.getStatus()) {
                            repair.setStatus(newStatus);
                            JOptionPane.showMessageDialog(statusDialog, "Status updated for " + repair.getCode() + "!");
                        }
                    });
                    
                    repairRow.add(statusCombo, BorderLayout.EAST);
                    
                    listPanel.add(repairRow);
                }
            }
            listPanel.revalidate();
            listPanel.repaint();
        };
        
        // Filter action
        java.util.function.Consumer<String> applyFilter = filterText -> {
            filteredRepairs.clear();
            if (filterText.trim().isEmpty()) {
                filteredRepairs.addAll(allRepairs);
            } else {
                String lowerFilter = filterText.toLowerCase();
                for (metier.models.Repair repair : allRepairs) {
                    if (filterByName.isSelected()) {
                        if (repair.getClientName().toLowerCase().contains(lowerFilter)) {
                            filteredRepairs.add(repair);
                        }
                    } else {
                        if (repair.getCode().toLowerCase().contains(lowerFilter)) {
                            filteredRepairs.add(repair);
                        }
                    }
                }
            }
            refreshList.accept(filteredRepairs);
        };
        
        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.accept(filterField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.accept(filterField.getText()); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.accept(filterField.getText()); }
        });
        
        filterByName.addActionListener(e -> applyFilter.accept(filterField.getText()));
        filterByCode.addActionListener(e -> applyFilter.accept(filterField.getText()));
        
        // Initial load
        refreshList.accept(filteredRepairs);
        
        // Close button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Theme.ACCENT_DARK);
        closeBtn.setForeground(Theme.BUTTON_TEXT);
        closeBtn.addActionListener(e -> statusDialog.dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        statusDialog.getContentPane().add(mainPanel);
        statusDialog.setVisible(true);
    }
    
    public DashboardPanel() { this(null, null); }
}
