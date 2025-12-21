package groupproject.apgroupproject.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataModelTest {

    @Test
    void testStudentConstructor() {
        Student s = new Student("101", "Name", "email@test.com", "pass");
        assertNotNull(s);
    }

    @Test
    void testGetId() {
        Student s = new Student("555", "Name", "email", "pass");
        assertEquals("555", s.getId());
    }

    @Test
    void testGetName() {
        Student s = new Student("1", "John Doe", "email", "pass");
        assertEquals("John Doe", s.getName());
    }

    @Test
    void testGetEmail() {
        Student s = new Student("1", "Name", "john@uni.edu", "pass");
        assertEquals("john@uni.edu", s.getEmail());
    }
}