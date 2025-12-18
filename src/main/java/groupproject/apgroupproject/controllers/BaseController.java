package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.UserSession;
import groupproject.apgroupproject.services.IngestionService;
import groupproject.apgroupproject.services.NavigationService;
import groupproject.apgroupproject.services.RAGService;
import groupproject.apgroupproject.services.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

// Abstract class because we never use it directly, other controllers extend it
public abstract class BaseController {

    protected static final RAGService.RagService ragService = new RAGService.RagService();

    protected static final IngestionService ingestionService = new IngestionService(
            ragService.getEmbeddingModel(),
            ragService.getEmbeddingStore()
    );
    // These IDs must match the FXML in EVERY file (Home, Chat, Browser, etc.)
    @FXML protected Button homeButton;
    @FXML protected Button aiChatButton;
    @FXML protected Button browseButton;
    @FXML protected Button myProfileButton;
    @FXML protected Button adminDashboard;

    protected NavigationService navService;

    public BaseController() {
        this.navService = new SceneSwitcher();
    }

    // This method sets up the sidebar actions
    protected void setupSidebar() {
        if (homeButton != null)
            homeButton.setOnAction(e -> navService.navigateTo("HomeScreen.fxml", homeButton));

        if (aiChatButton != null)
            aiChatButton.setOnAction(e -> navService.navigateTo("AIChat.fxml", aiChatButton));

        if (browseButton != null)
            browseButton.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", browseButton));

        if (myProfileButton != null)
            myProfileButton.setOnAction(e -> navService.navigateTo("AccountSettings.fxml", myProfileButton));

        if (adminDashboard != null)
            if (UserSession.isAdmin()) {
                adminDashboard.setVisible(true);
                adminDashboard.setManaged(true);
                adminDashboard.setOnAction(e -> navService.navigateTo("AdminDashboard.fxml", adminDashboard));
            } else {
                adminDashboard.setVisible(false);
                adminDashboard.setManaged(false);}

        RAGService RAGService = new RAGService();



    }

}
