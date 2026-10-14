package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import domain.Order;
import repository.OrderRepository;
import repository.RestaurantRepository;

public class OrderServiceStatusTest {

    @Test
    public void testValidStatusTransition() {

        OrderRepository orderRepository =
                new OrderRepository();

        RestaurantRepository restaurantRepository =
                new RestaurantRepository();

        OrderService orderService =
                new OrderService(
                        orderRepository,
                        restaurantRepository);

        Order order =
                new Order(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        orderRepository.save(order);

        orderService.updateOrderStatus(
                "o1",
                "CONFIRMED");

        assertEquals(
                "CONFIRMED",
                order.getStatus());
    }

    @Test
    public void testInvalidStatusTransitionRejected() {

        OrderRepository orderRepository =
                new OrderRepository();

        RestaurantRepository restaurantRepository =
                new RestaurantRepository();

        OrderService orderService =
                new OrderService(
                        orderRepository,
                        restaurantRepository);

        Order order =
                new Order(
                        "o2",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        orderRepository.save(order);

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(
                        "o2",
                        "READY"));
    }

    @Test
    public void testDeliveredOrderCannotBeModified() {

        OrderRepository orderRepository =
                new OrderRepository();

        RestaurantRepository restaurantRepository =
                new RestaurantRepository();

        OrderService orderService =
                new OrderService(
                        orderRepository,
                        restaurantRepository);

        Order order =
                new Order(
                        "o3",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        orderRepository.save(order);

        orderService.updateOrderStatus(
                "o3",
                "CONFIRMED");

        orderService.updateOrderStatus(
                "o3",
                "PREPARING");

        orderService.updateOrderStatus(
                "o3",
                "READY");

        orderService.updateOrderStatus(
                "o3",
                "OUT_FOR_DELIVERY");

        orderService.updateOrderStatus(
                "o3",
                "DELIVERED");

        assertEquals(
                "DELIVERED",
                order.getStatus());

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(
                        "o3",
                        "CONFIRMED"));
    }
}