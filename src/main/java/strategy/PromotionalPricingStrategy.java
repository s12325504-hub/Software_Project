package strategy;

import java.math.BigDecimal;

public class PromotionalPricingStrategy implements PricingStrategy {

    private BigDecimal discountPercentage;

    public PromotionalPricingStrategy(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {

        BigDecimal discount =
                basePrice.multiply(discountPercentage)
                         .divide(new BigDecimal("100"));

        return basePrice.subtract(discount);
    }
}