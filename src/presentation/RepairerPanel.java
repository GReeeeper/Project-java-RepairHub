package presentation;

import metier.models.*;
import metier.services.BusinessService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RepairerPanel extends JPanel {
    private final User user;
    private final BusinessService service;

    private JTextArea taRepairs;
    private JButton btnViewAssigned, btnUpdateStatus, btnAddRepairRequest;

    public RepairerPanel(User user, BusinessService service) {
        this.user = user;
        this.service = service;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("User Info"));

        btnViewAssigned = new JButton("View Assigned Repairs");
        btnUpdateStatus = new JButton("Update Repair Status");
        btnAddRepairRequest = new JButton("Add Repair Request");

        topPanel.add(btnViewAssigned);
        topPanel.add(btnUpdateStatus);
        topPanel.add(btnAddRepairRequest);

        add(topPanel, BorderLayout.NORTH);

        taRepairs = new JTextArea(20, 50);
        taRepairs.setEditable(false);
        add(new JScrollPane(taRepairs), BorderLayout.CENTER);

        btnViewAssigned.addActionListener(e -> viewAssignedRepairs());
        btnUpdateStatus.addActionListener(e -> updateRepairStatus());
        btnAddRepairRequest.addActionListener(e -> addRepairRequest());
    }

    private void viewAssignedRepairs() {
        List<Repair> repairs = service.getRepairsByRepairer(user);
        if (repairs.isEmpty()) {
            taRepairs.setText("No assigned repairs.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Repair r : repairs) {
                sb.append("Code: ").append(r.getCode())
                  .append(" | Device: ").append(r.getDevice().getBrand())
                  .append(" ").append(r.getDevice().getModel())
                  .append(" | Status: ").append(r.getStatus())
                  .append(" | Cost: ").append(r.getTotalCost())
                  .append(" | Description: ").append(r.getDescription())
                  .append("\n");
            }
            taRepairs.setText(sb.toString());
        }
    }

    private void updateRepairStatus() {
        String code = JOptionPane.showInputDialog(this, "Enter repair code:");
        if (code == null || code.isEmpty()) return;

        Repair repair = service.getRepairByCode(code);
        if (repair == null) {
            JOptionPane.showMessageDialog(this, "Repair not found!");
            return;
        }

        String status = JOptionPane.showInputDialog(this, "Enter new status (PENDING, IN_PROGRESS, COMPLETED):");
        if (status == null || status.isEmpty()) return;

        try {
            repair.setStatus(Repair.Status.valueOf(status.toUpperCase()));
            JOptionPane.showMessageDialog(this, "Status updated!");
            viewAssignedRepairs();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid status!");
        }
    }

    private String promptUser(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message);
    }

    private void addRepairRequest() {
        Shop shop = user.getShop();
        if (shop == null) {
            JOptionPane.showMessageDialog(this, "You must be assigned to a shop to add repairs!");
            return;
        }
        if (!(user.getRole() == User.Role.REPAIRER || user.getRole() == User.Role.BOTH)) {
            JOptionPane.showMessageDialog(this, "You are not allowed to add repairs!");
            return;
        }
        
        // Create a single-page add repair request dialog
        JFrame topFrame = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
        JDialog addDialog = new JDialog(topFrame, "Add Repair Request", true);
        addDialog.setSize(400, 500);
        addDialog.setLocationRelativeTo(this);
        
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBackground(presentation.Theme.DASHBOARD_BG1);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Client Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblClientName = new JLabel("Client Name:");
        lblClientName.setForeground(presentation.Theme.TEXT_PRIMARY);
        addPanel.add(lblClientName, gbc);
        gbc.gridx = 1;
        JTextField clientNameField = new JTextField(20);
        clientNameField.setBackground(presentation.Theme.INPUT_BG);
        clientNameField.setForeground(presentation.Theme.INPUT_TEXT);
        clientNameField.setBorder(javax.swing.BorderFactory.createLineBorder(presentation.Theme.INPUT_BORDER));
        addPanel.add(clientNameField, gbc);
        
        // Device IMEI
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblImei = new JLabel("Device IMEI:");
        lblImei.setForeground(presentation.Theme.TEXT_PRIMARY);
        addPanel.add(lblImei, gbc);
        gbc.gridx = 1;
        JTextField imeiField = new JTextField(20);
        imeiField.setBackground(presentation.Theme.INPUT_BG);
        imeiField.setForeground(presentation.Theme.INPUT_TEXT);
        imeiField.setBorder(javax.swing.BorderFactory.createLineBorder(presentation.Theme.INPUT_BORDER));
        addPanel.add(imeiField, gbc);
        
        // Device Type
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblType = new JLabel("Device Type:");
        lblType.setForeground(presentation.Theme.TEXT_PRIMARY);
        addPanel.add(lblType, gbc);
        gbc.gridx = 1;
        JTextField typeField = new JTextField(20);
        typeField.setBackground(presentation.Theme.INPUT_BG);
        typeField.setForeground(presentation.Theme.INPUT_TEXT);
        typeField.setBorder(javax.swing.BorderFactory.createLineBorder(presentation.Theme.INPUT_BORDER));
        addPanel.add(typeField, gbc);
        
        // Brand
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblBrand = new JLabel("Brand:");
        lblBrand.setForeground(presentation.Theme.TEXT_PRIMARY);
        addPanel.add(lblBrand, gbc);
        gbc.gridx = 1;
        JTextField brandField = new JTextField(20);
        brandField.setBackground(presentation.Theme.INPUT_BG);
        brandField.setForeground(presentation.Theme.INPUT_TEXT);
        brandField.setBorder(javax.swing.BorderFactory.createLineBorder(presentation.Theme.INPUT_BORDER));
        addPanel.add(brandField, gbc);
        
        // Model
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblModel = new JLabel("Model:");
        lblModel.setForeground(presentation.Theme.TEXT_PRIMARY);
        addPanel.add(lblModel, gbc);
        gbc.gridx = 1;
        JTextField modelField = new JTextField(20);
        modelField.setBackground(presentation.Theme.INPUT_BG);
        modelField.setForeground(presentation.Theme.INPUT_TEXT);
        modelField.setBorder(javax.swing.BorderFactory.createLineBorder(presentation.Theme.INPUT_BORDER));
        addPanel.add(modelField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setForeground(presentation.Theme.TEXT_PRIMARY);
        addPanel.add(lblDesc, gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setBackground(presentation.Theme.INPUT_BG);
        descArea.setForeground(presentation.Theme.INPUT_TEXT);
        descArea.setBorder(javax.swing.BorderFactory.createLineBorder(presentation.Theme.INPUT_BORDER));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        addPanel.add(new JScrollPane(descArea), gbc);
        
        // Cost
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblCost = new JLabel("Repair Cost:");
        lblCost.setForeground(presentation.Theme.TEXT_PRIMARY);
        addPanel.add(lblCost, gbc);
        gbc.gridx = 1;
        JTextField costField = new JTextField(20);
        costField.setBackground(presentation.Theme.INPUT_BG);
        costField.setForeground(presentation.Theme.INPUT_TEXT);
        costField.setBorder(javax.swing.BorderFactory.createLineBorder(presentation.Theme.INPUT_BORDER));
        addPanel.add(costField, gbc);
        
        // Add a filler to push buttons to bottom
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        addPanel.add(Box.createVerticalGlue(), gbc);
        
        // Buttons - fixed at bottom
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton submitBtn = new JButton("Add Request");
        submitBtn.setBackground(presentation.Theme.ACCENT);
        submitBtn.setForeground(presentation.Theme.BUTTON_TEXT);
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(presentation.Theme.ACCENT_DARK);
        cancelBtn.setForeground(presentation.Theme.BUTTON_TEXT);
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);
        addPanel.add(btnPanel, gbc);
        
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
            
            if(clientName.isEmpty() || imei.isEmpty() || type.isEmpty() || brand.isEmpty() || model.isEmpty() || desc.isEmpty() || costInput.isEmpty()) {
                JOptionPane.showMessageDialog(addDialog, "All fields are required.");
                return;
            }
            try {
                double cost = Double.parseDouble(costInput);
                Device device = new Device(imei, type, brand, model);
                String code = java.util.UUID.randomUUID().toString().substring(0, 8);
                Repair repair = new Repair(code, user, cost, device, desc, clientName);
                service.getRepairService().addRepair(repair);
                JOptionPane.showMessageDialog(addDialog, "Repair added! Code: " + code);
                addDialog.dispose();
                viewAssignedRepairs();
            } catch (NumberFormatException nfex) {
                JOptionPane.showMessageDialog(addDialog, "Please enter a valid number for cost.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addDialog, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        cancelBtn.addActionListener(ev -> addDialog.dispose());
        
        addDialog.setVisible(true);
    }
}
