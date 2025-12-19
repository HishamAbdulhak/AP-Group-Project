package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.UserSession;
import groupproject.apgroupproject.services.AlertService;
import groupproject.apgroupproject.services.AuthenticationService;
import groupproject.apgroupproject.services.FileAuthentication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AccountSettingsController extends BaseController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button saveButton;
    @FXML private Button logOut;

    private final AuthenticationService authService = new FileAuthentication("students.txt", "admins.txt");
    private final AlertService alertService = new AlertService();

    @FXML
    public void initialize() {
        super.setupSidebar();

        // 1. Check if user is logged in
        if (UserSession.getInstance() != null && UserSession.getInstance().getId() != null) {

            // FIX: Added .getInstance()
            if (fullNameField != null) fullNameField.setText(UserSession.getInstance().getName());
            if (emailField != null) emailField.setText(UserSession.getInstance().getEmail());
        }

        // SAVE LOGIC
        if (saveButton != null) {
            saveButton.setOnAction(e -> handleSaveChanges());
        }

        // LOGOUT LOGIC
        logOut.setOnAction(event -> {
            UserSession.cleanSession(); // This is static, so it is correct!
            navService.navigateTo("LogInScreen.fxml", logOut);
        });
    }

    private void handleSaveChanges() {
        // Ensure user is logged in
        if (UserSession.getInstance() == null || UserSession.getInstance().getId() == null) return;

        UserSession session = UserSession.getInstance(); // Helper variable to make code cleaner

        // Get Inputs
        String inputName = fullNameField.getText().trim();
        String inputEmail = emailField.getText().trim();
        String inputPass = passwordField.getText().trim();

        String finalName = inputName.isEmpty() ? session.getName() : inputName;

        // EMAIL: If empty, use old email. If changed, use new email.
        String finalEmail = inputEmail.isEmpty() ? session.getEmail() : inputEmail;

        // PASSWORD: If empty, Keep OLD password. If typed, use NEW password.
        String finalPass = inputPass.isEmpty() ? session.getPassword() : inputPass;

        if (finalName.isEmpty()) {
            alertService.showErrorMessage("Error", "Name cannot be empty.");
            return;
        }

        // Check if anything actually changed
        if (finalName.equals(session.getName()) &&
                finalEmail.equals(session.getEmail()) &&
                finalPass.equals(session.getPassword())) {

            alertService.showInfoMessage("No Changes", "You didn't change anything.");
            return;
        }

        // UPDATE (Using the ID from the session)
        boolean success = authService.updateStudentProfile(
                session.getId(),
                finalName,
                finalEmail,
                finalPass
        );

        if (success) {
            alertService.showInfoMessage("Success", "Profile updated successfully!");

            // IMPORTANT: Update the session data immediately
            UserSession.startSession(
                    session.getId(),
                    finalName,
                    finalEmail,
                    finalPass,
                    session.isAdmin()
            );

            // Clear password field to indicate safety
            passwordField.clear();

        } else {
            alertService.showErrorMessage("Error", "Could not update profile. Try again.");
        }
    }
}