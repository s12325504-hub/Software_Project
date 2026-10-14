package service;

import domain.Order;
import domain.OrderItem;
import domain.Restaurant;
import observer.CustomerNotificationObserver;
import observer.DriverNotificationObserver;
import observer.RestaurantNotificationObserver;
import repository.OrderRepository;
import repository.RestaurantRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final NotificationService notificationService;
    private final DeliveryCostService deliveryCostService;

    // يبقي استخدام الكود القديم شغال
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository) {

        this(orderRepository,
             restaurantRepository,
             new ConsoleNotificationService(),
             new DeliveryCostService());
    }

    // مفيد لاختبارات Notification باستخدام Mockito
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        NotificationService notificationService) {

        this(orderRepository,
             restaurantRepository,
             notificationService,
             new DeliveryCostService());
    }

    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        NotificationService notificationService,
                        DeliveryCostService deliveryCostService) {

        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.notificationService = notificationService;
        this.deliveryCostService = deliveryCostService;
    }

    public Order placeOrder(String customerId,
                            String restaurantId,
                            String deliveryAddress,
                            List<OrderItem> items) {

        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid delivery address");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId);

        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found");
        }

        if (!restaurant.isWithinDeliveryZone(deliveryAddress)) {
            throw new IllegalArgumentException(
                    "Restaurant does not deliver to this address");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order items cannot be empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {

            if (item == null || item.getMenuItem() == null) {
                throw new IllegalArgumentException(
                        "Order item cannot be null");
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero");
            }

            if (!item.getMenuItem().isAvailable()) {
                throw new IllegalArgumentException(
                        "Item is unavailable: "
                        + item.getMenuItem().getName());
            }

            totalAmount = totalAmount.add(item.getItemTotal());
        }

        if (totalAmount.compareTo(
                restaurant.getMinimumOrderValue()) < 0) {

            throw new IllegalArgumentException(
                    "Order total is below the minimum order value of "
                    + restaurant.getMinimumOrderValue());
        }

        String orderId = UUID.randomUUID().toString();

        Order newOrder = new Order(
                orderId,
                customerId,
                restaurantId,
                items,
                totalAmount
        );

        newOrder.addObserver(
                new CustomerNotificationObserver(notificationService));

        newOrder.addObserver(
                new DriverNotificationObserver(notificationService));

        newOrder.addObserver(
                new RestaurantNotificationObserver(notificationService));

        return orderRepository.save(newOrder);
    }

    public Order updateOrderStatus(String orderId, String newStatus) {

        Order order = orderRepository.findById(orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid order status");
        }

        String currentStatus = order.getStatus();
        String status = newStatus.toUpperCase();

        if (!isValidStatusTransition(currentStatus, status)) {
            throw new IllegalStateException(
                    "Invalid status transition from "
                    + currentStatus
                    + " to "
                    + status);
        }

        order.setStatus(status);

        return order;
    }

    private boolean isValidStatusTransition(String currentStatus,
                                            String newStatus) {

        if ("PENDING".equals(currentStatus)) {
            return "CONFIRMED".equals(newStatus);
        }

        if ("CONFIRMED".equals(currentStatus)) {
            return "PREPARING".equals(newStatus);
        }

        if ("PREPARING".equals(currentStatus)) {
            return "READY".equals(newStatus);
        }

        if ("READY".equals(currentStatus)) {
            return "OUT_FOR_DELIVERY".equals(newStatus);
        }

        if ("OUT_FOR_DELIVERY".equals(currentStatus)) {
            return "DELIVERED".equals(newStatus);
        }

        return false;
    }

    // حساب تكلفة التوصيل وإضافتها للطلب
    public BigDecimal calculateDeliveryCost(String orderId,
                                            double distance) {

        Order order = orderRepository.findById(orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        BigDecimal deliveryFee =
                deliveryCostService.calculateDeliveryCost(distance);

        order.setDeliveryFee(deliveryFee);

        return deliveryFee;
    }
}