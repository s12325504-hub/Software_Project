package repository;

import domain.BicycleDriver;
import domain.CarDriver;
import domain.Driver;
import domain.ElectricVehicleDriver;
import domain.MotorcycleDriver;

import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    private List<Driver> drivers = new ArrayList<>();

    public DriverRepository() {

        drivers.add(
                new BicycleDriver(
                        "d1",
                        "Ahmad",
                        true,
                        3.0,
                        40.0,
                        5.0));

        drivers.add(
                new MotorcycleDriver(
                        "d2",
                        "Omar",
                        true,
                        5.0,
                        60.0,
                        20.0));

        drivers.add(
                new CarDriver(
                        "d3",
                        "Ali",
                        false,
                        2.0,
                        70.0,
                        10));

        drivers.add(
                new ElectricVehicleDriver(
                        "d4",
                        "Sami",
                        false,
                        4.0,
                        50.0,
                        30.0));
    }

    public List<Driver> findAvailableDrivers() {

        List<Driver> availableDrivers =
                new ArrayList<>();

        for (Driver driver : drivers) {

            if (driver.isAvailable()) {
                availableDrivers.add(driver);
            }
        }

        return availableDrivers;
    }

    public Driver findById(String id) {

        if (id == null) {
            return null;
        }

        for (Driver driver : drivers) {

            if (driver.getId().equals(id)) {
                return driver;
            }
        }

        return null;
    }

    // 🟢 دالة إرجاع جميع السائقين
    public List<Driver> findAll() {
        return drivers;
    }

    // 🟢 دالة إضافة سائق جديد لقائمة السائقين
    public void save(Driver driver) {
        if (driver != null) {
            drivers.add(driver);
        }
    }
}