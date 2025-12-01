package groupproject.apgroupproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    // --- 1. STUDENT LOGIN TAB ---
    @FXML private TextField studentIDField;
    @FXML private PasswordField passwordField;

    // --- 2. REGISTRATION TAB ---
    @FXML private TextField newStudentIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;

    // --- 3. ADMIN LOGIN TAB ---
    @FXML private TextField adminUserField;     // <--- Make sure you added fx:id="adminUserField"
    @FXML private PasswordField adminPasswordField; // <--- Make sure you added fx:id="adminPasswordField"

    // --- OOP SERVICES ---
    private AuthenticationService authService;
    private NotificationService notifyService;

    public LoginController() {
        // Initialize with TWO files: students.txt and admins.txt
        this.authService = new FileAuthentication("students.txt", "admins.txt");
        this.notifyService = new AlertService();
    }

    // --- ACTIONS ---

    @FXML
    private void handleStudentLogin(ActionEvent event) {
        String id = studentIDField.getText().trim();
        String pass = passwordField.getText();

        // 1. Basic Validation
        if (id.isEmpty() || pass.isEmpty()) {
            notifyService.showErrorMessage("Login Error", "Please enter both ID and Password.");
            return;
        }

        // 2. NEW RULE: Check ID format before checking file
        if (!id.matches("\\d+")) {
            notifyService.showErrorMessage("Invalid Format",
                    "Student ID must be a number.");
            return;
        }

        // 3. Attempt Login
        if (authService.loginStudent(id, pass)) {
            System.out.println("Login Success! Switching scenes...");
            // Ensure you use the Correct File Name here!
            navigateTo("HomeScreen.fxml", event);
        } else {
            notifyService.showErrorMessage("Login Failed",
                    "Incorrect Student ID or Password.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Get text and trim spaces
        String id = newStudentIdField.getText().trim();
        String name = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = newPasswordField.getText();

        // 1. Check for Empty Fields
        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            notifyService.showErrorMessage("Registration Error", "Please fill in all fields.");
            return;
        }

        // 2. Validate Student ID (Numbers Only)
        if (!id.matches("\\d+")) {
            notifyService.showErrorMessage("Invalid Student ID",
                    "Student ID must contain only numbers.");
            return;
        }

        // 3. NEW RULE: Validate Email (Must contain '@')
        if (!email.contains("@")) {
            notifyService.showErrorMessage("Invalid Email",
                    "Please enter a valid email address containing '@'.");
            return;
        }

        // 4. Validate Password (Min 8 chars)
        if (pass.length() < 8) {
            notifyService.showErrorMessage("Weak Password",
                    "Password must be at least 8 characters long.");
            return;
        }

        // 5. Attempt Registration
        boolean isRegistered = authService.register(id, name, email, pass);

        if (isRegistered) {
            notifyService.showInfoMessage("Registration Successful",
                    "Account created! Please switch to the Login tab.");

            // Clear fields
            newStudentIdField.clear();
            fullNameField.clear();
            emailField.clear();
            newPasswordField.clear();

        } else {
            notifyService.showErrorMessage("Registration Failed",
                    "This Student ID is already registered.");
        }
    }

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        String user = adminUserField.getText();
        String pass = adminPasswordField.getText();

        if (authService.loginAdmin(user, pass)) {
            // Admins go to the DASHBOARD, not the Home Screen
            navigateTo("Admin Dashboard.fxml", event);
        } else {
            notifyService.showErrorMessage("Access Denied", "Invalid Admin Credentials");
        }
    }

    // Helper to reduce duplicated code
    private void navigateTo(String fxmlFile, ActionEvent event) {
        // Get the stage from ANY button that triggered the event
        Button btn = (Button) event.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        SceneSwitcher.switchTo(stage, fxmlFile);
    }
}