package presentation;

import metier.models.Repair;
import metier.services.RepairService;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TrackRequestPanel extends JPanel {
    JTextField codeField, clientField;
    JTextArea resultArea;
    public TrackRequestPanel(RepairService repairService) {
        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.DASHBOARD_BG2);
        JPanel header = new JPanel(new GridLayout(2,1));
        header.setBackground(Theme.DASHBOARD_BG2);
        JLabel bigTitle = new JLabel("Track Request - Staff View", SwingConstants.CENTER);
        bigTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        header.add(bigTitle);
        add(header, BorderLayout.NORTH);
        JPanel filters = new JPanel();
        filters.setBackground(Theme.DASHBOARD_BG2);
        JLabel codeLabel = new JLabel("Repair Code:");
        codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        codeLabel.setForeground(Theme.TEXT_PRIMARY);
        codeField = new JTextField(9);
        codeField.setBackground(Theme.INPUT_BG);
        codeField.setForeground(Theme.INPUT_TEXT);
        codeField.setCaretColor(Theme.INPUT_TEXT);
        codeField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
        JLabel clientLabel = new JLabel("Client Name:");
        clientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        clientLabel.setForeground(Theme.TEXT_PRIMARY);
        clientField = new JTextField(9);
        clientField.setBackground(Theme.INPUT_BG);
        clientField.setForeground(Theme.INPUT_TEXT);
        clientField.setCaretColor(Theme.INPUT_TEXT);
        clientField.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER));
        JButton btnTrack = new JButton("Search");
        btnTrack.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnTrack.setBackground(Theme.ACCENT);
        btnTrack.setForeground(Theme.BUTTON_TEXT);
        filters.add(codeLabel); filters.add(codeField);
        filters.add(clientLabel); filters.add(clientField); filters.add(btnTrack);
        add(filters, BorderLayout.CENTER);
        resultArea = new JTextArea(9, 32);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultArea.setBackground(Theme.CARD_BG);
        resultArea.setForeground(Theme.TEXT_PRIMARY);
        JScrollPane scroll = new JScrollPane(resultArea);
        add(scroll, BorderLayout.SOUTH);
        btnTrack.addActionListener(e -> {
            String code = codeField.getText().trim().toLowerCase();
            String client = clientField.getText().trim().toLowerCase();
            List<Repair> repairs = repairService.getAllRepairs();
            List<Repair> filtered = repairs.stream().filter(r ->
                (code.isEmpty() || r.getCode().toLowerCase().contains(code)) &&
                (client.isEmpty() || (r.getClientName()!=null && r.getClientName().toLowerCase().contains(client)))
            ).collect(Collectors.toList());
            if(filtered.isEmpty()) {
                resultArea.setText("No matches found.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Repair r : filtered) {
                    sb.append("Code: ").append(r.getCode())
                            .append(" | Client: ").append(r.getClientName()!=null?r.getClientName():"-")
                            .append(" | Device: ").append(r.getDevice().getBrand()).append(" ").append(r.getDevice().getModel())
                            .append(" | Status: ").append(r.getStatus())
                            .append(" | Desc: ").append(r.getDescription()).append("\n");
                }
                resultArea.setText(sb.toString());
            }
        });
    }
}
