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
        listPanel.setOpaque(false);
        JScrollPane scroll = new JScrollPane(listPanel);
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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<LoanHistory> active = service.getActiveLoansForUser(user);
        if (active.isEmpty()) {
            JLabel none = new JLabel("No active loans.");
            none.setForeground(Theme.TEXT_MUTED);
            listPanel.add(none);
        } else {
            for (LoanHistory h : active) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(8, 8, 8, 8),
                        BorderFactory.createLineBorder(Theme.CARD_BORDER)));
                card.setBackground(Theme.CARD_BG);
                JTextArea info = new JTextArea();
                info.setEditable(false);
                info.setBackground(Theme.CARD_BG);
                info.setForeground(Theme.TEXT_PRIMARY);
                info.setText("Loan to: " + h.getPersonName() +
                        "\nAmount: " + h.getAmount() +
                        "\nDate: " + h.getDate().format(fmt));
                card.add(info, BorderLayout.CENTER);
                JButton returnedBtn = new JButton("Loan returned");
                returnedBtn.setBackground(Theme.ACCENT_DARK);
                returnedBtn.setForeground(Theme.BUTTON_TEXT);
                returnedBtn.addActionListener(ev -> {
                    service.markLoanReturned(h);
                    refreshLoans(listPanel);
                });
                card.add(returnedBtn, BorderLayout.EAST);
                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(6));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}
