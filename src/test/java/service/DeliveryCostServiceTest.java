package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class DeliveryCostServiceTest {

    @Test
    public void testCalculateDeliveryCost() {

        DeliveryCostService service = new DeliveryCostService();

        BigDecimal result = service.calculateDeliveryCost(5.0);

        // استخدام compareTo يتجاهل فروق الـ Scale والأصفار الزائدة مقارنة بـ assertEquals
        assertNotNull(result);
        assertEquals(0, new BigDecimal("10.0").compareTo(result));
    }

    @Test
    public void testNegativeDistanceRejected() {

        DeliveryCostService service = new DeliveryCostService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateDeliveryCost(-1.0));
    }

    // ==========================================
    // الاختبارات المضافة لتغطية calculateDeliveryCostByAddress
    // ==========================================

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    public void testCalculateDeliveryCostByAddress_EmptyOrBlank(String address) {
        DeliveryCostService service = new DeliveryCostService();
        BigDecimal result = service.calculateDeliveryCostByAddress(address);
        assertEquals(0, new BigDecimal("5.0").compareTo(result)); // 2.5 * 2.0 = 5.0
    }

    @Test
    public void testCalculateDeliveryCostByAddress_Null() {
        DeliveryCostService service = new DeliveryCostService();
        BigDecimal result = service.calculateDeliveryCostByAddress(null);
        assertEquals(0, new BigDecimal("5.0").compareTo(result));
    }

    @ParameterizedTest
    @CsvSource({
        "nablus center, 10.0",   // 5.0 * 2.0
        "asira main street, 8.0", // 4.0 * 2.0
        "rafidia street, 7.0",    // 3.5 * 2.0
        "old city market, 6.0",   // 3.0 * 2.0
        "unknown location, 5.0"   // 2.5 * 2.0
    })
    public void testCalculateDeliveryCostByAddress_KnownLocations(String address, String expectedCost) {
        DeliveryCostService service = new DeliveryCostService();
        BigDecimal result = service.calculateDeliveryCostByAddress(address);
        assertEquals(0, new BigDecimal(expectedCost).compareTo(result));
    }
}