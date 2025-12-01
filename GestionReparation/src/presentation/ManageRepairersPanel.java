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
        listPanel.setOpaque(false);
        updateRepairersList();
        JScrollPane scroll = new JScrollPane(listPanel);
        add(scroll, BorderLayout.CENTER);
        btnAdd.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Repairer Username:");
            String password = JOptionPane.showInputDialog(this, "Password:");
            String email = JOptionPane.showInputDialog(this, "Email:");
            String phone = JOptionPane.showInputDialog(this, "Phone:");
            if(username == null || username.isBlank() || password == null || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Username and password required.");
                return;
            }
            try {
                service.addRepairer(username, password, email, phone, service.getShopByOwner(owner));
                updateRepairersList();
                JOptionPane.showMessageDialog(this, "Repairer added.");
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
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
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        BorderFactory.createLineBorder(Theme.CARD_BORDER, 1)));
                card.setBackground(Theme.CARD_BG);
                JTextArea info = new JTextArea();
                info.setEditable(false);
                info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                info.setBackground(Theme.CARD_BG);
                info.setForeground(Theme.TEXT_PRIMARY);
                info.setText("Username: " + r.getUsername() +
                        "\nRole: " + r.getRole() +
                        "\nEmail: " + (r.getEmail() != null ? r.getEmail() : "-") +
                        "\nPhone: " + (r.getPhone() != null ? r.getPhone() : "-"));
                card.add(info, BorderLayout.CENTER);
                JPanel actions = new JPanel();
                JButton btnUpdate = new JButton("Update");
                JButton btnDelete = new JButton("Delete");
                JButton btnShowRepairs = new JButton("Show Repairs");
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
                        service.getShopByOwner(owner).getRepairers().remove(r);
                        updateRepairersList();
                        JOptionPane.showMessageDialog(this, r.getUsername() + " removed from your shop.");
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
                listPanel.add(Box.createVerticalStrut(9));
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
