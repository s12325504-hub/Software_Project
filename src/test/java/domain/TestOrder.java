package domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import observer.OrderObserver;

class TestOrder {

    private Order order;

    @Mock
    private OrderObserver mockObserver;

    @Mock
    private Driver mockDriver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Order("ORD-001", "CUST-100", "REST-500", Collections.emptyList(), new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Should correctly initialize order with default values")
    void testConstructorAndGetters() {
        assertAll("Order Constructor Defaults Verification",
            () -> assertEquals("ORD-001", order.getId(), "Order ID should match constructor input"),
            () -> assertEquals(new BigDecimal("50.00"), order.getTotalPrice(), "Total price should match constructor input"),
            () -> assertEquals(BigDecimal.ZERO, order.getDeliveryFee(), "Default delivery fee should be ZERO"),
            () -> assertEquals(new BigDecimal("50.00"), order.getFinalPrice(), "Default final price should match total price"),
            () -> assertEquals("PENDING", order.getStatus(), "Default status should be PENDING"),
            () -> assertEquals("UNPAID", order.getPaymentStatus(), "Default payment status should be UNPAID"),
            () -> assertNull(order.getAssignedDriver(), "Assigned driver should initially be null"),
            () -> assertEquals("", order.getLastEvent(), "Last event should initially be empty")
        );
    }

    @Test
    @DisplayName("Should correctly update delivery fee and recalculate final price")
    void testSetDeliveryFee() {
        order.setDeliveryFee(new BigDecimal("5.00"));

        assertAll("Delivery Fee and Final Price Verification",
            () -> assertEquals(new BigDecimal("5.00"), order.getDeliveryFee(), "Delivery fee should be updated to 5.00"),
            () -> assertEquals(new BigDecimal("55.00"), order.getFinalPrice(), "Final price should equal total price + delivery fee")
        );
    }

    @Test
    @DisplayName("Should override final price when setFinalPrice is called explicitly")
    void testSetFinalPrice() {
        order.setFinalPrice(new BigDecimal("40.00"));

        assertEquals(new BigDecimal("40.00"), order.getFinalPrice(), "Final price should reflect explicitly assigned value");
    }

    @Test
    @DisplayName("Should update payment status")
    void testSetPaymentStatus() {
        order.setPaymentStatus("PAID");

        assertEquals("PAID", order.getPaymentStatus(), "Payment status should update to PAID");
    }

    @Test
    @DisplayName("Should update status, set last event, and notify registered observers")
    void testSetStatus_NotifiesObservers() {
        order.addObserver(mockObserver);

        order.setStatus("PREPARING");

        assertAll("Status Update Verification",
            () -> assertEquals("PREPARING", order.getStatus(), "Status should be updated to PREPARING"),
            () -> assertEquals("STATUS_CHANGED", order.getLastEvent(), "Last event should be STATUS_CHANGED")
        );

        verify(mockObserver, times(1)).update(order);
    }

    @Test
    @DisplayName("Should assign driver, set last event, and notify registered observers")
    void testSetAssignedDriver_NotifiesObservers() {
        order.addObserver(mockObserver);

        order.setAssignedDriver(mockDriver);

        assertAll("Driver Assignment Verification",
            () -> assertEquals(mockDriver, order.getAssignedDriver(), "Assigned driver should match injected mock driver"),
            () -> assertEquals("DRIVER_ASSIGNED", order.getLastEvent(), "Last event should be DRIVER_ASSIGNED")
        );

        verify(mockObserver, times(1)).update(order);
    }

    @Test
    @DisplayName("Should evaluate canBeCancelled based on PENDING status (case-insensitive)")
    void testCanBeCancelled() {
        assertTrue(order.canBeCancelled(), "Order with PENDING status should be cancellable");

        order.setStatus("PREPARING");
        assertFalse(order.canBeCancelled(), "Order with PREPARING status should not be cancellable");

        order.setStatus("DELIVERED");
        assertFalse(order.canBeCancelled(), "Order with DELIVERED status should not be cancellable");
    }
}