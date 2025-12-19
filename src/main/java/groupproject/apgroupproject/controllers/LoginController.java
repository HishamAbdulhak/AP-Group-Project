package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.UserSession;
import groupproject.apgroupproject.services.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class LoginController extends BaseController {

    // --- Student Log In Tab ---
    @FXML private TextField studentIDField;
    @FXML private PasswordField passwordField;

    // --- Registration Tab ---
    @FXML private TextField newStudentIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private TextField securityCode;

    // --- Admin Log In Tab ---
    @FXML private TextField adminUserField;
    @FXML private PasswordField adminPasswordField;

    // Services specific to Login (NavigationService is inherited from BaseController)
    private final AuthenticationService authService;
    private final NotificationService notifyService;

    public LoginController() {
        this.authService = new FileAuthentication("students.txt", "admins.txt");
        this.notifyService = new AlertService();
        // Note: 'navService' is already initialized by BaseController
    }

    @FXML
    public void initialize() {
        // We don't need setupSidebar() here because the login screen has no sidebar.
    }

    // --- LogIn Logic ---

    @FXML
    private void handleStudentLogin(ActionEvent event) {
        String id = studentIDField.getText().trim();
        String pass = passwordField.getText();

        // Basic Validation
        if (id.isEmpty() || pass.isEmpty()) {
            notifyService.showErrorMessage("Login Error", "Please enter both ID and Password.");
            return;
        }

        if (!id.matches("\\d+")) {
            notifyService.showErrorMessage("Invalid Format", "Student ID must be a number.");
            return;
        }

        // Attempt Login
        if (authService.loginStudent(id, pass)) {
            System.out.println("Login Success! Switching scenes...");
            AppLogger.info("User logged in successfully: " + id);

            // Start Session
            UserSession.startSession(id, authService.getStudentName(id), authService.getStudentEmail(id), pass, false);

            // Navigate
            Button sourceButton = (Button) event.getSource();
            navService.navigateTo("HomeScreen.fxml", sourceButton);
        } else {
            notifyService.showErrorMessage("Login Failed", "Incorrect Student ID or Password.");
            AppLogger.error("Failed login attempt: " + id);
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String id = newStudentIdField.getText().trim();
        String name = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = newPasswordField.getText();
        String passcode = securityCode.getText().trim();

        // 1. Check for Empty Fields
        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || pass.isEmpty() || passcode.isEmpty()) {
            notifyService.showErrorMessage("Registration Error", "Please fill in all fields.");
            return;
        }

        // 2. Validate Student ID
        if (!id.matches("\\d+")) {
            notifyService.showErrorMessage("Invalid Student ID", "Student ID must contain only numbers.");
            return;
        }

        // Validate Email
        if (!email.contains("@")) {
            notifyService.showErrorMessage("Invalid Email", "Please enter a valid email address containing '@'.");
            return;
        }

        // Validate Password
        if (pass.length() < 8) {
            notifyService.showErrorMessage("Weak Password", "Password must be at least 8 characters long.");
            return;
        }

        // Attempt Registration
        boolean isRegistered = authService.register(id, name, email, pass, passcode);

        if (isRegistered) {
            notifyService.showInfoMessage("Registration Successful", "Account created! Please switch to the Login tab.");
            AppLogger.info("New student registered: " + id);

            // Clear fields
            newStudentIdField.clear();
            fullNameField.clear();
            emailField.clear();
            newPasswordField.clear();
            securityCode.clear();

        } else {
            notifyService.showErrorMessage("Registration Failed", "This Student ID is already registered.");
        }
    }

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        String user = adminUserField.getText();
        String pass = adminPasswordField.getText();

        if (authService.loginAdmin(user, pass)) {
            UserSession.startSession(user, "Administrator", "admin@uni.edu", pass, true);
            AppLogger.info("Admin logged in: " + user);

            Button sourceButton = (Button) event.getSource();
            // Admins usually go straight to dashboard, but Home is fine too
            navService.navigateTo("AdminDashboard.fxml", sourceButton);
        } else {
            notifyService.showErrorMessage("Access Denied", "Invalid Admin Credentials");
            AppLogger.error("Failed admin login: " + user);
        }
    }

    @FXML
    private void showForgotPasswordDialog(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Verify your identity to reset your password.");

        ButtonType resetButtonType = new ButtonType("Reset Password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        TextField passcodeField = new TextField();
        passcodeField.setPromptText("City of Birth");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        grid.add(new Label("Student ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Security Code:"), 0, 1);
        grid.add(passcodeField, 1, 1);
        grid.add(new Label("New Password:"), 0, 2);
        grid.add(newPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(idField::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == resetButtonType) {
            String id = idField.getText().trim();
            String code = passcodeField.getText().trim();
            String newPass = newPasswordField.getText();

            handleResetLogic(id, code, newPass);
        }
    }

    private void handleResetLogic(String id, String code, String newPass) {
        if (id.isEmpty() || code.isEmpty() || newPass.isEmpty()) {
            notifyService.showErrorMessage("Error", "All fields are required.");
            return;
        }
        if (newPass.length() < 8) {
            notifyService.showErrorMessage("Weak Password", "Password must be at least 8 characters.");
            return;
        }

        if (authService.resetPassword(id, code, newPass)) {
            notifyService.showInfoMessage("Success", "Password updated successfully! You can now log in.");
            AppLogger.info("Password reset for user: " + id);
        } else {
            notifyService.showErrorMessage("Reset Failed", "Invalid Student ID or Security Code.");
        }
    }
}