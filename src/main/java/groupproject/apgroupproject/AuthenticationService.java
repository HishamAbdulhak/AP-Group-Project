package groupproject.apgroupproject;

public interface AuthenticationService {
    boolean loginStudent(String id, String password);
    boolean loginAdmin(String username, String password); // <--- NEW
    boolean register(String id, String name, String email, String password);
}