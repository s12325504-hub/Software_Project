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

        this.expressFee = expressFee != null ? expressFee : BigDecimal.ZERO;

        // تحديث finalPrice الابتدائي ليشمل expressFee
        setFinalPrice(getFinalPrice().add(this.expressFee));
    }

    public BigDecimal getExpressFee() {
        return expressFee;
    }

    @Override
    public void setDeliveryFee(BigDecimal deliveryFee) {
        super.setDeliveryFee(deliveryFee);
        // عند تعيين رسوم التوصيل (10 شيكل)، يتم إضافة رسوم Express (5 شيكل) فوقها تلقائياً
        if (expressFee != null) {
            setFinalPrice(getFinalPrice().add(expressFee));
        }
    }
}