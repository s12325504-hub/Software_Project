package observer;

import domain.Order;
import service.NotificationService;

public class DriverNotificationObserver implements OrderObserver {

    private NotificationService notificationService;

    public DriverNotificationObserver(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @Override
    public void update(Order order) {

        if ("STATUS_CHANGED".equals(order.getLastEvent())
                && order.getAssignedDriver() != null) {

            String message =
                    "Driver "
                    + order.getAssignedDriver().getName()
                    + " notified: Order "
                    + order.getId()
                    + " status changed to "
                    + order.getStatus();

            notificationService.sendNotification(message);
        }
    }
}