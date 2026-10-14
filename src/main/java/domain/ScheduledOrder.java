package domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduledOrder extends Order {

    private LocalDateTime scheduledDeliveryTime;

    public ScheduledOrder(
            String id,
            String customerId,
            String restaurantId,
            List<OrderItem> items,
            BigDecimal totalPrice,
            LocalDateTime scheduledDeliveryTime) {

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

        if (!scheduledDeliveryTime.isAfter(LocalDateTime.now())) {
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