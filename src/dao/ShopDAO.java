package dao;

import metier.models.Shop;
import metier.models.User;

import java.util.ArrayList;
import java.util.List;

public class ShopDAO {
    private final List<Shop> shops = new ArrayList<>();

    public void addShop(Shop s) { shops.add(s); }

    public Shop getShopByOwner(User owner) {
        return shops.stream().filter(s -> s.getOwner().equals(owner)).findFirst().orElse(null);
    }

    public Shop getShopByRepairer(User repairer) {
        return shops.stream().filter(s -> s.getRepairers().contains(repairer)).findFirst().orElse(null);
    }

    public void updateCash(double amount, Shop shop) { shop.updateCash(amount); }

    public List<Shop> getAllShops() { return shops; }
}
