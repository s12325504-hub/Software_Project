package domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RestaurantTest {

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant("R101", "Nablus Sweets", true, new BigDecimal("15.50"), Arrays.asList("Nablus", "Rafidia", "Asira", "Old City"));
    }

    @AfterEach
    void tearDown() {
        restaurant = null;
    }

    @Test
    @DisplayName("Should correctly initialize all fields via constructor")
    void testConstructorAndGetters() {
        // 1. المتوقع (Expected)
        String expectedId = "R101";
        String expectedName = "Nablus Sweets";
        boolean expectedIsOpen = true;
        BigDecimal expectedMinOrder = new BigDecimal("15.50");
        List<String> expectedDeliveryZones = Arrays.asList("Nablus", "Rafidia", "Asira", "Old City");

        // 2. الفعلي (Actual)
        String actualId = restaurant.getId();
        String actualName = restaurant.getName();
        boolean actualIsOpen = restaurant.isOpen();
        BigDecimal actualMinOrder = restaurant.getMinimumOrderValue();
        List<String> actualDeliveryZones = restaurant.getDeliveryZones();

        // 3. المقارنة (Assert)
        assertAll("Restaurant Properties",
            () -> assertEquals(expectedId, actualId, "ID should match input"),
            () -> assertEquals(expectedName, actualName, "Name should match input"),
            () -> assertEquals(expectedIsOpen, actualIsOpen, "Open status should be true"),
            () -> assertEquals(expectedMinOrder, actualMinOrder, "Minimum order value should match"),
            () -> assertEquals(expectedDeliveryZones, actualDeliveryZones, "Delivery zones should match")
        );
    }

    @Test
    @DisplayName("Should handle null values in constructor by setting defaults")
    void testConstructorWithNullValues() {
        // 1. المتوقع (Expected)
        BigDecimal expectedDefaultMinOrder = BigDecimal.ZERO;

        // 2. الفعلي (Actual)
        Restaurant nullRestaurant = new Restaurant("R102", "Default Cafe", false, null, (List<String>) null);
        BigDecimal actualMinOrder = nullRestaurant.getMinimumOrderValue();
        List<String> actualZones = nullRestaurant.getDeliveryZones();

        // 3. المقارنة (Assert)
        assertAll("Null Fallback Verification",
            () -> assertEquals(expectedDefaultMinOrder, actualMinOrder, "Null minimum order should default to BigDecimal.ZERO"),
            () -> assertNotNull(actualZones, "Null delivery zones list should default to non-null list"),
            () -> assertTrue(actualZones.isEmpty(), "Null delivery zones should default to empty list")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "'Rafidia Street', true",
        "'Asira Road', true",
        "'OLD CITY MARKET', true",
        "'Nablus Downtown', true",
        "'Ramallah Downtown', false",
        "'', false"
    })
    @DisplayName("Should correctly evaluate addresses within delivery zone (case-insensitive)")
    void testIsWithinDeliveryZone(String address, boolean expectedResult) {
        // الفعلي (Actual)
        boolean actualResult = restaurant.isWithinDeliveryZone(address);

        // المقارنة (Assert)
        assertEquals(expectedResult, actualResult, "Address delivery evaluation mismatch for: " + address);
    }

    @Test
    @DisplayName("Should return false when checking delivery zone with null address")
    void testIsWithinDeliveryZone_NullAddress() {
        // 1. المتوقع والفعلي
        boolean expectedResult = false;
        boolean actualResult = restaurant.isWithinDeliveryZone(null);

        // 2. المقارنة
        assertEquals(expectedResult, actualResult, "Null address should return false");
    }

    @Test
    @DisplayName("Should return false when restaurant delivery zone is empty")
    void testIsWithinDeliveryZone_EmptyDeliveryZone() {
        // 1. المتوقع والفعلي
        Restaurant emptyZoneRestaurant = new Restaurant("R103", "No Zone Restaurant", true, BigDecimal.TEN, (List<String>) null);
        boolean expectedResult = false;
        boolean actualResult = emptyZoneRestaurant.isWithinDeliveryZone("Nablus Center");

        // 2. المقارنة
        assertEquals(expectedResult, actualResult, "Empty delivery zone should always return false");
    }
}