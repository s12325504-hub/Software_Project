package domain;

public class BicycleDriver extends Driver {

    private double maxDeliveryDistance;

    public BicycleDriver(
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

    @Override
    public boolean canDeliver(
            double deliveryDistance,
            int orderSize) {

        return deliveryDistance <= maxDeliveryDistance;
    }
    @Override
    public String getDriverType() {
        return "MotorcycleDriver (دراجة نارية)";
    }
}