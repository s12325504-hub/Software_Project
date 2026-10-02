package service;

import domain.Restaurant;
import repository.RestaurantRepository;
import java.util.List;

public class RestaurantService {
    private RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getAvailableRestaurants() {
        // يعيد فقط المطاعم المفتوحة
        return restaurantRepository.findByIsOpenTrue();
    }
}