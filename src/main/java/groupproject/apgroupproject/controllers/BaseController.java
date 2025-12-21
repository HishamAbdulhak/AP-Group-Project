package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.UserSession;
import groupproject.apgroupproject.services.IngestionService;
import groupproject.apgroupproject.services.NavigationService;
import groupproject.apgroupproject.services.RAGService;
import groupproject.apgroupproject.services.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public abstract class BaseController {

    protected static RAGService ragService;
    protected static IngestionService ingestionService;

    // Flag to track if the AI has finished loading documents
    public static boolean isAiReady = false;

    @FXML protected Button homeButton;
    @FXML protected Button aiChatButton;
    @FXML protected Button browseButton;
    @FXML protected Button myProfileButton;
    @FXML protected Button adminDashboard;

    protected NavigationService navService;

    public BaseController() {
        this.navService = new SceneSwitcher();

        // Initialize AI Services only once when the app starts
        if (ragService == null) {
            try {
                System.out.println("Initializing AI Services...");
                ragService = new RAGService();
                ingestionService = new IngestionService(
                        RAGService.createEmbeddingModel(), // Creates a fresh model instance matching config
                        RAGService.getEmbeddingStore()     // Uses the SHARED static memory
                );

                // Run Ingestion in a Background Thread
                // This prevents the application from freezing on startup
                if (!isAiReady) {
                    Thread backgroundThread = new Thread(() -> {
                        System.out.println("App Started. Loading AI Brain in background...");

                        // Load all documents from the folder
                        ingestionService.ingestAllFiles("project_documents");

                        // Mark as ready so ChatController knows it can proceed
                        isAiReady = true;
                        System.out.println("AI is Ready to Chat!");
                    });

                    backgroundThread.start();
                }

                System.out.println("AI Services started successfully.");

            } catch (Exception e) {
                System.err.println("CRITICAL WARNING: AI Services failed to start.");
                System.err.println("Reason: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected void setupSidebar() {
        if (homeButton != null)
            homeButton.setOnAction(e -> navService.navigateTo("HomeScreen.fxml", homeButton));

        if (aiChatButton != null)
            aiChatButton.setOnAction(e -> navService.navigateTo("AIChat.fxml", aiChatButton));

        if (browseButton != null)
            browseButton.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", browseButton));

        if (myProfileButton != null)
            myProfileButton.setOnAction(e -> navService.navigateTo("AccountSettings.fxml", myProfileButton));

        if (adminDashboard != null) {
            // Check if user is Admin to show/hide dashboard button
            if (UserSession.getInstance().isAdmin()) {
                adminDashboard.setVisible(true);
                adminDashboard.setManaged(true);
                adminDashboard.setOnAction(e -> navService.navigateTo("AdminDashboard.fxml", adminDashboard));
            } else {
                adminDashboard.setVisible(false);
                adminDashboard.setManaged(false);
            }
        }
    }
}