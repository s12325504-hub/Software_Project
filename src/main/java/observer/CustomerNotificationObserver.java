package observer;

import domain.Order;
import service.NotificationService;

public class CustomerNotificationObserver implements OrderObserver {

    private NotificationService notificationService;

    public CustomerNotificationObserver(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @Override
    public void update(Order order) {

        String message;

        if ("DRIVER_ASSIGNED".equals(order.getLastEvent())
                && order.getAssignedDriver() != null) {

            message =
                    "Customer notified: Driver "
                    + order.getAssignedDriver().getName()
                    + " assigned to Order "
                    + order.getId();

        } else if ("OUT_FOR_DELIVERY".equals(order.getStatus())) {

            message =
                    "Customer notified: Order "
                    + order.getId()
                    + " is out for delivery";

        } else if ("DELIVERED".equals(order.getStatus())) {

            message =
                    "Customer notified: Order "
                    + order.getId()
                    + " delivered";

        } else {

            message =
                    "Customer notified: Order "
                    + order.getId()
                    + " status changed to "
                    + order.getStatus();
        }

        notificationService.sendNotification(message);
    }
}