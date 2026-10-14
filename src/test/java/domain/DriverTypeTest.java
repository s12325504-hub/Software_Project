package domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DriverTypeTest {

    @Test
    public void testBicycleDriverDistanceRule() {

        BicycleDriver driver =
                new BicycleDriver(
                        "d1",
                        "Ahmad",
                        true,
                        2.0,
                        20.0,
                        5.0);

        assertTrue(driver.canDeliver(4.0, 1));
        assertFalse(driver.canDeliver(6.0, 1));
    }

    @Test
    public void testMotorcycleDriverDistanceRule() {

        MotorcycleDriver driver =
                new MotorcycleDriver(
                        "d2",
                        "Omar",
                        true,
                        4.0,
                        50.0,
                        20.0);

        assertTrue(driver.canDeliver(15.0, 1));
        assertFalse(driver.canDeliver(25.0, 1));
    }

    @Test
    public void testCarDriverOrderSizeRule() {

        CarDriver driver =
                new CarDriver(
                        "d3",
                        "Ali",
                        true,
                        6.0,
                        60.0,
                        10);

        assertTrue(driver.canDeliver(5.0, 8));
        assertFalse(driver.canDeliver(5.0, 12));
    }

    @Test
    public void testElectricVehicleDriverRangeRule() {

        ElectricVehicleDriver driver =
                new ElectricVehicleDriver(
                        "d4",
                        "Sami",
                        true,
                        3.0,
                        55.0,
                        30.0);

        assertTrue(driver.canDeliver(25.0, 1));
        assertFalse(driver.canDeliver(35.0, 1));
    }
}