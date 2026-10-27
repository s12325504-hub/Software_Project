package repository;

import static org.junit.jupiter.api.Assertions.*;

import domain.Order;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderRepositoryTest {

    private OrderRepository repository;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        repository = new OrderRepository();
        order1 = new Order("ORD-001", "CUST-100", "REST-500", Collections.emptyList(), new BigDecimal("50.00"));
        order2 = new Order("ORD-002", "CUST-101", "REST-501", Collections.emptyList(), new BigDecimal("75.00"));
    }

    @AfterEach
    void tearDown() {
        repository = null;
        order1 = null;
        order2 = null;
    }

    @Test
    @DisplayName("Should save order and return the saved order instance")
    void testSave() {
        Order savedOrder = repository.save(order1);

        assertAll("Save Order Verification",
            () -> assertNotNull(savedOrder, "Saved order should not be null"),
            () -> assertEquals("ORD-001", savedOrder.getId(), "Saved order ID should match original"),
            () -> assertEquals(1, repository.findAll().size(), "Repository size should be 1 after saving")
        );
    }

    @Test
    @DisplayName("Should return all saved orders in repository")
    void testFindAll() {
        assertTrue(repository.findAll().isEmpty(), "Initially repository should be empty");

        repository.save(order1);
        repository.save(order2);

        List<Order> allOrders = repository.findAll();

        assertAll("Find All Orders Verification",
            () -> assertEquals(2, allOrders.size(), "Repository should contain 2 orders"),
            () -> assertTrue(allOrders.contains(order1), "Should contain order1"),
            () -> assertTrue(allOrders.contains(order2), "Should contain order2")
        );
    }

    @Test
    @DisplayName("Should find order by valid existing ID")
    void testFindById_ExistingId() {
        repository.save(order1);
        repository.save(order2);

        Order foundOrder = repository.findById("ORD-002");

        assertNotNull(foundOrder, "Found order should not be null");
        assertEquals("ORD-002", foundOrder.getId(), "Returned order ID should match searched ID");
        assertEquals("CUST-101", foundOrder.getAssignedDriver() == null ? "CUST-101" : "");
    }

    @Test
    @DisplayName("Should return null when searching with non-existing ID")
    void testFindById_NonExistingId() {
        repository.save(order1);

        Order foundOrder = repository.findById("NON_EXISTING_ID");

        assertNull(foundOrder, "Should return null when order ID is not found");
    }

    @Test
    @DisplayName("Should return null when searching with null ID")
    void testFindById_NullId() {
        repository.save(order1);

        Order foundOrder = repository.findById(null);

        assertNull(foundOrder, "Should return null when ID is null");
    }
}