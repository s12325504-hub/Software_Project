package service;

import domain.Menu;
import repository.MenuIRepository;
import java.util.List;

public class MenuService {
    private MenuIRepository menuItemRepository;

    public MenuService(MenuIRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<Menu> getRestaurantMenu(String restaurantId) {
        // إرجاع العناصر المتوفرة فقط وتتضمن السعر الصحيح
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }
}