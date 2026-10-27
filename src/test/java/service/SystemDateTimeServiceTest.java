package service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SystemDateTimeServiceTest {

    @Test
    @DisplayName("Should return current LocalDateTime not null")
    void testNow() {
        SystemDateTimeService service = new SystemDateTimeService();
        
        LocalDateTime now = service.now();
        
        assertNotNull(now);
        // التأكد من أن الوقت المرجع قريب جداً من الوقت الحالي
        assertTrue(now.isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(now.isAfter(LocalDateTime.now().minusSeconds(5)));
    }
}