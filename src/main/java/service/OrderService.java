package service;

import domain.Order;
import domain.OrderItem;
import domain.Restaurant;
import repository.OrderRepository;
import repository.RestaurantRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(OrderRepository orderRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Order placeOrder(String customerId, String restaurantId, String deliveryAddress, List<OrderItem> items) {

        // 1. Invalid delivery addresses rejected (رفض العناوين الفارغة أو المحتوية على مسافات فقط)
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid delivery address");
        }

        // البحث عن المطعم للتحقق من وجوده وسياساته
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found");
        }

        // 2. Restaurants outside the delivery zone rejected (رفض العناوين خارج نطاق التوصيل)
        if (!restaurant.isWithinDeliveryZone(deliveryAddress)) {
            throw new IllegalArgumentException("Restaurant does not deliver to this address");
        }

        // فحوصات US2.2 (رفض الطلبات الفارغة)
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {
            if (item == null || item.getMenuItem() == null) {
                throw new IllegalArgumentException("Order item cannot be null");
            }

            // فحوصات US2.2 (رفض الكميات الصفرية والسالبة)
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }

            // فحوصات US2.2 (رفض الوجبات غير المتاحة)
            if (!item.getMenuItem().isAvailable()) {
                throw new IllegalArgumentException("Item is unavailable: " + item.getMenuItem().getName());
            }

            // حساب المجموع الكلي للطلب (US2.1)
            totalAmount = totalAmount.add(item.getItemTotal());
        }

        // 3. Orders below the minimum order value rejected (رفض الطلب إذا كان أقل من الحد الأدنى للمطعم)
        if (totalAmount.compareTo(restaurant.getMinimumOrderValue()) < 0) {
            throw new IllegalArgumentException("Order total is below the minimum order value of " + restaurant.getMinimumOrderValue());
        }

        // إنشاء الطلب بحالة PENDING أولية وحفظه في الـ Repository عند نجاح كل الفحوصات (US2.1)
        String orderId = UUID.randomUUID().toString();
        Order newOrder = new Order(orderId, customerId, restaurantId, items, totalAmount);

        return orderRepository.save(newOrder);
    }
}