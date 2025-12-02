package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.UserSession;
import groupproject.apgroupproject.services.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {
    //Student Log In Tab
    @FXML private TextField studentIDField;
    @FXML private PasswordField passwordField;

    //Registration Tab
    @FXML private TextField newStudentIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private TextField securityCode;

    //Admin Log In Tab
    @FXML private TextField adminUserField;
    @FXML private PasswordField adminPasswordField;


    private AuthenticationService authService;
    private NotificationService notifyService;
    private NavigationService navService;

    public LoginController() {
        // Initialize with TWO files: students.txt and admins.txt
        this.authService = new FileAuthentication("students.txt", "admins.txt");
        this.notifyService = new AlertService();
        this.navService = new SceneSwitcher();
    }

    // LogIn Logic

    @FXML
    private void handleStudentLogin(ActionEvent event) {
        String id = studentIDField.getText().trim();
        String pass = passwordField.getText();

        //Basic Validation
        if (id.isEmpty() || pass.isEmpty()) {
            notifyService.showErrorMessage("Login Error", "Please enter both ID and Password.");
            return;
        }

        if (!id.matches("\\d+")) {
            notifyService.showErrorMessage("Invalid Format",
                    "Student ID must be a number.");
            return;
        }

        //Attempt Login
        if (authService.loginStudent(id, pass)) {
            System.out.println("Login Success! Switching scenes...");
            //Ensuring Student does not have access to admin dashboard
            UserSession.startSession(id, "Student Name", "email", pass, false);
            Button sourceButton = (Button) event.getSource();
            navService.navigateTo("HomeScreen.fxml", sourceButton);
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
        String passcode = securityCode.getText().trim();

        // 1. Check for Empty Fields
        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || pass.isEmpty() || passcode.isEmpty()) {
            notifyService.showErrorMessage("Registration Error", "Please fill in all fields.");
            return;
        }

        // 2. Validate Student ID (Numbers Only)
        if (!id.matches("\\d+")) {
            notifyService.showErrorMessage("Invalid Student ID",
                    "Student ID must contain only numbers.");
            return;
        }

        //Validate Email (Must contain '@')
        if (!email.contains("@")) {
            notifyService.showErrorMessage("Invalid Email",
                    "Please enter a valid email address containing '@'.");
            return;
        }

        //Validate Password (Min 8 chars)
        if (pass.length() < 8) {
            notifyService.showErrorMessage("Weak Password",
                    "Password must be at least 8 characters long.");
            return;
        }

        //Attempt Registration
        boolean isRegistered = authService.register(id, name, email, pass, passcode);

        if (isRegistered) {
            notifyService.showInfoMessage("Registration Successful",
                    "Account created! Please switch to the Login tab.");

            // Clear fields
            newStudentIdField.clear();
            fullNameField.clear();
            emailField.clear();
            newPasswordField.clear();
            securityCode.clear();

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
            //Ensures Admin role persists so admins can see dashboard
            UserSession.startSession(user, "Administrator", "admin@uni.edu", pass, true);
            Button sourceButton = (Button) event.getSource();
            navService.navigateTo("HomeScreen.fxml", sourceButton);
        } else {
            notifyService.showErrorMessage("Access Denied", "Invalid Admin Credentials");
        }
    }
    @FXML
    private void showForgotPasswordDialog(ActionEvent event) {
        //Create the Custom Dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Verify your identity to reset your password.");

        //Set the buttons (Reset + Cancel)
        ButtonType resetButtonType = new ButtonType("Reset Password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

        // 3. Create the Layout (GridPane)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        //Create the Input Fields
        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        TextField passcodeField = new TextField();
        passcodeField.setPromptText("City of Birth");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        //Add fields to layout
        grid.add(new Label("Student ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Security Code:"), 0, 1);
        grid.add(passcodeField, 1, 1);
        grid.add(new Label("New Password:"), 0, 2);
        grid.add(newPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        //Request focus on the ID field by default
        Platform.runLater(idField::requestFocus);

        //Show the Dialog and Wait for Result
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == resetButtonType) {
            //User clicked "Reset Password"
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

        // Call Service
        if (authService.resetPassword(id, code, newPass)) {
            notifyService.showInfoMessage("Success", "Password updated successfully! You can now log in.");
        } else {
            notifyService.showErrorMessage("Reset Failed", "Invalid Student ID or Security Code.");
        }
    }
}
