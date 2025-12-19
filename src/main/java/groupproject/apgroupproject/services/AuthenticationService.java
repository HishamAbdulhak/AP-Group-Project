package groupproject.apgroupproject.services;

public interface AuthenticationService {
    boolean loginStudent(String id, String password);
    boolean loginAdmin(String username, String password);
    boolean register(String id, String name, String email, String password, String recoverypasscode);
    boolean resetPassword(String id, String recoveryPasscode, String newPassword);
    boolean updateStudentProfile(String originalId, String newName, String newEmail, String newPassword);

    String getStudentName(String id);
    String getStudentEmail(String id);
}