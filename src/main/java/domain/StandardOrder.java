package domain;

import java.math.BigDecimal;
import java.util.List;

public class StandardOrder extends Order {

    public StandardOrder(
            String id,
            String customerId,
            String restaurantId,
            List<OrderItem> items,
            BigDecimal totalPrice) {

        super(
                id,
                customerId,
                restaurantId,
                items,
                totalPrice);
    }
}