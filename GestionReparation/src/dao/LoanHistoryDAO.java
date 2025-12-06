package dao;

import metier.models.LoanHistory;
import java.util.ArrayList;
import java.util.List;

public class LoanHistoryDAO {
    private final List<LoanHistory> histories = new ArrayList<>();

    public void addLoanHistory(LoanHistory h) { histories.add(h); }

    public List<LoanHistory> getAllLoanHistories() { return histories; }
}
