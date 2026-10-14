package observer;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import domain.Driver;
import domain.Order;
import service.NotificationService;

public class CustomerNotificationObserverTest {

    @Test
    public void testCustomerNotificationWhenOrderStatusChanges() {

        NotificationService notificationService =
                mock(NotificationService.class);

        CustomerNotificationObserver observer =
                new CustomerNotificationObserver(notificationService);

        Order order =
                new Order(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        order.addObserver(observer);

        order.setStatus("CONFIRMED");

        verify(notificationService, times(1))
                .sendNotification(
                        "Customer notified: Order o1 status changed to CONFIRMED");
    }

    @Test
    public void testCustomerNotifiedWhenDriverAssigned() {

        NotificationService notificationService =
                mock(NotificationService.class);

        CustomerNotificationObserver observer =
                new CustomerNotificationObserver(notificationService);

        Order order =
                new Order(
                        "o2",
                        "c1",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        order.addObserver(observer);

        Driver driver =
                new Driver(
                        "d1",
                        "Ahmad",
                        true,
                        3.0,
                        40.0);

        order.setAssignedDriver(driver);

        verify(notificationService, times(1))
                .sendNotification(
                        "Customer notified: Driver Ahmad assigned to Order o2");
    }

    @Test
    public void testCustomerNotifiedWhenOrderOutForDelivery() {

        NotificationService notificationService =
                mock(NotificationService.class);

        CustomerNotificationObserver observer =
                new CustomerNotificationObserver(notificationService);

        Order order =
                new Order(
                        "o3",
                        "c1",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        order.addObserver(observer);

        order.setStatus("OUT_FOR_DELIVERY");

        verify(notificationService, times(1))
                .sendNotification(
                        "Customer notified: Order o3 is out for delivery");
    }

    @Test
    public void testCustomerNotifiedWhenOrderDelivered() {

        NotificationService notificationService =
                mock(NotificationService.class);

        CustomerNotificationObserver observer =
                new CustomerNotificationObserver(notificationService);

        Order order =
                new Order(
                        "o4",
                        "c1",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        order.addObserver(observer);

        order.setStatus("DELIVERED");

        verify(notificationService, times(1))
                .sendNotification(
                        "Customer notified: Order o4 delivered");
    }
}