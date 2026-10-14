package domain;

import java.math.BigDecimal;
import java.util.List;

public class ExpressOrder extends Order {

    private BigDecimal expressFee;

    public ExpressOrder(
            String id,
            String customerId,
            String restaurantId,
            List<OrderItem> items,
            BigDecimal totalPrice,
            BigDecimal expressFee) {

        super(
                id,
                customerId,
                restaurantId,
                items,
                totalPrice);

        this.expressFee = expressFee;

        setFinalPrice(
                getFinalPrice().add(expressFee));
    }

    public BigDecimal getExpressFee() {
        return expressFee;
    }
}