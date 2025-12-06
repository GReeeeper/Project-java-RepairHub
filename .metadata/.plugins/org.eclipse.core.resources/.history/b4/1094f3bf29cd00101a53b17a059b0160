package metier.services;

import dao.RepairDAO;
import metier.models.Repair;
import metier.models.Repair.Status;
import metier.models.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RepairService {

    private final RepairDAO repairDAO = new RepairDAO();

    // Add a new repair
    public void addRepair(Repair repair) {
        if (repair != null) repairDAO.addRepair(repair);
    }

    // Get repair by its code
    public Repair getRepairByCode(String code) {
        return repairDAO.getRepairByCode(code);
    }

    // Get all repairs
    public List<Repair> getAllRepairs() {
        List<Repair> all = repairDAO.getAllRepairs();
        return all == null ? Collections.emptyList() : all;
    }

    // Get repairs by their status
    public List<Repair> getRepairsByStatus(Status status) {
        return repairDAO.getRepairsByStatus(status);
    }

    // Validate a repair (mark as IN_PROGRESS)
    public void validateRepair(Repair repair) {
        if (repair == null) return;
        repair.validate();
        repairDAO.updateRepairStatus(repair, Status.IN_PROGRESS);
    }

    // Complete payment for a repair (mark as COMPLETED)
    public void completeRepairPayment(Repair repair) {
        if (repair == null) return;
        repair.completePayment();
        repairDAO.updateRepairStatus(repair, Status.COMPLETED);
    }

    // count by status (delegate)
    public int countRepairsByStatus(Status status) {
        return repairDAO.countRepairsByStatus(status);
    }

    // Update status generic
    public void updateRepairStatus(Repair repair, Status status) {
        repairDAO.updateRepairStatus(repair, status);
    }

    // Get repairs assigned to a specific repairer (null-safe)
    public List<Repair> getRepairsByRepairer(User repairer) {
        if (repairer == null) return Collections.emptyList();
        return repairDAO.getAllRepairs().stream()
                .filter(r -> r != null && r.getAssignedRepairer() != null && r.getAssignedRepairer().equals(repairer))
                .toList();
    }
}
