package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.user;
import repository.userRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthTest {

    @Mock
    private userRepository userRepository;

    private Auth auth;

    private AutoCloseable mocksCloseable;

    @BeforeEach
    void setUp() {
        mocksCloseable = MockitoAnnotations.openMocks(this);
        auth = new Auth(userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocksCloseable != null) {
            mocksCloseable.close();
        }
        auth = null;
    }

    @Test
    @DisplayName("Should successfully authenticate and return user when credentials are correct")
    void testLogin_Success() {
        // 1. المتوقع (Expected)
        String expectedUsername = "tala";
        String expectedPassword = "password123";
        String expectedRole = "customer";

        user mockUser = new user(expectedUsername, expectedPassword, expectedRole);
        when(userRepository.findByUsername(expectedUsername)).thenReturn(mockUser);

        // 2. الفعلي (Actual)
        user actualLoggedInUser = auth.login(expectedUsername, expectedPassword);

        // 3. المقارنة والتحقق (Assert & Verify)
        assertNotNull(actualLoggedInUser, "User should not be null");
        assertAll("Successful Login Verification",
            () -> assertEquals(expectedUsername, actualLoggedInUser.getUsername(), "Username should match"),
            () -> assertEquals(expectedPassword, actualLoggedInUser.getPassword(), "Password should match"),
            () -> assertEquals(expectedRole, actualLoggedInUser.getRole(), "Role should match")
        );

        verify(userRepository, times(1)).findByUsername(expectedUsername);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when password does not match")
    void testLogin_InvalidPassword_ThrowsException() {
        // 1. المتوقع (Expected)
        String username = "tala";
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String expectedErrorMessage = "Invalid credentials";

        user mockUser = new user(username, correctPassword, "customer");
        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        // 2. التنفيذ ومراقبة الاستثناء (Act & Assert)
        IllegalArgumentException actualException = assertThrows(
            IllegalArgumentException.class,
            () -> auth.login(username, wrongPassword),
            "Should throw IllegalArgumentException on password mismatch"
        );

        // 3. التحقق من رسالة الخطأ والاستدعاء
        assertEquals(expectedErrorMessage, actualException.getMessage(), "Error message should match");
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should throw exception when user is not found in repository")
    void testLogin_UserNotFound_ThrowsException() {
        // 1. المتوقع (Expected)
        String unknownUsername = "unknown_user";
        String dummyPassword = "password123";
        String expectedErrorMessage = "User not found with username: " + unknownUsername;

        when(userRepository.findByUsername(unknownUsername))
            .thenThrow(new IllegalArgumentException(expectedErrorMessage));

        // 2. التنفيذ ومراقبة الاستثناء (Act & Assert)
        IllegalArgumentException actualException = assertThrows(
            IllegalArgumentException.class,
            () -> auth.login(unknownUsername, dummyPassword)
        );

        // 3. التحقق من الرسالة والاستدعاء
        assertEquals(expectedErrorMessage, actualException.getMessage(), "Error message should match");
        verify(userRepository, times(1)).findByUsername(unknownUsername);
    }
}