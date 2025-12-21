package groupproject.apgroupproject.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SearchLogicTest {

    // Helper method that mimics your BrowserController filtering logic
    private boolean matchesSearch(String filename, String query) {
        if (filename == null) return false;
        if (query == null || query.isEmpty()) return true; // Empty search shows all
        return filename.toLowerCase().contains(query.toLowerCase());
    }

    @Test
    void testExactMatch() {
        assertTrue(matchesSearch("ExamSchedule.pdf", "Exam"), "Should find exact match");
    }

    @Test
    void testCaseInsensitiveMatch() {
        assertTrue(matchesSearch("ExamSchedule.pdf", "exam"), "Should find match regardless of case");
    }

    @Test
    void testPartialMatch() {
        assertTrue(matchesSearch("University_Rules_2024.pdf", "Rules"), "Should find partial match");
    }

    @Test
    void testNoMatch() {
        assertFalse(matchesSearch("Cafeteria_Menu.pdf", "Exam"), "Should not find unrelated files");
    }
}