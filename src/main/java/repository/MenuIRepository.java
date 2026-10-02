package repository;

import domain.Menu;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuIRepository {
    private List<Menu> menuItems = new ArrayList<>();

    public MenuIRepository() {
        // بيانات تجريبية للمطعم رقم "1"
        menuItems.add(new Menu("m1", "1", "Margherita Pizza", new BigDecimal("10.50"), true));
        menuItems.add(new Menu("m2", "1", "Pepperoni Pizza", new BigDecimal("12.00"), false)); // غير متوفر
        menuItems.add(new Menu("m3", "1", "Garlic Bread", new BigDecimal("4.00"), true));
    }

    // جلب العناصر المتوفرة فقط لمطعم معين
    public List<Menu> findByRestaurantIdAndIsAvailableTrue(String restaurantId) {
        List<Menu> availableMenuItems = new ArrayList<>();

        for (Menu item : menuItems) {
            // فحص الشرطين معا: مطابقة رقم المطعم + أن الوجبة متاحة (Available)
            if (item.getRestaurantId().equals(restaurantId) && item.isAvailable()) {
                availableMenuItems.add(item);
            }
        }

        return availableMenuItems;
    }
}