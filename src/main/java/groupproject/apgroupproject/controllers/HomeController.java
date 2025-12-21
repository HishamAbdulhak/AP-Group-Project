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

        // 1. "Ask AI" Logic
        askAiButton.setOnAction(e -> {
            String question = questionText.getText();
            if (question != null && !question.trim().isEmpty()) {
                UserSession.getInstance().setPendingQuestion(question);
                navService.navigateTo("AIChat.fxml", askAiButton);
            } else {
                questionText.setPromptText("Please type a question first...");
            }
        });

        // 2. Direct Mappings to Built-in Viewer (Quick Access)
        admissionsButtton.setOnAction(e -> viewDocumentDirectly("admission and application guide.docx", admissionsButtton));
        examsAndGrades.setOnAction(e -> viewDocumentDirectly("exam and assessment guide.docx", examsAndGrades));
        idCard.setOnAction(e -> viewDocumentDirectly("id card fee.pdf", idCard));
        transfer.setOnAction(e -> viewDocumentDirectly("apply and transfer guide.txt", transfer));
        library.setOnAction(e -> viewDocumentDirectly("library rules and system.docx", library));
        accommodation.setOnAction(e -> viewDocumentDirectly("accommodation.pdf", accommodation));

        // 3. Trending Questions Mappings
        resetStudentEmail.setOnAction(e -> viewDocumentDirectly("student email facility.docx", resetStudentEmail));

        // UPDATED: Now calls downloadToSystem instead of viewDocumentDirectly
        examPDF.setOnAction(e -> downloadToSystem("exam and assessment guide.docx", "Exam Guidelines"));

        clubOptions.setOnAction(e -> askSpecificQuestion("What clubs are there?", clubOptions));
        courseDeadline.setOnAction(e -> askSpecificQuestion("When are the course deadlines?", courseDeadline));

        // 4. Specialized Download for Campus Map
        campusMap.setOnAction(e -> downloadToSystem("TaylorParkZone-1.pdf", "Campus Map"));
    }

    private void viewDocumentDirectly(String fileName, Node sourceNode) {
        String fullPath = "project_documents/" + fileName;
        File file = new File(fullPath);

        if (file.exists()) {
            UserSession.getInstance().setDocumentCategory(fullPath);
            navService.navigateTo("DocumentViewer.fxml", sourceNode);
        } else {
            alertService.showErrorMessage("File Error", "Could not find the document: " + fileName);
        }
    }

    // UPDATED: Added a displayName parameter to make the alerts more dynamic
    private void downloadToSystem(String fileName, String displayName) {
        boolean confirm = alertService.showConfirmation("Download " + displayName,
                "Do you want to download the " + displayName + " to your system?");

        if (confirm) {
            try {
                File source = new File("project_documents/" + fileName);
                String userHome = System.getProperty("user.home");
                File destination = new File(userHome + "/Downloads/" + fileName);

                Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                alertService.showInfoMessage("Success", displayName + " has been saved to your Downloads folder.");
            } catch (IOException ex) {
                alertService.showErrorMessage("Download Failed", "An error occurred while saving the file: " + ex.getMessage());
            }
        }
    }

    private void askSpecificQuestion(String question, Node sourceNode) {
        UserSession.getInstance().setPendingQuestion(question);
        navService.navigateTo("AIChat.fxml", sourceNode);
    }
}