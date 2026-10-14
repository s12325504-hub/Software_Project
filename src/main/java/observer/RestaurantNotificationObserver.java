package observer;

import domain.Order;
import service.NotificationService;

public class RestaurantNotificationObserver implements OrderObserver {

    private NotificationService notificationService;

    public RestaurantNotificationObserver(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @Override
    public void update(Order order) {

        if ("STATUS_CHANGED".equals(order.getLastEvent())) {

            String message =
                    "Restaurant notified: Order "
                    + order.getId()
                    + " status changed to "
                    + order.getStatus();

            notificationService.sendNotification(message);
        }
    }
}