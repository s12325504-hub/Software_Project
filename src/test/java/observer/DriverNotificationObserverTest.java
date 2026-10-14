package observer;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import domain.Driver;
import domain.Order;
import service.NotificationService;

public class DriverNotificationObserverTest {

    @Test
    public void testDriverNotificationWhenOrderStatusChanges() {

        NotificationService notificationService =
                mock(NotificationService.class);

        DriverNotificationObserver observer =
                new DriverNotificationObserver(notificationService);

        Order order =
                new Order(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        Driver driver =
                new Driver(
                        "d1",
                        "Ahmad",
                        true,
                        3.0,
                        40.0);

        order.setAssignedDriver(driver);

        order.addObserver(observer);

        order.setStatus("READY");

        verify(notificationService, times(1))
                .sendNotification(
                        "Driver Ahmad notified: Order o1 status changed to READY");
    }
}