package repository;

import domain.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private List<Order> orders = new ArrayList<>();

    // شرط US2.1: حفظ السجل في الذاكرة
    public Order save(Order order) {
        orders.add(order);
        return order;
    }

    public List<Order> findAll() {
        return orders;
    }
}