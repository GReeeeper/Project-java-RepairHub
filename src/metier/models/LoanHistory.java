package metier.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanHistory {
    private String description;
    private LocalDateTime date;
    private User user;
    private double amount;
    private String personName;
    private boolean active; // true while loan outstanding
}
