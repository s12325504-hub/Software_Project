package repository;

import static org.junit.jupiter.api.Assertions.*;

import domain.user;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class userRepositoryTest {

    private userRepository repo;

    @BeforeEach
    void setUp() {
        repo = new userRepository();
    }

    @AfterEach
    void tearDown() {
        repo = null;
    }

    @Test
    @DisplayName("Should find existing customer user successfully")
    void testFindByUsername_ExistingUser_Customer() {
        // 1. المتوقع (Expected)
        String expectedUsername = "tala";
        String expectedRole = "customer";

        // 2. الفعلي (Actual)
        user actualUser = repo.findByUsername(expectedUsername);

        // 3. المقارنة (Assert)
        assertNotNull(actualUser, "User should not be null");
        assertAll("Customer User Verification",
            () -> assertEquals(expectedUsername, actualUser.getUsername(), "Username should match tala"),
            () -> assertEquals(expectedRole, actualUser.getRole(), "Role should match customer")
        );
    }

    @Test
    @DisplayName("Should find existing manager user successfully")
    void testFindByUsername_ExistingUser_Manager() {
        // 1. المتوقع (Expected)
        String expectedUsername = "reem";
        String expectedRole = "manager";

        // 2. الفعلي (Actual)
        user actualUser = repo.findByUsername(expectedUsername);

        // 3. المقارنة (Assert)
        assertNotNull(actualUser, "User should not be null");
        assertAll("Manager User Verification",
            () -> assertEquals(expectedUsername, actualUser.getUsername(), "Username should match reem"),
            () -> assertEquals(expectedRole, actualUser.getRole(), "Role should match manager")
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user is not found")
    void testFindByUsername_NonExistingUser_ThrowsException() {
        // 1. المتوقع (Expected)
        String nonExistingUsername = "unknown_user";
        String expectedErrorMessage = "User not found with username: " + nonExistingUsername;

        // 2. التنفيذ ومراقبة الاستثناء (Act & Assert)
        IllegalArgumentException actualException = assertThrows(
            IllegalArgumentException.class,
            () -> repo.findByUsername(nonExistingUsername),
            "Should throw IllegalArgumentException when username does not exist"
        );

        // 3. التحقق من رسالة الخطأ المتوقعة
        assertEquals(expectedErrorMessage, actualException.getMessage(), "Exception error message should match");
    }
}
