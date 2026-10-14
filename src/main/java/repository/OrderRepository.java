package repository;

import domain.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private List<Order> orders = new ArrayList<>();

    // حفظ الطلب
    public Order save(Order order) {
        orders.add(order);
        return order;
    }

    // إرجاع جميع الطلبات
    public List<Order> findAll() {
        return orders;
    }

    // البحث عن طلب حسب الـ ID
    public Order findById(String id) {

        if (id == null) {
            return null;
        }

        for (Order order : orders) {
            if (order.getId().equals(id)) {
                return order;
            }
        }

        return null;
    }
}