package domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestOrderItem {

    private Menu mockMenu;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        mockMenu = mock(Menu.class);
        when(mockMenu.getPrice()).thenReturn(new BigDecimal("12.50"));
        orderItem = new OrderItem(mockMenu, 3);
    }

    @Test
    @DisplayName("Should correctly initialize order item with menu and quantity")
    void testConstructorAndGetters() {
        assertAll("OrderItem Properties Verification",
            () -> assertEquals(mockMenu, orderItem.getMenuItem(), "Menu item should match injected instance"),
            () -> assertEquals(3, orderItem.getQuantity(), "Quantity should match constructor input")
        );
    }

    @Test
    @DisplayName("Should calculate item total as price multiplied by quantity")
    void testGetItemTotal() {
        BigDecimal itemTotal = orderItem.getItemTotal();

        assertEquals(new BigDecimal("37.50"), itemTotal, "Item total should equal 12.50 * 3 = 37.50");
        verify(mockMenu, times(1)).getPrice();
    }

    @Test
    @DisplayName("Should calculate item total correctly when quantity is zero")
    void testGetItemTotal_ZeroQuantity() {
        OrderItem zeroQtyItem = new OrderItem(mockMenu, 0);

        BigDecimal itemTotal = zeroQtyItem.getItemTotal();

        assertEquals(new BigDecimal("0.00"), itemTotal, "Item total should be 0.00 when quantity is zero");
    }
}