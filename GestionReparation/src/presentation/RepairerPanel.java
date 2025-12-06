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
        try {
            String imei = promptUser(this, "Device IMEI:");
            String type = promptUser(this, "Device Type:");
            String brand = promptUser(this, "Brand:");
            String model = promptUser(this, "Model:");
            String desc = promptUser(this, "Repair Description:");
            String costInput = promptUser(this, "Repair Cost:");
            String clientName = promptUser(this, "Client Name:");
            if(imei==null || type==null || brand==null || model==null || desc==null || costInput==null || clientName==null ||
               imei.trim().isEmpty() || type.trim().isEmpty() || brand.trim().isEmpty() || model.trim().isEmpty() || desc.trim().isEmpty() || costInput.trim().isEmpty() || clientName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }
            double cost = Double.parseDouble(costInput);
            Device device = new Device(imei, type, brand, model);
            String code = java.util.UUID.randomUUID().toString().substring(0, 8);
            Repair repair = new Repair(code, user, cost, device, desc, clientName);
            service.getRepairService().addRepair(repair);
            JOptionPane.showMessageDialog(this, "Repair added! Code: " + code);
            viewAssignedRepairs();
        } catch (NumberFormatException nfex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for cost.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
