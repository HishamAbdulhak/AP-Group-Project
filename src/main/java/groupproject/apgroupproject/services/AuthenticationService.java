package groupproject.apgroupproject.services;

public interface AuthenticationService {
    boolean loginStudent(String id, String password);
    boolean loginAdmin(String username, String password);
    boolean register(String id, String name, String email, String password, String recoverypasscode);
    boolean resetPassword(String id, String recoveryPasscode, String newPassword);
}