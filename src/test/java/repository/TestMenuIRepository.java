package repository;

import static org.junit.jupiter.api.Assertions.*;

import domain.Menu;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestMenuIRepository {

    private MenuIRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MenuIRepository();
    }

    @AfterEach
    void tearDown() {
        repository = null;
    }

    @Test
    @DisplayName("Should return only available menu items for a specific restaurant ID")
    void testFindByRestaurantIdAndIsAvailableTrue_ExistingRestaurant() {
        List<Menu> availableItems = repository.findByRestaurantIdAndIsAvailableTrue("1");

        assertAll("Available Menu Items Verification",
            () -> assertEquals(2, availableItems.size(), "Should return exactly 2 available items for restaurant ID '1'"),
            () -> assertTrue(availableItems.stream().allMatch(Menu::isAvailable), "All returned items must be available"),
            () -> assertEquals("Margherita Pizza", availableItems.get(0).getName()),
            () -> assertEquals("Garlic Bread", availableItems.get(1).getName())
        );
    }

    @Test
    @DisplayName("Should return an empty list when searching for a restaurant ID with no available items")
    void testFindByRestaurantIdAndIsAvailableTrue_NonExistingRestaurant() {
        List<Menu> availableItems = repository.findByRestaurantIdAndIsAvailableTrue("999");

        assertNotNull(availableItems, "Returned list should not be null");
        assertTrue(availableItems.isEmpty(), "Returned list should be empty for non-existing restaurant ID");
    }

    @Test
    @DisplayName("Should return an empty list when restaurant ID is null")
    void testFindByRestaurantIdAndIsAvailableTrue_NullRestaurantId() {
        List<Menu> availableItems = repository.findByRestaurantIdAndIsAvailableTrue(null);

        assertNotNull(availableItems, "Returned list should not be null");
        assertTrue(availableItems.isEmpty(), "Returned list should be empty when given a null restaurant ID");
    }
}
