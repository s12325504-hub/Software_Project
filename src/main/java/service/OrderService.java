
package service;

import domain.ExpressOrder;
import domain.Order;
import domain.OrderItem;
import domain.Restaurant;
import domain.ScheduledOrder;
import observer.CustomerNotificationObserver;
import observer.DriverNotificationObserver;
import observer.RestaurantNotificationObserver;
import repository.OrderRepository;
import repository.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Provides order placement, status updates, and delivery cost calculation.
 * Supports regular, express, and scheduled orders, as well as notifications.
 */
public class OrderService {

    /** Stores and retrieves orders. */
    private final OrderRepository orderRepository;

    /** Retrieves restaurants and their delivery rules. */
    private final RestaurantRepository restaurantRepository;

    /** Provides notifications to registered order observers. */
    private final NotificationService notificationService;

    /** Calculates order delivery fees. */
    private final DeliveryCostService deliveryCostService;

    /** Sends order confirmation and status update emails. */
    private final EmailService emailService;

    /**
     * Creates a service using default notification and delivery cost services.
     *
     * @param orderRepository order storage
     * @param restaurantRepository restaurant lookup
     */
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository) {

        this(orderRepository,
             restaurantRepository,
             new ConsoleNotificationService(),
             new DeliveryCostService());
    }

    /**
     * Creates a service with a custom notification service.
     *
     * @param orderRepository order storage
     * @param restaurantRepository restaurant lookup
     * @param notificationService notification implementation
     */
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        NotificationService notificationService) {

        this(orderRepository,
             restaurantRepository,
             notificationService,
             new DeliveryCostService());
    }

    /**
     * Creates a service with custom notification and delivery cost services.
     *
     * @param orderRepository order storage
     * @param restaurantRepository restaurant lookup
     * @param notificationService notification implementation
     * @param deliveryCostService delivery fee calculator
     */
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        NotificationService notificationService,
                        DeliveryCostService deliveryCostService) {

        this(orderRepository,
             restaurantRepository,
             notificationService,
             deliveryCostService,
             new EmailService());
    }

    /**
     * Creates a service with all required dependencies supplied.
     *
     * @param orderRepository order storage
     * @param restaurantRepository restaurant lookup
     * @param notificationService notification implementation
     * @param deliveryCostService delivery fee calculator
     * @param emailService email sender
     */
    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        NotificationService notificationService,
                        DeliveryCostService deliveryCostService,
                        EmailService emailService) {

        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.notificationService = notificationService;
        this.deliveryCostService = deliveryCostService;
        this.emailService = emailService;
    }

    /**
     * Places a regular order using the default order type.
     *
     * @param customerId ID of the customer
     * @param restaurantId ID of the restaurant
     * @param deliveryAddress delivery destination
     * @param items items to order
     * @return the saved order
     * @throws IllegalArgumentException if the order data is invalid
     */
    public Order placeOrder(String customerId,
                            String restaurantId,
                            String deliveryAddress,
                            List<OrderItem> items) {

        return placeOrder(customerId, restaurantId, deliveryAddress, items, "1", null);
    }

    /**
     * Places an order of the requested type without an explicit scheduled time.
     *
     * @param customerId ID of the customer
     * @param restaurantId ID of the restaurant
     * @param deliveryAddress delivery destination
     * @param items items to order
     * @param orderType regular, express, or scheduled order selection
     * @return the saved order
     * @throws IllegalArgumentException if the order data is invalid
     */
    public Order placeOrder(String customerId,
                            String restaurantId,
                            String deliveryAddress,
                            List<OrderItem> items,
                            String orderType) {

        return placeOrder(customerId, restaurantId, deliveryAddress, items, orderType, null);
    }

    /**
     * Validates and saves a regular, express, or scheduled order and sends notifications.
     *
     * @param customerId ID of the customer
     * @param restaurantId ID of the restaurant
     * @param deliveryAddress delivery destination
     * @param items items to order
     * @param orderType regular, express, or scheduled order selection
     * @param scheduledTime requested delivery time for a scheduled order; may be null
     * @return the saved order
     * @throws IllegalArgumentException if validation fails
     */
    public Order placeOrder(String customerId,
                            String restaurantId,
                            String deliveryAddress,
                            List<OrderItem> items,
                            String orderType,
                            LocalDateTime scheduledTime) {

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

        Order newOrder;
        String type = (orderType != null) ? orderType.trim() : "1";

        // Instantiates the order object according to the selected option
        if ("2".equals(type) || "EXPRESS".equalsIgnoreCase(type)) {
            newOrder = new ExpressOrder(
                    orderId,
                    customerId,
                    restaurantId,
                    items,
                    totalAmount,
                    new BigDecimal("5.00")
            );
        } else if ("3".equals(type) || "SCHEDULED".equalsIgnoreCase(type)) {
            LocalDateTime deliveryTime = (scheduledTime != null) ? scheduledTime : LocalDateTime.now().plusHours(2);
            newOrder = new ScheduledOrder(
                    orderId,
                    customerId,
                    restaurantId,
                    items,
                    totalAmount,
                    deliveryTime
            );
        } else {
            newOrder = new Order(
                    orderId,
                    customerId,
                    restaurantId,
                    items,
                    totalAmount
            );
        }

        // Calculate delivery fee based on address
        BigDecimal deliveryFee = deliveryCostService.calculateDeliveryCostByAddress(deliveryAddress);
        newOrder.setDeliveryFee(deliveryFee);

        newOrder.addObserver(
                new CustomerNotificationObserver(notificationService));

        newOrder.addObserver(
                new DriverNotificationObserver(notificationService));

        newOrder.addObserver(
                new RestaurantNotificationObserver(notificationService));

        Order savedOrder = orderRepository.save(newOrder);

        // Send email notification upon successful order creation
        try {
            String subject = "Order Confirmation #" + savedOrder.getId();
            String body = "Hello, your order has been placed successfully!\n\n"
                    + "Items Total: $" + savedOrder.getTotalPrice() + "\n"
                    + "Delivery Fee: $" + savedOrder.getDeliveryFee() + "\n"
                    + "Grand Total: $" + savedOrder.getFinalPrice() + "\n\n"
                    + "Thank you for ordering with us!";

            emailService.sendEmail("talasaadeh775@gmail.com", subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        return savedOrder;
    }

    /**
     * Moves an order to the next permitted status and attempts an email notification.
     *
     * @param orderId ID of the order
     * @param newStatus requested new status
     * @return the updated order
     * @throws IllegalArgumentException if the order or status is invalid
     * @throws IllegalStateException if the transition is not allowed
     */
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

        // Send email notification upon order status update
        try {
            String subject = "Order Status Update #" + order.getId();
            String body = "Dear Customer, your order status has been updated to: " + status;

            emailService.sendEmail("talasaadeh775@gmail.com", subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send status update email: " + e.getMessage());
        }

        return order;
    }

    /**
     * Checks whether the requested status follows the allowed order lifecycle.
     *
     * @param currentStatus current order status
     * @param newStatus proposed next status
     * @return true if the transition is allowed; otherwise false
     */
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

    /**
     * Calculates and assigns the delivery fee for an existing order.
     *
     * @param orderId ID of the order
     * @param distance delivery distance used by the calculator
     * @return the calculated delivery fee
     * @throws IllegalArgumentException if the order does not exist
     */
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
