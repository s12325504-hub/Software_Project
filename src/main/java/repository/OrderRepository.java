package repository;

import domain.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private List<Order> orders = new ArrayList<>();

    // حفظ الطلب
    public Order save(Order order) {
        if (order != null) {
            orders.add(order);
        }
        return order;
    }

    // إرجاع جميع الطلبات
    public List<Order> findAll() {
        return orders;
    }

    // البحث عن طلب حسب الـ ID
    public Order findById(String id) {

        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        // تنظيف النص المدخل من أي مسافات زائدة في البداية أو النهاية
        String cleanId = id.trim();

        for (Order order : orders) {
            if (order.getId() != null && order.getId().trim().equalsIgnoreCase(cleanId)) {
                return order;
            }
        }

        return null;
            }
}