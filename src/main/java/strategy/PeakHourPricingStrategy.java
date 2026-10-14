package strategy;

import java.math.BigDecimal;

public class PeakHourPricingStrategy implements PricingStrategy {

    private BigDecimal percentage;

    public PeakHourPricingStrategy(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {

        BigDecimal increase =
                basePrice.multiply(percentage)
                         .divide(new BigDecimal("100"));

        return basePrice.add(increase);
    }
}