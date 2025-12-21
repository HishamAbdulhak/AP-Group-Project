package groupproject.apgroupproject.controllers;

import dev.langchain4j.data.document.Document;
import groupproject.apgroupproject.models.UserSession;
import groupproject.apgroupproject.services.AlertService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DocumentViewerController extends BaseController {

    @FXML private Button backButton;
    @FXML private Button downloadButton;
    @FXML private Label fileNameLabel;
    @FXML private TextArea contentArea;

    private final AlertService alertService = new AlertService();

    //Holds the file passed from the Browser Page
    public static File fileToView;

    @FXML
    public void initialize() {
        super.setupSidebar();

        //Back Button Logic
        if (backButton != null) {
            backButton.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", backButton));
        }

        //Download Button Logic
        if (downloadButton != null) {
            downloadButton.setOnAction(e -> handleDownload());
        }

        //Load the File Content
        String pathFromSession = UserSession.getInstance().getDocumentCategoryAndClear();
        if (pathFromSession != null) {
            fileToView = new File(pathFromSession);
        }

        if (fileToView != null && fileToView.exists()) {
            loadDocument(fileToView);
        } else {
            if (fileNameLabel != null) fileNameLabel.setText("No file selected.");
            contentArea.setText("Please go back and select a document.");
        }
    }

    private void handleDownload() {
        if (fileToView == null || !fileToView.exists()) return;

        // Setup FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Document");
        fileChooser.setInitialFileName(fileToView.getName());

        //Extension Filter
        String name = fileToView.getName().toLowerCase();
        if (name.endsWith(".pdf")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        } else if (name.endsWith(".docx")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Documents", "*.docx"));
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        }

        // Show Save Dialog
        File destFile = fileChooser.showSaveDialog(downloadButton.getScene().getWindow());

        // Copy the file
        if (destFile != null) {
            try {
                Files.copy(fileToView.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                alertService.showInfoMessage("Download Complete", "File saved successfully to: " + destFile.getName());

            } catch (IOException ex) {
                alertService.showErrorMessage("Download Failed", "Could not save file: " + ex.getMessage());
            }
        }
    }

    private void loadDocument(File file) {
        // Update Title
        if (fileNameLabel != null) {
            fileNameLabel.setText(file.getName());
        }

        // Update Text Area
        if (contentArea != null) {
            try {
                contentArea.setText("Loading document content...");

                // This converts the binary file into plain text for the user to read.
                Document doc = ingestionService.loadSingleDocument(file);

                contentArea.setText(doc.text());
                contentArea.setScrollTop(0); // Scroll to top

            } catch (Exception e) {
                // Fallback if parsing fails
                contentArea.setText("Could not generate text preview.\n\n" +
                        "Error: " + e.getMessage() + "\n\n" +
                        "You can still download the file using the button above.");
            }
        }
    }
}