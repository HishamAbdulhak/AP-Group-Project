package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.services.SceneSwitcher;
import groupproject.apgroupproject.services.AlertService;
import groupproject.apgroupproject.services.NotificationService;
import groupproject.apgroupproject.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class HomeController extends BaseController {

    private final NotificationService alertService;

    @FXML private TextField questionText;
    @FXML private Button askAiButton;

    @FXML private Button admissionsButtton;
    @FXML private Button examsAndGrades;
    @FXML private Button idCard;
    @FXML private Button transfer;
    @FXML private Button library;
    @FXML private Button accommodation;

    @FXML private Hyperlink resetStudentEmail;
    @FXML private Hyperlink examPDF;
    @FXML private Hyperlink campusMap;
    @FXML private Hyperlink courseDeadline;
    @FXML private Hyperlink clubOptions;

    public HomeController() {
        this.navService = new SceneSwitcher();
        this.alertService = new AlertService();
    }

    @FXML
    public void initialize() {
        super.setupSidebar();

        // 1. "Ask AI" Logic - Links to AIChat.fxml
        askAiButton.setOnAction(e -> {
            String question = questionText.getText();
            if (question != null && !question.trim().isEmpty()) {
                UserSession.getInstance().setPendingQuestion(question);
                navService.navigateTo("AIChat.fxml", askAiButton);
            } else {
                questionText.setPromptText("Please type a question first...");
            }
        });

        // 2. Quick Access Buttons - Links to DocumentViewer.fxml
        admissionsButtton.setOnAction(e -> viewDocumentDirectly("admission and application guide.docx", admissionsButtton));
        examsAndGrades.setOnAction(e -> viewDocumentDirectly("exam and assessment guide.docx", examsAndGrades));
        idCard.setOnAction(e -> viewDocumentDirectly("id card fee.pdf", idCard));
        transfer.setOnAction(e -> viewDocumentDirectly("apply and transfer guide.txt", transfer));
        library.setOnAction(e -> viewDocumentDirectly("library rules and system.docx", library));
        accommodation.setOnAction(e -> viewDocumentDirectly("accommodation.pdf", accommodation));

        // 3. Hyperlinks - Trending Questions & Downloads

        // Viewer Link
        // Inside initialize() method of HomeController.java
        resetStudentEmail.setOnAction(e -> viewDocumentDirectly("student email facility.docx", resetStudentEmail));

        //Exam PDF is Downloadable
        examPDF.setOnAction(e -> downloadToSystem("exam and assessment guide.docx", "Exam Guidelines"));

        // AI Chat Links
        clubOptions.setOnAction(e -> askSpecificQuestion("What campus clubs are available?", clubOptions));
        courseDeadline.setOnAction(e -> askSpecificQuestion("When is the deadline for course registration?", courseDeadline));

        //Campus Map is now Downloadable
        campusMap.setOnAction(e -> downloadToSystem("TaylorParkZone-1.pdf", "Campus Map"));
    }

    private void viewDocumentDirectly(String fileName, Node sourceNode) {
        // Force the app to look at the absolute path of your project root
        File projectDir = new File("").getAbsoluteFile();
        File file = new File(projectDir, "project_documents/" + fileName);

        if (file.exists()) {
            // Pass the absolute path so the DocumentViewer knows exactly where to look
            UserSession.getInstance().setDocumentCategory(file.getAbsolutePath());
            navService.navigateTo("DocumentViewer.fxml", sourceNode);
        } else {
            // Debugging print to see exactly where it is looking in your console
            System.out.println("DEBUG: File not found at: " + file.getAbsolutePath());
            alertService.showErrorMessage("File Error", "Could not find the document: " + fileName);
        }
    }

    private void downloadToSystem(String fileName, String displayName) {
        // Ask the user to select a folder
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder to Save " + displayName);

        // Show the dialog
        File selectedDirectory = directoryChooser.showDialog(askAiButton.getScene().getWindow());

        if (selectedDirectory != null) {
            try {
                // Find the source file in project_documents
                File projectDir = new File("").getAbsoluteFile();
                File source = new File(projectDir, "project_documents/" + fileName);

                if (!source.exists()) {
                    alertService.showErrorMessage("Source Missing", "The file " + fileName + " is missing from the project folder.");
                    return;
                }

                // Define the destination path based on user selection
                File destination = new File(selectedDirectory, fileName);

                // Perform the copy
                Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                alertService.showInfoMessage("Success", displayName + " has been saved to: " + selectedDirectory.getAbsolutePath());
                System.out.println("Download Successful: " + destination.getAbsolutePath());

            } catch (IOException ex) {
                alertService.showErrorMessage("Download Failed", "An error occurred while saving the file: " + ex.getMessage());
            }
        }
    }


    // Passes a specific question directly to the AI Chat

    private void askSpecificQuestion(String question, Node sourceNode) {
        UserSession.getInstance().setPendingQuestion(question);
        navService.navigateTo("AIChat.fxml", sourceNode);
    }
}