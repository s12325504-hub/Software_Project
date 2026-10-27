package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.Restaurant;
import repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return available open restaurants from repository")
    void testGetAvailableRestaurants_ReturnsOpenRestaurants() {
        Restaurant restaurant1 = new Restaurant("1", "Pizza House", true, new BigDecimal("15.00"), "Nablus Center");
        Restaurant restaurant2 = new Restaurant("3", "Shawarma Express", true, new BigDecimal("10.00"), "Nablus Center");
        List<Restaurant> mockOpenRestaurants = Arrays.asList(restaurant1, restaurant2);

        when(restaurantRepository.findByIsOpenTrue()).thenReturn(mockOpenRestaurants);

        List<Restaurant> result = restaurantService.getAvailableRestaurants();

        assertAll("Available Restaurants Verification",
            () -> assertEquals(2, result.size(), "Should return 2 open restaurants"),
            () -> assertEquals("Pizza House", result.get(0).getName()),
            () -> assertEquals("Shawarma Express", result.get(1).getName())
        );

        verify(restaurantRepository, times(1)).findByIsOpenTrue();
    }

    @Test
    @DisplayName("Should return empty list when no restaurants are open")
    void testGetAvailableRestaurants_ReturnsEmptyListWhenNoneOpen() {
        when(restaurantRepository.findByIsOpenTrue()).thenReturn(Collections.emptyList());

        List<Restaurant> result = restaurantService.getAvailableRestaurants();

        assertNotNull(result, "Returned list should not be null");
        assertTrue(result.isEmpty(), "Returned list should be empty");

        verify(restaurantRepository, times(1)).findByIsOpenTrue();
    }
}