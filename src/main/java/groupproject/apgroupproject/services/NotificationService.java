package groupproject.apgroupproject.services;

public interface NotificationService {
    void showErrorMessage(String title, String message);
    void showInfoMessage(String title, String message);
    boolean showConfirmation(String title, String message);
}