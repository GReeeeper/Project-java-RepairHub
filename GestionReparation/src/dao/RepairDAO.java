package dao;

import metier.models.Repair;
import metier.models.Repair.Status;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RepairDAO {
    private static final List<Repair> repairs = new ArrayList<>();

    public void addRepair(Repair repair) {
        if (repair != null) repairs.add(repair);
    }

    // Return a defensive copy to avoid callers mutating internal list
    public List<Repair> getAllRepairs() {
        return Collections.unmodifiableList(new ArrayList<>(repairs));
    }
    
    public Repair getRepairByCode(String code) {
        if (code == null) return null;
        return repairs.stream()
                .filter(r -> r != null && code.equals(r.getCode()))
                .findFirst()
                .orElse(null);
    }

    public List<Repair> getRepairsByStatus(Status status) {
        List<Repair> filtered = new ArrayList<>();
        if (status == null) return filtered;
        for (Repair r : repairs) {
            if (r != null && r.getStatus() == status) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    public void updateRepairStatus(Repair repair, Status status) {
        if (repair != null && status != null) {
            repair.setStatus(status);
        }
    }

    public int countRepairsByStatus(Status status) {
        if (status == null) return 0;
        return (int) repairs.stream()
                .filter(r -> r != null && r.getStatus() == status)
                .count();
    }
}
