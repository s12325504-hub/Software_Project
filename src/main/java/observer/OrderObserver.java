package observer;

import domain.Order;

public interface OrderObserver {

    void update(Order order);
}