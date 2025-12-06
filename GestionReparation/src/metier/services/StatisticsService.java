package metier.services;

import metier.models.Repair;
import metier.models.User;
import dao.RepairDAO;

public class StatisticsService {
    private final RepairDAO repairDAO = new RepairDAO();

    public int getTotalRepairs(User user) {
        // If you later want per-user totals, filter by assignedRepairer.
        return repairDAO.getAllRepairs().size();
    }

    public int getCompletedRepairs(User user) {
        return repairDAO.countRepairsByStatus(Repair.Status.COMPLETED);
    }
}
