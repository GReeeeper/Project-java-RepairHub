package metier.services;

import dao.*;
import lombok.NonNull;
import metier.models.*;
import exception.InvalidInputException;
import exception.ShopAssignmentException;

import java.time.LocalDateTime;
import java.util.List;

public class BusinessService {
    private final UserDAO userDAO = new UserDAO();
    private final ShopDAO shopDAO = new ShopDAO();
    private final RepairService repairService = new RepairService();
    private final LoanHistoryDAO loanHistoryDAO = new LoanHistoryDAO();

    public User authenticate(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    public void signUpOwner(@NonNull String username,
                            @NonNull String email,
                            @NonNull String phone,
                            @NonNull String password,
                            @NonNull String confirmPassword,
                            boolean isAlsoRepairer) {

        if (userDAO.getUserByUsername(username) != null)
            throw new InvalidInputException("⚠ Username already exists.");
        if (!password.equals(confirmPassword))
            throw new InvalidInputException("⚠ Password confirmation does not match.");
        if (!email.contains("@"))
            throw new InvalidInputException("⚠ Invalid email address.");
        if (phone.length() < 8)
            throw new InvalidInputException("⚠ Invalid phone number.");

        User.Role role = isAlsoRepairer ? User.Role.BOTH : User.Role.OWNER;
        User owner = new User(username, password, email, phone, role);
        userDAO.addUser(owner);
    }

    public void createShopForOwner(User owner, String shopName) {
        if (shopDAO.getShopByOwner(owner) != null)
            throw new ShopAssignmentException("⚠ Owner already has a shop.");

        Shop shop = new Shop(shopName, owner);
        shopDAO.addShop(shop);
        owner.setShop(shop);
    }

    public Shop getShopByOwner(User owner) { return shopDAO.getShopByOwner(owner); }

    public Shop getShopByRepairer(User repairer) { return shopDAO.getShopByRepairer(repairer); }

    public void addRepairer(@NonNull String username,
                            @NonNull String password,
                            String email,
                            String phone,
                            @NonNull Shop shop) {
        if (username.isBlank() || password.isBlank())
            throw new InvalidInputException("Username and password required.");
        if (shop == null)
            throw new InvalidInputException("Shop required.");
        if (userDAO.getUserByUsername(username) != null)
            throw new InvalidInputException("Username already exists!");
        User repairer = new User(username, password, email, phone, User.Role.REPAIRER);
        userDAO.addUser(repairer);
        shop.addRepairer(repairer);
        repairer.setShop(shop);
    }

    public List<Repair> getRepairsByRepairer(User repairer) { return repairService.getRepairsByRepairer(repairer); }

    public Repair getRepairByCode(String code) { return repairService.getRepairByCode(code); }

    public RepairService getRepairService() { return repairService; }

    public List<LoanHistory> getAllLoanHistories() {
        return loanHistoryDAO.getAllLoanHistories();
    }

    // --- Revenue & loans helpers ---

    public double getCompletedTotalForUser(User user) {
        return repairService.getRepairsByRepairer(user).stream()
                .filter(r -> r.getStatus() == Repair.Status.COMPLETED)
                .mapToDouble(Repair::getTotalCost)
                .sum();
    }

    public List<LoanHistory> getLoansForUser(User user) {
        return loanHistoryDAO.getAllLoanHistories().stream()
                .filter(h -> h.getUser().equals(user))
                .toList();
    }

    public List<LoanHistory> getActiveLoansForUser(User user) {
        return loanHistoryDAO.getAllLoanHistories().stream()
                .filter(h -> h.getUser().equals(user) && h.isActive())
                .toList();
    }

    public double getActiveLoanTotalForUser(User user) {
        return getActiveLoansForUser(user).stream()
                .mapToDouble(LoanHistory::getAmount)
                .sum();
    }

    public double getNetRevenueForUser(User user) {
        double completed = getCompletedTotalForUser(user);
        double loans = getActiveLoanTotalForUser(user);
        return completed - loans;
    }

    public void addLoan(User user, String personName, double amount) {
        if (user == null) throw new InvalidInputException("User required for loan.");
        if (personName == null || personName.isBlank()) throw new InvalidInputException("Person name required.");
        if (amount <= 0) throw new InvalidInputException("Amount must be positive.");
        LoanHistory loan = new LoanHistory("Loan to " + personName,
                LocalDateTime.now(), user, amount, personName, true);
        loanHistoryDAO.addLoanHistory(loan);
    }

    public void markLoanReturned(LoanHistory loan) {
        if (loan == null || !loan.isActive()) return;
        loan.setActive(false);
        LoanHistory log = new LoanHistory("Loan returned from " + loan.getPersonName(),
                LocalDateTime.now(), loan.getUser(), loan.getAmount(), loan.getPersonName(), false);
        loanHistoryDAO.addLoanHistory(log);
    }
}
