package service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.Driver;
import domain.Order;
import repository.DriverRepository;
import strategy.FastestDriverStrategy;
import strategy.NearestDriverStrategy;

public class DriverServiceTest {

    @Test
    public void testAssignNearestAvailableDriver() {

        DriverRepository driverRepository =
                new DriverRepository();

        DriverService driverService =
                new DriverService(
                        driverRepository,
                        new NearestDriverStrategy());

        Order order =
                new Order(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        java.math.BigDecimal.ZERO);

        Driver assignedDriver =
                driverService.assignDriver(order);

        assertNotNull(assignedDriver);

        assertEquals(
                "Ahmad",
                assignedDriver.getName());

        assertFalse(
                assignedDriver.isAvailable());

        assertEquals(
                "BUSY",
                assignedDriver.getStatus());

        assertEquals(
                assignedDriver,
                order.getAssignedDriver());
    }

    @Test
    public void testAssignFastestAvailableDriver() {

        DriverRepository driverRepository =
                new DriverRepository();

        DriverService driverService =
                new DriverService(
                        driverRepository,
                        new FastestDriverStrategy());

        Order order =
                new Order(
                        "o2",
                        "c1",
                        "r1",
                        null,
                        java.math.BigDecimal.ZERO);

        Driver assignedDriver =
                driverService.assignDriver(order);

        assertNotNull(assignedDriver);

        assertEquals(
                "Omar",
                assignedDriver.getName());

        assertFalse(
                assignedDriver.isAvailable());

        assertEquals(
                "BUSY",
                assignedDriver.getStatus());

        assertEquals(
                assignedDriver,
                order.getAssignedDriver());
    }

    @Test
    public void testNoAvailableDriverRejected() {

        DriverRepository driverRepository =
                new DriverRepository();

        DriverService driverService =
                new DriverService(
                        driverRepository,
                        new NearestDriverStrategy());

        Order firstOrder =
                new Order(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        java.math.BigDecimal.ZERO);

        Order secondOrder =
                new Order(
                        "o2",
                        "c2",
                        "r1",
                        null,
                        java.math.BigDecimal.ZERO);

        Order thirdOrder =
                new Order(
                        "o3",
                        "c3",
                        "r1",
                        null,
                        java.math.BigDecimal.ZERO);

        driverService.assignDriver(firstOrder);
        driverService.assignDriver(secondOrder);

        assertThrows(
                IllegalStateException.class,
                () -> driverService.assignDriver(thirdOrder));
    }
}