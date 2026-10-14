package strategy;

import domain.Driver;
import java.util.List;

public class FastestDriverStrategy implements DriverSelectionStrategy {

    @Override
    public Driver selectDriver(List<Driver> availableDrivers) {

        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return null;
        }

        Driver fastestDriver = availableDrivers.get(0);

        for (Driver driver : availableDrivers) {
            if (driver.getSpeed() > fastestDriver.getSpeed()) {
                fastestDriver = driver;
            }
        }

        return fastestDriver;
    }
}