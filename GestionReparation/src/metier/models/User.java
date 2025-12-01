package metier.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public enum Role { OWNER, REPAIRER, BOTH }

    private String username;
    private String password;
    private Role role;
    private String email;
    private String phone;
    @EqualsAndHashCode.Exclude
    private Shop shop;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String email, String phone, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
}
