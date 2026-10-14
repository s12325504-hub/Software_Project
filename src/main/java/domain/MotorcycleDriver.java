package domain;

public class MotorcycleDriver extends Driver {

    private double maxDeliveryDistance;

    public MotorcycleDriver(
            String id,
            String name,
            boolean available,
            double distance,
            double speed,
            double maxDeliveryDistance) {

        super(
                id,
                name,
                available,
                distance,
                speed);

        this.maxDeliveryDistance = maxDeliveryDistance;
    }

    public double getMaxDeliveryDistance() {
        return maxDeliveryDistance;
    }

    public boolean canDeliver(double deliveryDistance) {
        return deliveryDistance <= maxDeliveryDistance;
    }
}