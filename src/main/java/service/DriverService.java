package service;

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
    public DriverService(DriverRepository driverRepository,
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
            throw new IllegalArgumentException("Order cannot be null");
        }

        List<Driver> availableDrivers =
                driverRepository.findAvailableDrivers();

        Driver selectedDriver =
                selectionStrategy.selectDriver(availableDrivers);

        if (selectedDriver == null) {
            throw new IllegalStateException("No available driver");
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

        this.selectionStrategy = selectionStrategy;
    }
}