package service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConsoleNotificationServiceTest {

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        // توجيه مخرجات الطباعة إلى المجرى المؤقت لالتقاط النص المطبوخ
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        // إعادة مخرجات الشاشة لحالتها الأصلية
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should correctly print notification message to System.out")
    void testSendNotification() {
        ConsoleNotificationService service = new ConsoleNotificationService();
        String testMessage = "Test Notification Message";

        service.sendNotification(testMessage);

        // التحقق من أن النص تمت طباعته بنجاح في الكونسول
        assertEquals(testMessage, outputStreamCaptor.toString().trim());
    }
}