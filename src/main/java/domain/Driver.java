package domain;

public class Driver {

    private String id;
    private String name;
    private boolean available;
    private String status;
    private double distance;
    private double speed;

    public Driver(
            String id,
            String name,
            boolean available,
            double distance,
            double speed) {

        this.id = id;
        this.name = name;
        this.available = available;
        this.distance = distance;
        this.speed = speed;

        this.status =
                available ? "AVAILABLE" : "BUSY";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getStatus() {
        return status;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean canDeliver(
            double deliveryDistance,
            int orderSize) {

        return true;
    }
}