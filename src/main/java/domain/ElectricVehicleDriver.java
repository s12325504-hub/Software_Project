package domain;

public class ElectricVehicleDriver extends Driver {

    private double batteryRange;

    public ElectricVehicleDriver(
            String id,
            String name,
            boolean available,
            double distance,
            double speed,
            double batteryRange) {

        super(
                id,
                name,
                available,
                distance,
                speed);

        this.batteryRange = batteryRange;
    }

    public double getBatteryRange() {
        return batteryRange;
    }

    @Override
    public boolean canDeliver(
            double deliveryDistance,
            int orderSize) {

        return deliveryDistance <= batteryRange;
    }

    @Override
    public String getDriverType() {
        return "ElectricVehicleDriver (مركبة كهربائية)";
    }
}