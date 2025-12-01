package metier.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Shop {
    private String name;
    private double rawCash = 0;
    private User owner;
    private List<User> repairers = new ArrayList<>();

    public Shop(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public void addRepairer(User repairer) {
        repairers.add(repairer);
        repairer.setShop(this);
    }

    public void updateCash(double amount) {
        rawCash += amount;
    }
}
