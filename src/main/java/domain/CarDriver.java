package domain;

public class CarDriver extends Driver {

    private int maxOrderSize;

    public CarDriver(
            String id,
            String name,
            boolean available,
            double distance,
            double speed,
            int maxOrderSize) {

        super(
                id,
                name,
                available,
                distance,
                speed);

        this.maxOrderSize = maxOrderSize;
    }

    public int getMaxOrderSize() {
        return maxOrderSize;
    }

    public boolean canDeliver(int orderSize) {
        return orderSize <= maxOrderSize;
    }
}