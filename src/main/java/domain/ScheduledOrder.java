package domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import service.DateTimeService;
import service.SystemDateTimeService;

public class ScheduledOrder extends Order {

    private LocalDateTime scheduledDeliveryTime;

    public ScheduledOrder(
            String id,
            String customerId,
            String restaurantId,
            List<OrderItem> items,
            BigDecimal totalPrice,
            LocalDateTime scheduledDeliveryTime) {

        this(
                id,
                customerId,
                restaurantId,
                items,
                totalPrice,
                scheduledDeliveryTime,
                new SystemDateTimeService());
    }
    public ScheduledOrder(
            String id,
            String customerId,
            String restaurantId,
            List<OrderItem> items,
            BigDecimal totalPrice) {

        this(
                id,
                customerId,
                restaurantId,
                items,
                totalPrice,
                LocalDateTime.now().plusHours(2), // وقت افتراضي مستقبلي
                new SystemDateTimeService());
    }

    public ScheduledOrder(
            String id,
            String customerId,
            String restaurantId,
            List<OrderItem> items,
            BigDecimal totalPrice,
            LocalDateTime scheduledDeliveryTime,
            DateTimeService dateTimeService) {

        super(
                id,
                customerId,
                restaurantId,
                items,
                totalPrice);

        if (scheduledDeliveryTime == null) {
            throw new IllegalArgumentException(
                    "Scheduled delivery time cannot be null");
        }

        if (!scheduledDeliveryTime.isAfter(
                dateTimeService.now())) {

            throw new IllegalArgumentException(
                    "Scheduled delivery time must be in the future");
        }

        this.scheduledDeliveryTime =
                scheduledDeliveryTime;
    }

    public LocalDateTime getScheduledDeliveryTime() {
        return scheduledDeliveryTime;
    }
}