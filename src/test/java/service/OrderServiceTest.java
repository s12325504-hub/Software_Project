package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
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
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", address, Collections.emptyList())
        );
    }

    @Test
    @DisplayName("Should throw exception when delivery address is null")
    void testPlaceOrder_NullAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", null, Collections.emptyList())
        );
    }

    @Test
    @DisplayName("Should throw exception when restaurant is not found")
    void testPlaceOrder_RestaurantNotFound() {
        when(restaurantRepository.findById("REST1")).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", Collections.emptyList())
        );
    }

    @Test
    @DisplayName("Should throw exception when address is outside delivery zone")
    void testPlaceOrder_OutsideDeliveryZone() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Ramallah")).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Ramallah", Collections.emptyList())
        );
    }

    @Test
    @DisplayName("Should throw exception when items list is null")
    void testPlaceOrder_NullItems() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", null)
        );
    }

    @Test
    @DisplayName("Should throw exception when items list is empty")
    void testPlaceOrder_EmptyItems() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", Collections.emptyList())
        );
    }

    @Test
    @DisplayName("Should throw exception when an order item is null")
    void testPlaceOrder_NullOrderItem() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);

        List<OrderItem> items = Collections.singletonList(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", items)
        );
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

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", items)
        );
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

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", items)
        );
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

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder("CUST1", "REST1", "Nablus Center", items)
        );
    }

    @Test
    @DisplayName("Should successfully place order and register observers")
    void testPlaceOrder_Success() {
        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantRepository.findById("REST1")).thenReturn(mockRestaurant);
        when(mockRestaurant.isWithinDeliveryZone("Nablus Center")).thenReturn(true);
        when(mockRestaurant.getMinimumOrderValue()).thenReturn(new BigDecimal("15.00"));

        // التعديل المضاف: منع الـ NullPointerException بإرجاع قيمة غير فارغة لرسوم التوصيل
        when(deliveryCostService.calculateDeliveryCost(anyDouble())).thenReturn(new BigDecimal("5.00"));
        when(deliveryCostService.calculateDeliveryCostByAddress(anyString())).thenReturn(new BigDecimal("5.00"));

        OrderItem mockItem = mock(OrderItem.class);
        Menu mockMenuItem = mock(Menu.class);
        when(mockItem.getMenuItem()).thenReturn(mockMenuItem);
        when(mockItem.getQuantity()).thenReturn(2);
        when(mockMenuItem.isAvailable()).thenReturn(true);
        when(mockItem.getItemTotal()).thenReturn(new BigDecimal("20.00"));

        List<OrderItem> items = Collections.singletonList(mockItem);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.placeOrder("CUST1", "REST1", "Nablus Center", items);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // ==========================================
    // 3. STATUS TRANSITION TESTS
    // ==========================================

    @Test
    @DisplayName("Should throw exception when order ID is not found during status update")
    void testUpdateOrderStatus_OrderNotFound() {
        when(orderRepository.findById("INVALID_ID")).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus("INVALID_ID", "CONFIRMED")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw exception when new status is empty or blank")
    void testUpdateOrderStatus_InvalidNewStatus(String newStatus) {
        Order mockOrder = mock(Order.class);
        when(orderRepository.findById("ORD1")).thenReturn(mockOrder);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus("ORD1", newStatus)
        );
    }

    @Test
    @DisplayName("Should throw exception when new status is null")
    void testUpdateOrderStatus_NullNewStatus() {
        Order mockOrder = mock(Order.class);
        when(orderRepository.findById("ORD1")).thenReturn(mockOrder);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus("ORD1", null)
        );
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

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus("ORD1", targetStatus)
        );
    }

    // ==========================================
    // 4. DELIVERY COST CALCULATION TESTS
    // ==========================================

    @Test
    @DisplayName("Should throw exception when calculating delivery cost for non-existent order")
    void testCalculateDeliveryCost_OrderNotFound() {
        when(orderRepository.findById("INVALID_ID")).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.calculateDeliveryCost("INVALID_ID", 5.0)
        );
    }

    @Test
    @DisplayName("Should calculate delivery cost and set it on the order")
    void testCalculateDeliveryCost_Success() {
        Order order = new Order("ORD1", "CUST1", "REST1", Collections.emptyList(), new BigDecimal("50.00"));
        when(orderRepository.findById("ORD1")).thenReturn(order);

        BigDecimal fee = new BigDecimal("8.00");
        when(deliveryCostService.calculateDeliveryCost(10.0)).thenReturn(fee);

        BigDecimal resultFee = orderService.calculateDeliveryCost("ORD1", 10.0);

        assertNotNull(resultFee);
        assertEquals(0, fee.compareTo(resultFee));
    }
}