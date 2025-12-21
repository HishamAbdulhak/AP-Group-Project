package groupproject.apgroupproject.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InputValidationTest {

    // --- Helper Logic (Simulates your Controller) ---
    private boolean isValidEmail(String email) { return email != null && email.contains("@") && email.contains("."); }
    private boolean isValidPassword(String pass) { return pass != null && pass.length() >= 8; }
    private boolean isNumeric(String str) { return str != null && str.matches("\\d+"); }

    @Test
    void test01_EmailValid() { assertTrue(isValidEmail("test@uni.edu")); }

    @Test
    void test02_EmailNoAt() { assertFalse(isValidEmail("testuni.edu")); }

    @Test
    void test03_EmailNoDot() { assertFalse(isValidEmail("test@uniedu")); }

    @Test
    void test04_EmailEmpty() { assertFalse(isValidEmail("")); }

    @Test
    void test05_PasswordValid() { assertTrue(isValidPassword("password123")); }

    @Test
    void test06_PasswordShort() { assertFalse(isValidPassword("short")); }

    @Test
    void test07_PasswordNull() { assertFalse(isValidPassword(null)); }

    @Test
    void test08_IDNumeric() { assertTrue(isNumeric("123456")); }

    @Test
    void test09_IDAlpha() { assertFalse(isNumeric("123A")); }

    @Test
    void test10_IDSpecialChar() { assertFalse(isNumeric("123-456")); }
}