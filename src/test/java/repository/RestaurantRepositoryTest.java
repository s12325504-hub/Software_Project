package repository;

import static org.junit.jupiter.api.Assertions.*;

import domain.Restaurant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RestaurantRepositoryTest {

    private RestaurantRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RestaurantRepository();
    }

    @AfterEach
    void tearDown() {
        repository = null;
    }

    @Test
    @DisplayName("Should return only open restaurants")
    void testFindByIsOpenTrue() {
        // 1. المتوقع (Expected)
        int expectedSize = 2;
        String expectedFirstName = "Pizza Time";
        String expectedSecondName = "Shawarman";

        // 2. الفعلي (Actual)
        List<Restaurant> actualOpenRestaurants = repository.findByIsOpenTrue();

        // 3. المقارنة والتحقق (Assert)
        assertAll("Open Restaurants Verification",
            () -> assertEquals(expectedSize, actualOpenRestaurants.size(), "Should return exactly 2 open restaurants"),
            () -> assertTrue(actualOpenRestaurants.stream().allMatch(Restaurant::isOpen), "All returned restaurants must be open"),
            () -> assertEquals(expectedFirstName, actualOpenRestaurants.get(0).getName(), "First restaurant name mismatch"),
            () -> assertEquals(expectedSecondName, actualOpenRestaurants.get(1).getName(), "Second restaurant name mismatch")
        );
    }

    @Test
    @DisplayName("Should find restaurant by valid existing ID")
    void testFindById_ExistingId() {
        // 1. المتوقع (Expected)
        String expectedId = "1";
        String expectedName = "Pizza Time";

        // 2. الفعلي (Actual)
        Restaurant actualRestaurant = repository.findById(expectedId);

        // 3. المقارنة (Assert)
        assertNotNull(actualRestaurant, "Restaurant should not be null");
        assertAll("Restaurant Details Verification",
            () -> assertEquals(expectedId, actualRestaurant.getId(), "Restaurant ID should match input"),
            () -> assertEquals(expectedName, actualRestaurant.getName(), "Restaurant name should match")
        );
    }

    @Test
    @DisplayName("Should return null when searching with non-existing ID")
    void testFindById_NonExistingId() {
        // 1. المتوقع والفعلي
        String nonExistingId = "999";
        Restaurant actualRestaurant = repository.findById(nonExistingId);

        // 2. المقارنة
        assertNull(actualRestaurant, "Restaurant should be null for non-existing ID");
    }

    @Test
    @DisplayName("Should return null when searching with null ID")
    void testFindById_NullId() {
        // 1. المتوقع والفعلي
        Restaurant actualRestaurant = repository.findById(null);

        // 2. المقارنة
        assertNull(actualRestaurant, "Restaurant should be null when given a null ID");
    }
}