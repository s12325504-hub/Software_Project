package repository;

import domain.Driver;
import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    private List<Driver> drivers = new ArrayList<>();

    public DriverRepository() {
        // بيانات تجريبية للسائقين
        drivers.add(new Driver("d1", "Ahmad", true, 3.0, 40.0));
        drivers.add(new Driver("d2", "Omar", true, 5.0, 60.0));
        drivers.add(new Driver("d3", "Ali", false, 2.0, 70.0));
    }

    // إرجاع السائقين المتاحين فقط
    public List<Driver> findAvailableDrivers() {

        List<Driver> availableDrivers = new ArrayList<>();

        for (Driver driver : drivers) {
            if (driver.isAvailable()) {
                availableDrivers.add(driver);
            }
        }

        return availableDrivers;
    }

    // البحث عن سائق حسب الـ ID
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
}