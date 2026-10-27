package repository;

import domain.Restaurant;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantRepository {
    private List<Restaurant> restaurants = new ArrayList<>();

    public RestaurantRepository() {
        // تمرير كافة المقاييس المطلوبة لسياسات التوصيل (US2.3)
        restaurants.add(new Restaurant("1", "Pizza Time", true, new BigDecimal("15.00"), Arrays.asList("Nablus", "Rafidia", "Asira", "Old City")));
        restaurants.add(new Restaurant("2", "Orgada Burger", false, new BigDecimal("20.00"), Arrays.asList("Rafidia")));
        restaurants.add(new Restaurant("3", "Shawarman", true, new BigDecimal("10.00"), Arrays.asList("Nablus")));
    }

    // دالة جلب المطاعم المفتوحة (باستخدام for-each loop)
    public List<Restaurant> findByIsOpenTrue() {
        List<Restaurant> openRestaurants = new ArrayList<>();
         
        for (Restaurant restaurant : restaurants) {
            if (restaurant.isOpen()) {
                openRestaurants.add(restaurant);
            }
        }
        
        return openRestaurants; 
    }

    // دالة البحث بـ ID لدعم OrderService (باستخدام for-each loop)
    public Restaurant findById(String id) {
        if (id == null) {
            return null;
        }

        for (Restaurant restaurant : restaurants) {
            if (restaurant.getId().equals(id)) {
                return restaurant;
            }
        }

        return null;
    }
}