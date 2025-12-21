package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.DocumentsMetadata;
import groupproject.apgroupproject.services.AppLogger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import java.util.List;
import javafx.scene.control.TableCell;

public class AdminController extends BaseController {

    @FXML private Button adminSettings;
    @FXML private Button uploadButton;
    @FXML private VBox dropZone;
    @FXML private TableView<DocumentsMetadata> filesTable;
    @FXML private TableColumn<DocumentsMetadata, String> fileNameCol;
    @FXML private TableColumn<DocumentsMetadata, LocalDate> fileDateCol;
    @FXML private TableColumn<DocumentsMetadata, String> fileStatusCol;
    @FXML private TableColumn<DocumentsMetadata, String> fileActionsCol;

    @FXML private Label totalDocuments;
    @FXML private Label lastUpdateTime;

    @FXML
    public void initialize() {
        super.setupSidebar();

        //Setup the Table Columns
        if (fileNameCol != null) {
            fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        }

        if (fileDateCol != null) {
            fileDateCol.setCellValueFactory(new PropertyValueFactory<>("uploadDate"));
        }

        if (fileStatusCol != null) {
            fileStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

        //Connect the Settings Button
        if (adminSettings != null) {
            adminSettings.setOnAction(e -> navService.navigateTo("SettingsPage.fxml", adminSettings));
        }

        //Connect the Upload Button
        if (uploadButton != null) {
            uploadButton.setOnAction(e -> handleFileChooser());
        }

        if (dropZone != null) {
            setupDragAndDrop();
        }

        if (fileActionsCol != null) {
            setupActionsColumn();
        }

        //This adds file persistence, the files will all load when the app is re-opened.
        loadExistingFile();
    }

    //The Logic
    private void handleFileChooser() {
        FileChooser fileChooser = new FileChooser();

        // Allow PDF, DOCX, and TXT
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported Files", "*.pdf", "*.docx", "*.txt"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Documents", "*.docx"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        // Open the window
        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null) {
            saveFile(selectedFile);
        }
    }

    private void setupDragAndDrop() {
        // Change color to show "Active"
        dropZone.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != dropZone && event.getDragboard().hasFiles()) {
                // Allow the drop
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                // Visual feedback (Darker Blue)
                dropZone.setStyle("-fx-background-color: #e8f6ff; -fx-border-color: #2980b9; -fx-border-width: 2; -fx-border-style: dashed; -fx-background-radius: 10; -fx-border-radius: 10;");
            }
            event.consume();
        });

        //Drag Exit - Reset color
        dropZone.setOnDragExited((DragEvent event) -> {
            dropZone.setStyle("-fx-background-color: #F0F8FF; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-style: dashed; -fx-background-radius: 10; -fx-border-radius: 10;");
            event.consume();
        });

        //Dropped - Get the file
        dropZone.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                List<File> files = db.getFiles();
                for (File file : files) {
                    saveFile(file);
                }
            }
            event.setDropCompleted(success);
            event.consume();

            // Reset style
            dropZone.setStyle("-fx-background-color: #F0F8FF; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-style: dashed; -fx-background-radius: 10; -fx-border-radius: 10;");
        });
    }

    // Saves file and updates Table
    private void saveFile(File sourceFile) {
        try {
            //Ensure the storage directory exists
            File destDir = new File("project_documents");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            //Define the destination file
            File destFile = new File(destDir, sourceFile.getName());

            //Copy the file (Overwrite if exists)
            Files.copy(
                    sourceFile.toPath(),
                    destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            if (ingestionService != null) {
                try {
                    System.out.println("Ingesting file into AI Memory: " + destFile.getName());
                    ingestionService.ingestFile(destFile); // <--- THIS MAKES THE AI SMART
                    System.out.println("Ingestion Successful!");
                } catch (Exception e) {
                    System.err.println("AI Ingestion Failed (Chat won't know this file): " + e.getMessage());
                }
            }

            //Resets file modify time to NOW so "Last Update Time" can be measured accurately
            destFile.setLastModified(System.currentTimeMillis());

            //Add to the Table
            DocumentsMetadata newMeta = new DocumentsMetadata(
                    sourceFile.getName(),
                    LocalDate.now(),
                    "Uploaded"
            );

            //This adds the row to the UI
            filesTable.getItems().add(newMeta);

            // Update the stats immediately after saving
            updateDashboardStats();

            System.out.println("Success! File saved to: " + destFile.getAbsolutePath());
            AppLogger.info("File uploaded: " + sourceFile.getName());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to save file: " + e.getMessage());
        }
    }

    //This adds file persistence, the files will all load when the app is re-opened.
    private void loadExistingFile() {
        File folder = new File("project_documents");

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                // Bulk ingest to ensure AI knows about these files on startup
                if (ingestionService != null) {
                    System.out.println("Reloading existing files into AI memory...");
                }

                for (File file : files) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".pdf") || name.endsWith(".docx") || name.endsWith(".txt")) {

                        // 1. Add to UI
                        LocalDate fileDate = java.time.Instant.ofEpochMilli(file.lastModified())
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        DocumentsMetadata metadata = new DocumentsMetadata(file.getName(), fileDate, "Stored");
                        filesTable.getItems().add(metadata);

                        // 2. Add to AI (CRITICAL FIX)
                        if (ingestionService != null) {
                            try {
                                ingestionService.ingestFile(file);
                            } catch (Exception e) {
                                System.err.println("Error re-ingesting " + name);
                            }
                        }
                    }
                }
                updateDashboardStats();
            }
        }
    }

    // This adds the number of documents to the Total Documents block and calculates time
    private void updateDashboardStats() {
        // Update Total Count
        if (totalDocuments != null) {
            totalDocuments.setText(String.valueOf(filesTable.getItems().size()));
        }

        // Find Last Update Time
        if (lastUpdateTime != null) {
            File folder = new File("project_documents");
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();

                if (files != null && files.length > 0) {
                    long latestTime = 0;
                    for (File f : files) {
                        if (f.lastModified() > latestTime) {
                            latestTime = f.lastModified();
                        }
                    }

                    if (latestTime > 0) {
                        lastUpdateTime.setText(formatTimeAgo(latestTime));
                    }
                } else {
                    lastUpdateTime.setText("N/A");
                }
            }
        }
    }

    private void setupActionsColumn() {
        fileActionsCol.setCellFactory(param -> new TableCell<>() {
            // Create the button once
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setMaxWidth(Double.MAX_VALUE);
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px;");

                // When clicked -> Call our helper method
                deleteBtn.setOnAction(event -> {
                    DocumentsMetadata doc = getTableView().getItems().get(getIndex());
                    deleteDocument(doc);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                // If row is empty, show nothing. Otherwise, show button.
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private String formatTimeAgo(long lastModifiedMillis) {
        long now = System.currentTimeMillis();
        long diff = now - lastModifiedMillis;

        long minutes = diff / (1000 * 60);
        long hours = diff / (1000 * 60 * 60);
        long days = diff / (1000 * 60 * 60 * 24);

        if (diff < 60_000) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else if (days == 1) {
            return "Yesterday";
        } else {
            return days + " days ago";
        }
    }

    private void deleteDocument(DocumentsMetadata document) {
        if (document == null) return;

        File file = new File("project_documents", document.getFileName());
        if (file.exists()) {
            file.delete();
        }

        filesTable.getItems().remove(document);
        updateDashboardStats();
    }
}