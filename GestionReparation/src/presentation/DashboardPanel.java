package presentation;

import metier.models.User;
import metier.models.Shop;
import metier.services.BusinessService;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel(User user, BusinessService service) {
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
        if (user != null && (user.getRole() == User.Role.OWNER || user.getRole() == User.Role.BOTH)) {
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
                    sb.append("Shop Info\nUsername: ").append(user.getUsername()).append("\n");
                    sb.append("Cash: ").append(shop.getRawCash()).append("\nRepairers:\n");
                    for(var r : shop.getRepairers()) sb.append(" - ").append(r.getUsername()).append("\n");
                }
                ta.setText(sb.toString());
                JOptionPane.showMessageDialog(null, new JScrollPane(ta), "Shop Info", JOptionPane.PLAIN_MESSAGE);
            });
            btns[1].addActionListener(e -> {
                String username = JOptionPane.showInputDialog(this, "Repairer Username:");
                String password = JOptionPane.showInputDialog(this, "Password:");
                String email = JOptionPane.showInputDialog(this, "Email:");
                String phone = JOptionPane.showInputDialog(this, "Phone:");
                if(username==null||username.isBlank()||password==null||password.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Username and Password required."); return;
                }
                try {
                    service.addRepairer(username, password, email, phone, service.getShopByOwner(user));
                    JOptionPane.showMessageDialog(this, "Repairer added.");
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            });
            btns[2].addActionListener(e -> {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (topFrame instanceof MainFrame) {
                    ((MainFrame)topFrame).setMainPanel(new ManageRepairersPanel(user, service));
                }
            });
            btns[3].addActionListener(e -> {
                String clientName = JOptionPane.showInputDialog(this, "Client Name:");
                String imei = JOptionPane.showInputDialog(this, "Device IMEI:");
                String type = JOptionPane.showInputDialog(this, "Device Type:");
                String brand = JOptionPane.showInputDialog(this, "Brand:");
                String model = JOptionPane.showInputDialog(this, "Model:");
                String desc = JOptionPane.showInputDialog(this, "Repair Description:");
                String costInput = JOptionPane.showInputDialog(this, "Repair Cost:");
                if(clientName==null||clientName.isBlank()||imei==null||type==null||brand==null||model==null||desc==null||costInput==null||clientName.isBlank()||imei.isBlank()||type.isBlank()||brand.isBlank()||model.isBlank()||desc.isBlank()||costInput.isBlank()) {
                    JOptionPane.showMessageDialog(this, "All fields are required."); return;
                }
                try {
                    double cost = Double.parseDouble(costInput);
                    var device = new metier.models.Device(imei, type, brand, model);
                    String code = java.util.UUID.randomUUID().toString().substring(0,8);
                    var repair = new metier.models.Repair(code, user, cost, device, desc, clientName);
                    service.getRepairService().addRepair(repair);
                    JOptionPane.showMessageDialog(this, "Request added! Code: " + code);
                } catch (NumberFormatException nfx) {
                    JOptionPane.showMessageDialog(this, "Cost must be a valid number.");
                }
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
            btns[5].addActionListener(e -> {
                String code = JOptionPane.showInputDialog(this, "Enter code:");
                if(code == null || code.isBlank()) return;
                var repair = service.getRepairByCode(code);
                if(repair == null) { JOptionPane.showMessageDialog(this, "Not found."); return; }
                String status = JOptionPane.showInputDialog(this, "Enter new status (PENDING, IN_PROGRESS, COMPLETED):");
                if(status==null||status.isBlank()) return;
                try {
                    repair.setStatus(metier.models.Repair.Status.valueOf(status.toUpperCase()));
                    JOptionPane.showMessageDialog(this, "Status updated!");
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Bad status!");
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
                String clientName = JOptionPane.showInputDialog(this, "Client Name:");
                String imei = JOptionPane.showInputDialog(this, "Device IMEI:");
                String type = JOptionPane.showInputDialog(this, "Device Type:");
                String brand = JOptionPane.showInputDialog(this, "Brand:");
                String model = JOptionPane.showInputDialog(this, "Model:");
                String desc = JOptionPane.showInputDialog(this, "Repair Description:");
                String costInput = JOptionPane.showInputDialog(this, "Repair Cost:");
                if(clientName==null||clientName.isBlank()||imei==null||type==null||brand==null||model==null||desc==null||costInput==null||clientName.isBlank()||imei.isBlank()||type.isBlank()||brand.isBlank()||model.isBlank()||desc.isBlank()||costInput.isBlank()) {
                    JOptionPane.showMessageDialog(this, "All fields are required."); return;
                }
                try {
                    double cost = Double.parseDouble(costInput);
                    var device = new metier.models.Device(imei, type, brand, model);
                    String code = java.util.UUID.randomUUID().toString().substring(0,8);
                    var repair = new metier.models.Repair(code, user, cost, device, desc, clientName);
                    service.getRepairService().addRepair(repair);
                    JOptionPane.showMessageDialog(this, "Request added! Code: " + code);
                } catch (NumberFormatException nfx) {
                    JOptionPane.showMessageDialog(this, "Cost must be a valid number.");
                }
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
            btns[2].addActionListener(e -> {
                String code = JOptionPane.showInputDialog(this, "Enter code:");
                if(code == null || code.isBlank()) return;
                var repair = service.getRepairByCode(code);
                if(repair == null) { JOptionPane.showMessageDialog(this, "Not found."); return; }
                String status = JOptionPane.showInputDialog(this, "Enter new status (PENDING, IN_PROGRESS, COMPLETED):");
                if(status==null||status.isBlank()) return;
                try {
                    repair.setStatus(metier.models.Repair.Status.valueOf(status.toUpperCase()));
                    JOptionPane.showMessageDialog(this, "Status updated!");
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Bad status!");
                }
            });
        }
        actionsPanel.setPreferredSize(new Dimension(470, 380));
        add(actionsPanel, BorderLayout.CENTER);
    }
    public DashboardPanel() { this(null, null); }
}
