package observer;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import domain.Order;
import service.NotificationService;

public class RestaurantNotificationObserverTest {

    @Test
    public void testRestaurantNotificationWhenOrderStatusChanges() {

        NotificationService notificationService =
                mock(NotificationService.class);

        RestaurantNotificationObserver observer =
                new RestaurantNotificationObserver(notificationService);

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
                        "Restaurant notified: Order o1 status changed to CONFIRMED");
    }
}