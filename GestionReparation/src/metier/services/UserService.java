package metier.services;

import dao.*;
import metier.models.*;

public class UserService {
    private final BusinessService businessService = new BusinessService();

    public User login(String username, String password) throws Exception {
        return businessService.authenticate(username, password);
    }

    // SIGNUP OWNER (shop creation is separate)
    public void signUpOwner(String username, String email, String phone,
                            String password, String confirmPassword,
                            boolean isAlsoRepairer) throws Exception {
        businessService.signUpOwner(username, email, phone, password, confirmPassword, isAlsoRepairer);
    }

    // ADD REPAIRER TO SHOP
    public void addRepairerToShop(String username, String password, String email, String phone, Shop shop) throws Exception {
        businessService.addRepairer(username, password, email, phone, shop);
    }
}
