package service;

import java.util.ArrayList;
import java.util.List;

import domain.Driver;
import domain.Order;
import repository.DriverRepository;
import strategy.DriverSelectionStrategy;

/**
 * Service responsible for assigning delivery drivers to orders.
 * It uses a driver selection strategy to choose an available driver.
 */
public class DriverService {

    private DriverRepository driverRepository;
    private DriverSelectionStrategy selectionStrategy;

    /**
     * Creates a DriverService.
     *
     * @param driverRepository repository that stores drivers
     * @param selectionStrategy strategy used to select a driver
     */
    public DriverService(
            DriverRepository driverRepository,
            DriverSelectionStrategy selectionStrategy) {

        this.driverRepository = driverRepository;
        this.selectionStrategy = selectionStrategy;
    }

    /**
     * Assigns an available driver to an order.
     *
     * @param order the order that needs a driver
     * @return the selected driver
     * @throws IllegalArgumentException if the order is null
     * @throws IllegalStateException if no driver is available
     */
    public Driver assignDriver(Order order) {

        if (order == null) {
            throw new IllegalArgumentException(
                    "Order cannot be null");
        }

        List<Driver> availableDrivers =
                driverRepository.findAvailableDrivers();

        Driver selectedDriver =
                selectionStrategy.selectDriver(
                        availableDrivers);

        if (selectedDriver == null) {
            throw new IllegalStateException(
                    "No available driver");
        }

        selectedDriver.setAvailable(false);
        selectedDriver.setStatus("BUSY");

        order.setAssignedDriver(selectedDriver);

        return selectedDriver;
    }

    /**
     * Assigns an eligible driver according to driver type rules.
     *
     * @param order the order that needs a driver
     * @param deliveryDistance delivery distance
     * @param orderSize number of items in the order
     * @return the selected eligible driver
     */
    public Driver assignDriver(
            Order order,
            double deliveryDistance,
            int orderSize) {

        if (order == null) {
            throw new IllegalArgumentException(
                    "Order cannot be null");
        }

        List<Driver> availableDrivers =
                driverRepository.findAvailableDrivers();

        List<Driver> eligibleDrivers =
                new ArrayList<>();

        for (Driver driver : availableDrivers) {

            if (driver.canDeliver(
                    deliveryDistance,
                    orderSize)) {

                eligibleDrivers.add(driver);
            }
        }

        Driver selectedDriver =
                selectionStrategy.selectDriver(
                        eligibleDrivers);

        if (selectedDriver == null) {
            throw new IllegalStateException(
                    "No eligible driver available");
        }

        selectedDriver.setAvailable(false);
        selectedDriver.setStatus("BUSY");

        order.setAssignedDriver(selectedDriver);

        return selectedDriver;
    }

    /**
     * Changes the strategy used to select drivers.
     *
     * @param selectionStrategy the new driver selection strategy
     */
    public void setSelectionStrategy(
            DriverSelectionStrategy selectionStrategy) {

        this.selectionStrategy =
                selectionStrategy;
    }
}