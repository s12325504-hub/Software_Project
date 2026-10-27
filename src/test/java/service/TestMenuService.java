package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.Menu;
import repository.MenuIRepository;

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

class TestMenuService {

    @Mock
    private MenuIRepository menuItemRepository;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return available menu items for a valid restaurant ID")
    void testGetRestaurantMenu_ReturnsAvailableItems() {
        Menu item1 = new Menu("m1", "1", "Margherita Pizza", new BigDecimal("10.50"), true);
        Menu item2 = new Menu("m3", "1", "Garlic Bread", new BigDecimal("4.00"), true);
        List<Menu> mockMenuItems = Arrays.asList(item1, item2);

        when(menuItemRepository.findByRestaurantIdAndIsAvailableTrue("1")).thenReturn(mockMenuItems);

        List<Menu> result = menuService.getRestaurantMenu("1");

        assertAll("Restaurant Menu Items Verification",
            () -> assertNotNull(result, "Returned menu list should not be null"),
            () -> assertEquals(2, result.size(), "Should return exactly 2 menu items"),
            () -> assertEquals("Margherita Pizza", result.get(0).getName()),
            () -> assertEquals("Garlic Bread", result.get(1).getName())
        );

        verify(menuItemRepository, times(1)).findByRestaurantIdAndIsAvailableTrue("1");
    }

    @Test
    @DisplayName("Should return empty list when no menu items are available for restaurant")
    void testGetRestaurantMenu_ReturnsEmptyListWhenNoneAvailable() {
        when(menuItemRepository.findByRestaurantIdAndIsAvailableTrue("999")).thenReturn(Collections.emptyList());

        List<Menu> result = menuService.getRestaurantMenu("999");

        assertNotNull(result, "Returned menu list should not be null");
        assertTrue(result.isEmpty(), "Returned menu list should be empty");

        verify(menuItemRepository, times(1)).findByRestaurantIdAndIsAvailableTrue("999");
    }

    @Test
    @DisplayName("Should handle null restaurant ID gracefully by returning empty list")
    void testGetRestaurantMenu_NullRestaurantId() {
        when(menuItemRepository.findByRestaurantIdAndIsAvailableTrue(null)).thenReturn(Collections.emptyList());

        List<Menu> result = menuService.getRestaurantMenu(null);

        assertNotNull(result, "Returned menu list should not be null");
        assertTrue(result.isEmpty(), "Returned menu list should be empty when given a null restaurant ID");

        verify(menuItemRepository, times(1)).findByRestaurantIdAndIsAvailableTrue(null);
    }
}