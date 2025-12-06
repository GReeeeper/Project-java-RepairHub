package presentation;

import metier.models.Repair;
import metier.services.RepairService;
import exception.InvalidInputException;
import javax.swing.*;
import java.awt.*;

public class ClientTrackRequestPanel extends JPanel {
    public ClientTrackRequestPanel(RepairService repairService, String code) {
        setLayout(new BorderLayout(10,10));
        setBackground(Theme.DASHBOARD_BG2);
        JLabel bigTitle = new JLabel("Repair Request Status", SwingConstants.CENTER);
        bigTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        bigTitle.setForeground(Theme.TEXT_PRIMARY);
        add(bigTitle, BorderLayout.NORTH);
        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resultArea.setBackground(Theme.CARD_BG);
        resultArea.setForeground(Theme.TEXT_PRIMARY);
        resultArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(resultArea);
        add(scroll, BorderLayout.CENTER);
        Repair r = repairService.getRepairByCode(code);
        if (r == null) {
            throw new InvalidInputException("No repair found for code: " + code);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Your repair request status:\n");
        sb.append("Status: ").append(r.getStatus());
        sb.append("\nDevice: ").append(r.getDevice().getBrand()).append(" ").append(r.getDevice().getModel());
        sb.append("\nDescription: ").append(r.getDescription());
        resultArea.setText(sb.toString());
    }
}
