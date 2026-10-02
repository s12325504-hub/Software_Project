package domain;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private String id;
    private String customerId;
    private String restaurantId;
    private List<OrderItem> items;
    private BigDecimal totalPrice;
    private String status; // الحالات: "PENDING", "PREPARING", "CANCELLED"

    public Order(String id, String customerId, String restaurantId, List<OrderItem> items, BigDecimal totalPrice) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = "PENDING"; // الحالة الافتراضية عند الإنشاء
    }

    public String getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // دالة تحقق من إمكانية الإلغاء
    public boolean canBeCancelled() {
        return "PENDING".equalsIgnoreCase(this.status);
    }
}