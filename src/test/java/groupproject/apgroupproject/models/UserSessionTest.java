package groupproject.apgroupproject.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @AfterEach
    void tearDown() {
        UserSession.cleanSession();
    }

    @Test
    void testStartSession() {
        UserSession.startSession("1", "User", "email", "pass", false);
        assertNotNull(UserSession.getInstance());
        assertEquals("User", UserSession.getInstance().getName());
    }

    @Test
    void testCleanSession() {
        UserSession.startSession("1", "User", "email", "pass", false);
        UserSession.cleanSession();
        assertNull(UserSession.getInstance());
    }
}