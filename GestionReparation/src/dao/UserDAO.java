package dao;

import metier.models.User;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final List<User> users = new ArrayList<>();

    public void addUser(User u) { users.add(u); }

    public User getUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }

    public User authenticate(String username, String password) throws Exception {
        User u = getUserByUsername(username);
        if (u == null || !u.getPassword().equals(password))
            throw new Exception("Invalid credentials");
        return u;
    }

    public List<User> getAllUsers() { return users; }
}
