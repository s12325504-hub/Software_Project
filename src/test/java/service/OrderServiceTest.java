package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import domain.Menu;
import domain.Order;
import domain.OrderItem;
import domain.Restaurant;
import repository.OrderRepository;
import repository.RestaurantRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private DeliveryCostService deliveryCostService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(
                orderRepository,
                restaurantRepository,
                notificationService,
                deliveryCostService
        );
    }

    // ==========================================
    // 1. CONSTRUCTOR COVERAGE TESTS
    // ==========================================

    @Test
    @DisplayName("Should initialize OrderService using two-argument constructor")
    void testTwoArgumentConstructor() {
        OrderService service = new OrderService(orderRepository, restaurantRepository);
        assertNotNull(service);
    }

    @Test
    @DisplayName("Should initialize OrderService using three-argument constructor")
    void testThreeArgumentConstructor() {
        OrderService service = new OrderService(orderRepository, restaurantRepository, notificationService);
        assertNotNull(service);
    }

    // ==========================================
    // 2. PLACE ORDER TESTS (VALIDATION & SUCCESS)
    // ==========================================

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw exception when delivery address is empty or blank")
    void testPlaceOrder_InvalidAddress(String address) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", address, Collections.emptyList())
        );
        assertEquals("Invalid delivery address", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when delivery address is null")
    void testPlaceOrder_NullAddress() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", null, Collections.emptyList())
        );
        assertEquals("Invalid delivery address", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when restaurant is not found")
    void testPlaceOrder_RestaurantNotFound() {
        when(restaurantRepository.findById("REST1")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", Collections.emptyList())
        );
        assertEquals("Restaurant not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when address is outside delivery zone")
    void testPlaceOrder_OutsideDeliveryZone() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Ramallah")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Ramallah", Collections.emptyList())
        );
        assertEquals("Restaurant does not deliver to this address", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when items list is null")
    void testPlaceOrder_NullItems() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", null)
        );
        assertEquals("Order items cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when items list is empty")
    void testPlaceOrder_EmptyItems() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", Collections.emptyList())
        );
        assertEquals("Order items cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when an order item is null")
    void testPlaceOrder_NullOrderItem() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);

        List<OrderItem> items = Collections.singletonList(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", items)
        );
        assertEquals("Order item cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when an item quantity is zero or negative")
    void testPlaceOrder_InvalidQuantity() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);

        OrderItem mockItem = mock(OrderItem.class);
        Menu mockMenuItem = mock(Menu.class);
        when(mockItem.getMenuItem()).thenReturn(mockMenuItem);
        when(mockItem.getQuantity()).thenReturn(0);

        List<OrderItem> items = Collections.singletonList(mockItem);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", items)
        );
        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when menu item is unavailable")
    void testPlaceOrder_UnavailableMenuItem() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);

        OrderItem mockItem = mock(OrderItem.class);
        Menu mockMenuItem = mock(Menu.class);
        when(mockItem.getMenuItem()).thenReturn(mockMenuItem);
        when(mockItem.getQuantity()).thenReturn(2);
        when(mockMenuItem.isAvailable()).thenReturn(false);
        when(mockMenuItem.getName()).thenReturn("Burger");

        List<OrderItem> items = Collections.singletonList(mockItem);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", items)
        );
        assertEquals("Item is unavailable: Burger", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when total amount is below minimum order value")
    void testPlaceOrder_BelowMinimumOrderValue() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);
        when(mockRestaurant.getMinimumOrderValue()).thenReturn(new BigDecimal("30.00"));

        OrderItem mockItem = mock(OrderItem.class);
        Menu mockMenuItem = mock(Menu.class);
        when(mockItem.getMenuItem()).thenReturn(mockMenuItem);
        when(mockItem.getQuantity()).thenReturn(1);
        when(mockMenuItem.isAvailable()).thenReturn(true);
        when(mockItem.getItemTotal()).thenReturn(new BigDecimal("20.00"));

        List<OrderItem> items = Collections.singletonList(mockItem);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", items)
        );
        assertEquals("Order total is below the minimum order value of 30.00", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully place order and register observers")
    void testPlaceOrder_Success() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);
        when(mockRestaurant.getMinimumOrderValue()).thenReturn(new BigDecimal("15.00"));

        OrderItem mockItem = mock(OrderItem.class);
        Menu mockMenuItem = mock(Menu.class);
        when(mockItem.getMenuItem()).thenReturn(mockMenuItem);
        when(mockItem.getQuantity()).thenReturn(2);
        when(mockMenuItem.isAvailable()).thenReturn(true);
        when(mockItem.getItemTotal()).thenReturn(new BigDecimal("20.00"));

        List<OrderItem> items = Collections.singletonList(mockItem);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.placeOrder("CUST1", "REST1", "Nablus Center", items);

        assertAll("Order Placement Success Verification",
            () -> assertNotNull(result, "Placed order should not be null"),
            () -> assertEquals(new BigDecimal("20.00"), result.getTotalPrice(), "Total price should match items total")
        );

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // ==========================================
    // 3. STATUS TRANSITION TESTS
    // ==========================================

    @Test
    @DisplayName("Should throw exception when order ID is not found during status update")
    void testUpdateOrderStatus_OrderNotFound() {
        when(orderRepository.findById("INVALID_ID")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus("INVALID_ID", "CONFIRMED")
        );
        assertEquals("Order not found", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw exception when new status is empty or blank")
    void testUpdateOrderStatus_InvalidNewStatus(String newStatus) {
        Order mockOrder = mock(Order.class);
        when(orderRepository.findById("ORD1")).thenReturn(mockOrder);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus("ORD1", newStatus)
        );
        assertEquals("Invalid order status", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when new status is null")
    void testUpdateOrderStatus_NullNewStatus() {
        Order mockOrder = mock(Order.class);
        when(orderRepository.findById("ORD1")).thenReturn(mockOrder);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus("ORD1", null)
        );
        assertEquals("Invalid order status", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "PENDING, CONFIRMED",
        "CONFIRMED, PREPARING",
        "PREPARING, READY",
        "READY, OUT_FOR_DELIVERY",
        "OUT_FOR_DELIVERY, DELIVERED"
    })
    @DisplayName("Should allow valid status transitions")
    void testUpdateOrderStatus_ValidTransitions(String currentStatus, String targetStatus) {
        Order order = new Order("ORD1", "CUST1", "REST1", Collections.emptyList(), BigDecimal.TEN);
        order.setStatus(currentStatus);

        when(orderRepository.findById("ORD1")).thenReturn(order);

        Order updatedOrder = orderService.updateOrderStatus("ORD1", targetStatus);

        assertEquals(targetStatus, updatedOrder.getStatus());
    }

    @ParameterizedTest
    @CsvSource({
        "PENDING, DELIVERED",
        "CONFIRMED, DELIVERED",
        "PREPARING, CONFIRMED",
        "DELIVERED, PENDING",
        "READY, PENDING"
    })
    @DisplayName("Should throw exception for invalid status transitions")
    void testUpdateOrderStatus_InvalidTransitions(String currentStatus, String targetStatus) {
        Order order = new Order("ORD1", "CUST1", "REST1", Collections.emptyList(), BigDecimal.TEN);
        order.setStatus(currentStatus);

        when(orderRepository.findById("ORD1")).thenReturn(order);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus("ORD1", targetStatus)
        );
        assertTrue(exception.getMessage().contains("Invalid status transition from " + currentStatus + " to " + targetStatus));
    }

    // ==========================================
    // 4. DELIVERY COST CALCULATION TESTS
    // ==========================================

    @Test
    @DisplayName("Should throw exception when calculating delivery cost for non-existent order")
    void testCalculateDeliveryCost_OrderNotFound() {
        when(orderRepository.findById("INVALID_ID")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.calculateDeliveryCost("INVALID_ID", 5.0)
        );
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should calculate delivery cost and set it on the order")
    void testCalculateDeliveryCost_Success() {
        Order order = new Order("ORD1", "CUST1", "REST1", Collections.emptyList(), new BigDecimal("50.00"));
        when(orderRepository.findById("ORD1")).thenReturn(order);

        BigDecimal fee = new BigDecimal("8.00");
        when(deliveryCostService.calculateDeliveryCost(10.0)).thenReturn(fee);

        BigDecimal resultFee = orderService.calculateDeliveryCost("ORD1", 10.0);

        assertAll("Delivery Cost Verification",
            () -> assertEquals(fee, resultFee, "Returned delivery fee should match calculated fee"),
            () -> assertEquals(fee, order.getDeliveryFee(), "Delivery fee should be updated on order"),
            () -> assertEquals(new BigDecimal("58.00"), order.getFinalPrice(), "Final price should reflect updated delivery fee")
        );
    }
}