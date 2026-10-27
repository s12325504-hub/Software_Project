package service;

import java.math.BigDecimal;

public class DeliveryCostService {

    // دالة حساب رسوم التوصيل الأساسية بناءً على المسافة (المسافة * 2 شيكل)
    public BigDecimal calculateDeliveryCost(double distance) {
        // الشرط المفقود الذي يمنع المسافات السالبة ويرمي الاستثناء المطلوب
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        
        return BigDecimal.valueOf(distance * 2.0);
    }

    // حساب المسافة لكل منطقة ثم تطبيق معادلة الضرب (distance * 2.0)
    public BigDecimal calculateDeliveryCostByAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return calculateDeliveryCost(2.5); // قيمة افتراضية باقي المناطق (5.00 شيكل)
        }

        String lowerAddress = address.toLowerCase();
        double distance;

        if (lowerAddress.contains("nablus")) {
            distance = 5.0; // 5 كم * 2.0 = 10.00 شيكل
        } else if (lowerAddress.contains("asira")) { 
            distance = 4.0; // 4 كم * 2.0 = 8.00 شيكل
        } else if (lowerAddress.contains("rafidia")) {
            distance = 3.5; // 3.5 كم * 2.0 = 7.00 شيكل
        } else if (lowerAddress.contains("old city")) {
            distance = 3.0; // 3 كم * 2.0 = 6.00 شيكل
        } else {
            distance = 2.5; // 2.5 كم * 2.0 = 5.00 شيكل
        }

        return calculateDeliveryCost(distance);
    }
}