package domain;

import java.math.BigDecimal;

public class Restaurant {
    private String id;
    private String name;
    private boolean open;
    private BigDecimal minimumOrderValue;
    private String deliveryZone; // اسم المنطقة المسموح بها، مثل: "Nablus Center"

    public Restaurant(String id, String name, boolean open, BigDecimal minimumOrderValue, String deliveryZone) {
        this.id = id;
        this.name = name;
        this.open = open;
        this.minimumOrderValue = minimumOrderValue != null ? minimumOrderValue : BigDecimal.ZERO;
        this.deliveryZone = deliveryZone != null ? deliveryZone : "";
    }

    // الـ Getters التي تحل الأخطاء لدى RestaurantRepository و OrderService
    public String getId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    public boolean isOpen() { 
        return open; 
    }

    public BigDecimal getMinimumOrderValue() { 
        return minimumOrderValue; 
    }

    public String getDeliveryZone() { 
        return deliveryZone; 
    }

    // دالة فحص هل العنوان المكتوب يطابق منطقة التوصيل
    public boolean isWithinDeliveryZone(String address) {
        if (address == null || deliveryZone.isEmpty()) {
            return false;
        }
        return address.toLowerCase().contains(deliveryZone.toLowerCase());
    }
}