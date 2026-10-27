package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String id;
    private String name;
    private boolean open;
    private BigDecimal minimumOrderValue;
    private List<String> deliveryZones; // قائمة المناطق المسموح بها، مثل: ["Nablus", "Rafidia", "Asira", "Old City"]

    // Constructor يستقبل قائمة المناطق (دعم أكثر من منطقة للتوصيل)
    public Restaurant(String id, String name, boolean open, BigDecimal minimumOrderValue, List<String> deliveryZones) {
        this.id = id;
        this.name = name;
        this.open = open;
        this.minimumOrderValue = minimumOrderValue != null ? minimumOrderValue : BigDecimal.ZERO;
        this.deliveryZones = deliveryZones != null ? deliveryZones : new ArrayList<>();
    }

    // Constructor قديم لضمان عدم توقف أي كود سابق يعتمد على تمرير منطقة واحدة String
    public Restaurant(String id, String name, boolean open, BigDecimal minimumOrderValue, String deliveryZone) {
        this.id = id;
        this.name = name;
        this.open = open;
        this.minimumOrderValue = minimumOrderValue != null ? minimumOrderValue : BigDecimal.ZERO;
        this.deliveryZones = new ArrayList<>();
        if (deliveryZone != null && !deliveryZone.trim().isEmpty()) {
            this.deliveryZones.add(deliveryZone);
        }
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

    public List<String> getDeliveryZones() { 
        return deliveryZones; 
    }

    // إرجاع أول منطقة أو نص مجمع لدعم الكود القديم الذي يستدعي getDeliveryZone()
    public String getDeliveryZone() { 
        if (deliveryZones == null || deliveryZones.isEmpty()) {
            return "";
        }
        return String.join(", ", deliveryZones);
    }

    // دالة فحص هل العنوان المكتوب يطابق إحدى مناطق التوصيل المتاحة
    public boolean isWithinDeliveryZone(String address) {
        if (address == null || deliveryZones == null || deliveryZones.isEmpty()) {
            return false;
        }
        
        for (String zone : deliveryZones) {
            if (address.toLowerCase().contains(zone.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}