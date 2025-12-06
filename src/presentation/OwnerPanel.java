package presentation;

import metier.models.*;
import metier.services.BusinessService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OwnerPanel extends JPanel {

    private final User owner;
    private final BusinessService service;

    private JTextArea taInfo;

    public OwnerPanel(User owner, BusinessService service) {
        this.owner = owner;
        this.service = service;
        setLayout(new BorderLayout());
        // Only the results area remains
        taInfo = new JTextArea(20, 50);
        taInfo.setEditable(false);
        add(new JScrollPane(taInfo), BorderLayout.CENTER);
        // By default show requests
        showRepairRequests();
    }

    private void viewShop() {
        Shop shop = service.getShopByOwner(owner);
        if (shop == null) {
            taInfo.setText("No shop found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Shop Info\n");
        sb.append("Username: ").append(owner.getUsername()).append("\n");
        // Calculate sum of all repairers' net (completed - loans)
        double totalNet = 0.0;
        for (User r : shop.getRepairers()) {
            double completed = service.getCompletedTotalForUser(r);
            double loans = service.getActiveLoanTotalForUser(r);
            totalNet += (completed - loans);
        }
        sb.append("Cash: ").append(totalNet).append("\n");
        sb.append("Repairers:\n");
        for (User r : shop.getRepairers()) {
            sb.append(" - ").append(r.getUsername()).append("\n");
        }
        taInfo.setText(sb.toString());
    }

    private String promptUser(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message);
    }

    private void addRepairer() {
        Shop shop = service.getShopByOwner(owner);
        if (shop == null) {
            JOptionPane.showMessageDialog(this, "You must have a shop first!");
            return;
        }
        try {
            String username = promptUser(this, "Repairer Username:");
            String password = promptUser(this, "Password:");
            String email = promptUser(this, "Email:");
            String phone = promptUser(this, "Phone:");
            if(username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password are required.");
                return;
            }
            service.addRepairer(username, password, email, phone, shop);
            JOptionPane.showMessageDialog(this, "Repairer added successfully!");
            viewShop();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void addRepairRequest() {
        Shop shop = service.getShopByOwner(owner);
        if (shop == null) {
            JOptionPane.showMessageDialog(this, "You must have a shop to add repairs!");
            return;
        }
        if (!(owner.getRole() == User.Role.BOTH || owner.getRole() == User.Role.REPAIRER)) {
            JOptionPane.showMessageDialog(this, "You cannot add repairs!");
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
            Repair repair = new Repair(code, owner, cost, device, desc, clientName);
            service.getRepairService().addRepair(repair);
            JOptionPane.showMessageDialog(this, "Repair added! Code: " + code);
        } catch (NumberFormatException nfex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for cost.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showRepairRequests() {
        List<Repair> repairs = service.getRepairsByRepairer(owner);
        if (repairs.isEmpty()) {
            taInfo.setText("No repair requests yet.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Repair Requests by You ---\n");
        for (Repair r : repairs) {
            sb.append("Code: ").append(r.getCode())
              .append(" | Device: ").append(r.getDevice().getBrand())
              .append(" ").append(r.getDevice().getModel())
              .append(" | Status: ").append(r.getStatus())
              .append(" | Cost: ").append(r.getTotalCost())
              .append(" | Description: ").append(r.getDescription())
              .append("\n");
        }
        taInfo.setText(sb.toString());
    }

    // ---------------- Update repair status (same as RepairerPanel) ----------------
    private void updateRepairStatus() {
        List<Repair> repairs = service.getRepairsByRepairer(owner);
        if (repairs.isEmpty()) {
            taInfo.setText("No repair requests to update.");
            return;
        }

        String code = JOptionPane.showInputDialog(this, "Enter the code of the repair to update:");
        if (code == null || code.isBlank()) return;

        Repair target = null;
        for (Repair r : repairs) {
            if (r.getCode().equals(code)) {
                target = r;
                break;
            }
        }

        if (target == null) {
            JOptionPane.showMessageDialog(this, "Repair not found.");
            return;
        }

        String status = JOptionPane.showInputDialog(this, "Enter new status (PENDING, IN_PROGRESS, COMPLETED):");
        if (status == null || status.isBlank()) return;

        try {
            target.setStatus(Repair.Status.valueOf(status.toUpperCase()));
            JOptionPane.showMessageDialog(this, "Status updated!");
            showRepairRequests(); // refresh display
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid status! Use PENDING, IN_PROGRESS, or COMPLETED.");
        }
    }
}
