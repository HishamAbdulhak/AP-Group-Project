package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.UserSession; // Import UserSession
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.io.File;

public class BrowserController extends BaseController {

    @FXML private TextField searchField;
    @FXML private VBox admissionsBox;
    @FXML private VBox campusBox;
    @FXML private VBox examsBox;
    @FXML private Accordion categoryAccordion;

    // Fallback container for "Other" files
    private VBox othersBox;

    @FXML
    public void initialize() {
        super.setupSidebar();
        createOthersBox();
        refreshFileList("");

        // 1. Search Logic
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                refreshFileList(newValue.toLowerCase());
            });
        }

        // 2. Check for "Quick Access" category from Home Screen
        String category = UserSession.getInstance().getDocumentCategoryAndClear();
        if (category != null) {
            handleCategoryFilter(category);
        }
    }

    private void handleCategoryFilter(String category) {
        if (categoryAccordion == null) return;

        VBox targetBox = null;

        // Map the button names to the correct VBox
        switch (category) {
            case "Admissions":
            case "Financial Aid": // 'Fee' keywords are in admissionsBox
                targetBox = admissionsBox;
                break;
            case "Exams":
                targetBox = examsBox;
                break;
            case "Campus Life":
            case "Library":       // 'Library' keywords are in campusBox
                targetBox = campusBox;
                break;
            case "IT Support":
                targetBox = othersBox;
                break;
            default:
                targetBox = othersBox;
        }

        // Find the TitledPane that holds this VBox and expand it
        if (targetBox != null) {
            for (TitledPane pane : categoryAccordion.getPanes()) {
                if (pane.getContent() == targetBox) {
                    categoryAccordion.setExpandedPane(pane);
                    break;
                }
            }
        }
    }

    private void createOthersBox() {
        othersBox = new VBox(10);
        othersBox.setStyle("-fx-padding: 15;");

        // FIX: Ensure this pane is added to the accordion logic
        TitledPane othersPane = new TitledPane("Other Documents" , othersBox);

        if (categoryAccordion != null){
            categoryAccordion.getPanes().add(othersPane);
        }
    }

    private void sortFileIntoCategory(File file, String nameLower) {
        Hyperlink link = createFileLink(file);

        if (nameLower.contains("exam") || nameLower.contains("grade") ||
                nameLower.contains("test") || nameLower.contains("result") ||
                nameLower.contains("transcript") || nameLower.contains("assessment")) {
            if (examsBox != null) examsBox.getChildren().add(link);
            return;
        }

        if (nameLower.contains("admission") || nameLower.contains("register") ||
                nameLower.contains("apply") || nameLower.contains("application") ||
                nameLower.contains("transfer") || nameLower.contains("fee") ||
                nameLower.contains("id card")) {

            if (admissionsBox != null) admissionsBox.getChildren().add(link);
            return;
        }

        if (nameLower.contains("campus") || nameLower.contains("facility") ||
                nameLower.contains("library") || nameLower.contains("map") ||
                nameLower.contains("parking") || nameLower.contains("room") ||
                nameLower.contains("bus") || nameLower.contains("accommodation")) {

            if (campusBox != null) campusBox.getChildren().add(link);
            return;
        }

        if (othersBox != null) othersBox.getChildren().add(link);
    }

    private void refreshFileList(String query) {
        if (admissionsBox != null) admissionsBox.getChildren().clear();
        if (campusBox != null) campusBox.getChildren().clear();
        if (examsBox != null) examsBox.getChildren().clear();
        if (othersBox != null) othersBox.getChildren().clear();

        File folder = new File("project_documents");

        if (folder.exists() && folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                String name = file.getName().toLowerCase();
                if (name.contains(query)) {
                    sortFileIntoCategory(file, name);
                }
            }
        }
    }

    private void openDocumentViewer(File file) {
        DocumentViewerController.fileToView = file;
        navService.navigateTo("DocumentViewer.fxml", searchField);
    }

    private Hyperlink createFileLink(File file) {
        Hyperlink link = new Hyperlink("📄 " + file.getName());
        link.setStyle("-fx-font-size: 14px; -fx-text-fill: #2980b9; -fx-border-color: transparent;");

        link.setOnAction(e -> {
            openDocumentViewer(file);
        });

        return link;
    }
}