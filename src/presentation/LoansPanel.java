package presentation;

import metier.models.User;
import metier.models.LoanHistory;
import metier.models.Repair;
import metier.services.BusinessService;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoansPanel extends JPanel {
    private final User user;
    private final BusinessService service;

    public LoansPanel(User user, BusinessService service) {
        this.user = user;
        this.service = service;
        setLayout(new BorderLayout(10,10));
        setBackground(Theme.DASHBOARD_BG1);

        JLabel title = new JLabel("Loans & Revenue History for " + (user != null ? user.getUsername() : "?"), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setForeground(Theme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Completed Requests", buildCompletedTab());
        tabs.addTab("Loans", buildLoansTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildCompletedTab() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setOpaque(false);
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(Theme.CARD_BG);
        area.setForeground(Theme.TEXT_PRIMARY);
        List<Repair> repairs = service.getRepairsByRepairer(user).stream()
                .filter(r -> r.getStatus() == Repair.Status.COMPLETED).toList();
        StringBuilder sb = new StringBuilder();
        double total = 0;
        for (Repair r : repairs) {
            total += r.getTotalCost();
            sb.append("Code: ").append(r.getCode())
              .append(" | Device: ").append(r.getDevice().getBrand()).append(" ").append(r.getDevice().getModel())
              .append(" | Client: ").append(r.getClientName())
              .append(" | Amount: ").append(r.getTotalCost()).append("\n");
        }
        sb.append("\nTotal completed amount: ").append(total);
        area.setText(sb.toString());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLoansTab() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setOpaque(false);

        // Top: add new loan form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setOpaque(false);
        JLabel nameLbl = new JLabel("Person:");
        nameLbl.setForeground(Theme.TEXT_PRIMARY);
        JTextField nameField = new JTextField(10);
        nameField.setBackground(Theme.INPUT_BG);
        nameField.setForeground(Theme.INPUT_TEXT);
        nameField.setCaretColor(Theme.INPUT_TEXT);
        nameField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
        JLabel amountLbl = new JLabel("Amount:");
        amountLbl.setForeground(Theme.TEXT_PRIMARY);
        JTextField amountField = new JTextField(6);
        amountField.setBackground(Theme.INPUT_BG);
        amountField.setForeground(Theme.INPUT_TEXT);
        amountField.setCaretColor(Theme.INPUT_TEXT);
        amountField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
        JButton addBtn = new JButton("Add Loan");
        addBtn.setBackground(Theme.ACCENT);
        addBtn.setForeground(Theme.BUTTON_TEXT);
        form.add(nameLbl); form.add(nameField);
        form.add(amountLbl); form.add(amountField);
        form.add(addBtn);
        panel.add(form, BorderLayout.NORTH);

        // Center: active loans list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(20, 28, 40)); // Better background color for loans list
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER, 1));
        scroll.getViewport().setBackground(new Color(20, 28, 40));
        panel.add(scroll, BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String amountStr = amountField.getText().trim();
            if (name.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and amount are required.");
                return;
            }
            try {
                double amount = Double.parseDouble(amountStr);
                service.addLoan(user, name, amount);
                nameField.setText("");
                amountField.setText("");
                refreshLoans(listPanel);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        refreshLoans(listPanel);
        return panel;
    }

    private void refreshLoans(JPanel listPanel) {
        listPanel.removeAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<LoanHistory> active = service.getActiveLoansForUser(user);
        if (active.isEmpty()) {
            JLabel none = new JLabel("No active loans.");
            none.setForeground(Theme.TEXT_MUTED);
            none.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            listPanel.add(none);
        } else {
            for (LoanHistory h : active) {
                // Create a compact single-line panel
                JPanel loanRow = new JPanel(new BorderLayout(10, 0));
                loanRow.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 10, 5, 10),
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER)));
                loanRow.setBackground(new Color(25, 35, 50)); // Better contrasting background
                loanRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                loanRow.setPreferredSize(new Dimension(loanRow.getPreferredSize().width, 40));
                
                // Left side: Name, Amount, Date/Time
                JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
                infoPanel.setOpaque(false);
                
                JLabel nameLabel = new JLabel(h.getPersonName());
                nameLabel.setForeground(Theme.TEXT_PRIMARY);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                nameLabel.setPreferredSize(new Dimension(120, 30));
                
                JLabel amountLabel = new JLabel(String.format("%.2f", h.getAmount()));
                amountLabel.setForeground(Theme.ACCENT);
                amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                amountLabel.setPreferredSize(new Dimension(80, 30));
                
                JLabel dateLabel = new JLabel(h.getDate().format(fmt));
                dateLabel.setForeground(Theme.TEXT_MUTED);
                dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                dateLabel.setPreferredSize(new Dimension(180, 30));
                
                infoPanel.add(nameLabel);
                infoPanel.add(amountLabel);
                infoPanel.add(dateLabel);
                
                loanRow.add(infoPanel, BorderLayout.CENTER);
                
                // Right side: Button
                JButton returnedBtn = new JButton("Mark Returned");
                returnedBtn.setBackground(Theme.ACCENT_DARK);
                returnedBtn.setForeground(Theme.BUTTON_TEXT);
                returnedBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                returnedBtn.setPreferredSize(new Dimension(120, 30));
                returnedBtn.addActionListener(ev -> {
                    service.markLoanReturned(h);
                    refreshLoans(listPanel);
                });
                loanRow.add(returnedBtn, BorderLayout.EAST);
                
                listPanel.add(loanRow);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}
