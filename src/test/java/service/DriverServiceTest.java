package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

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
                        BigDecimal.ZERO);

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
                        BigDecimal.ZERO);

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
                        BigDecimal.ZERO);

        Order secondOrder =
                new Order(
                        "o2",
                        "c2",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        Order thirdOrder =
                new Order(
                        "o3",
                        "c3",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        driverService.assignDriver(firstOrder);
        driverService.assignDriver(secondOrder);

        assertThrows(
                IllegalStateException.class,
                () -> driverService.assignDriver(thirdOrder));
    }

    @Test
    public void testDriverTypeRuleUsedDuringAssignment() {

        DriverRepository driverRepository =
                new DriverRepository();

        DriverService driverService =
                new DriverService(
                        driverRepository,
                        new NearestDriverStrategy());

        Order order =
                new Order(
                        "o4",
                        "c1",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        Driver assignedDriver =
                driverService.assignDriver(
                        order,
                        10.0,
                        1);

        assertNotNull(assignedDriver);

        assertEquals(
                "Omar",
                assignedDriver.getName());

        assertEquals(
                assignedDriver,
                order.getAssignedDriver());
    }

    @Test
    public void testNoEligibleDriverRejected() {

        DriverRepository driverRepository =
                new DriverRepository();

        DriverService driverService =
                new DriverService(
                        driverRepository,
                        new NearestDriverStrategy());

        Order order =
                new Order(
                        "o5",
                        "c1",
                        "r1",
                        null,
                        BigDecimal.ZERO);

        assertThrows(
                IllegalStateException.class,
                () -> driverService.assignDriver(
                        order,
                        30.0,
                        1));
    }
}