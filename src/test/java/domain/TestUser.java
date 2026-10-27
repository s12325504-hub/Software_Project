package domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestUser {

    private user testUser;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        // تُنفذ مرة واحدة قبل البدء بجميع الاختبارات
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        // تُنفذ مرة واحدة بعد الانتهاء من جميع الاختبارات
    }

    @BeforeEach
    void setUp() throws Exception {
        // تهيئة كائن user قبل كل اختبار ضماناً لاستقلالية الاختبارات
        testUser = new user("sara_admin", "securePass123", "ADMIN");
    }

    @AfterEach
    void tearDown() throws Exception {
        // تفريغ الكائن بعد كل اختبار
        testUser = null;
    }

    @Test
    @DisplayName("Test User Getters with Valid Data")
    void testUserGetters() {
        // 1. القيم المتوقعة (Expected)
        String expectedUsername = "sara_admin";
        String expectedPassword = "securePass123";
        String expectedRole = "ADMIN";

        // 2. القيم الفعلية من الكود (Actual)
        String actualUsername = testUser.getUsername();
        String actualPassword = testUser.getPassword();
        String actualRole = testUser.getRole();

        // 3. المقارنة بين المتوقع والفعلي (Assert)
        assertAll("User Details Verification",
            () -> assertEquals(expectedUsername, actualUsername, "Username does not match"),
            () -> assertEquals(expectedPassword, actualPassword, "Password does not match"),
            () -> assertEquals(expectedRole, actualRole, "Role does not match")
        );
    }

    @Test
    @DisplayName("Test User with Null Values")
    void testUserNullValues() {
        // 1. إعداد البيانات الممررة والتوقع (Expected)
        user nullUser = new user(null, null, null);

        // 2. القيم الفعلية (Actual)
        String actualUsername = nullUser.getUsername();
        String actualPassword = nullUser.getPassword();
        String actualRole = nullUser.getRole();

        // 3. المقارنة للتحقق من أن القيم المفروض تكون null
        assertAll("Null Properties Verification",
            () -> assertNull(actualUsername, "Username should be null"),
            () -> assertNull(actualPassword, "Password should be null"),
            () -> assertNull(actualRole, "Role should be null")
        );
    }
}