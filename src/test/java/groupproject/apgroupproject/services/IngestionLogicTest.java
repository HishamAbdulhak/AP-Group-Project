package groupproject.apgroupproject.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IngestionLogicTest {

    private boolean isFileSupported(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".docx") || lower.endsWith(".txt");
    }

    @Test
    void testPdfIsSupported() {
        assertTrue(isFileSupported("lecture_notes.pdf"), "PDF files should be accepted");
    }

    @Test
    void testDocxIsSupported() {
        assertTrue(isFileSupported("assignment.docx"), "DOCX files should be accepted");
    }

    @Test
    void testTxtIsSupported() {
        assertTrue(isFileSupported("notes.txt"), "TXT files should be accepted");
    }

    @Test
    void testImageNotSupported() {
        assertFalse(isFileSupported("screenshot.png"), "PNG images should be rejected");
    }

    @Test
    void testExeNotSupported() {
        assertFalse(isFileSupported("virus.exe"), "EXE files should be rejected");
    }
}