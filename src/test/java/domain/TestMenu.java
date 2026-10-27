package domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestMenu {

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu("M101", "REST-500", "Margherita Pizza", new BigDecimal("25.50"), true);
    }

    @AfterEach
    void tearDown() {
        menu = null;
    }

    @Test
    @DisplayName("Should correctly initialize menu item properties via constructor")
    void testConstructorAndGetters() {
        assertAll("Menu Properties Verification",
            () -> assertEquals("M101", menu.getId(), "Menu ID should match input"),
            () -> assertEquals("REST-500", menu.getRestaurantId(), "Restaurant ID should match input"),
            () -> assertEquals("Margherita Pizza", menu.getName(), "Name should match input"),
            () -> assertEquals(new BigDecimal("25.50"), menu.getPrice(), "Price should match input"),
            () -> assertTrue(menu.isAvailable(), "Availability status should be true")
        );
    }

    @Test
    @DisplayName("Should correctly handle unavailable status")
    void testUnavailableMenu() {
        Menu unavailableMenu = new Menu("M102", "REST-500", "Special Pasta", new BigDecimal("30.00"), false);

        assertFalse(unavailableMenu.isAvailable(), "Availability status should be false for unavailable item");
    }
}