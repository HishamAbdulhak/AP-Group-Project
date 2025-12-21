package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.ChatSession;
import groupproject.apgroupproject.models.UserSession;
import groupproject.apgroupproject.services.AlertService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import java.io.File;

public class ChatController extends BaseController {

    @FXML private VBox messageContainer;
    @FXML private TextField chatInput;
    @FXML private Button sendButton;
    @FXML private ScrollPane chatScrollPane;

    @FXML
    public void initialize() {
        super.setupSidebar();

        // 1. Restore History
        for (ChatSession.ChatMessage msg : ChatSession.getHistory()) {
            addMessageBubble(msg.text, msg.isUser);
        }

        Platform.runLater(() -> {
            if (chatScrollPane != null) chatScrollPane.setVvalue(1.0);
        });

        // 2. Handle Pending Questions (from Dashboard)
        String pending = UserSession.getInstance().getPendingQuestionAndClear();
        if (pending != null) {
            addMessageBubble(pending, true);
            initiateAiTask(pending);
        }

        // 3. Setup Inputs
        if (chatInput != null) {
            chatInput.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) handleSendMessage();
            });
        }
        if (sendButton != null) {
            sendButton.setOnAction(e -> handleSendMessage());
        }
    }

    private void handleSendMessage() {
        String message = chatInput.getText().trim();
        if (message.isEmpty()) return;

        chatInput.clear();

        // 1. Add User Message to UI and History
        ChatSession.addMessage(message, true);
        addMessageBubble(message, true);

        // 2. Trigger the AI Logic
        initiateAiTask(message);
    }

    //Handles the AI logic for both button clicks and pending questions
    private void initiateAiTask(String message) {
        // Safety Check
        if (!BaseController.isAiReady) {
            addMessageBubble("⚠️ I am still reading the university documents. Please wait a moment...", false);
            return;
        }

        Task<String> aiTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                if (ragService == null) return "Error: AI Service not initialized.";
                return ragService.askQuestion(message);
            }
        };

        aiTask.setOnSucceeded(event -> {
            String response = aiTask.getValue();
            ChatSession.addMessage(response, false);
            addMessageBubble(response, false);
        });

        aiTask.setOnFailed(event -> {
            String err = "Sorry, I encountered an error.";
            ChatSession.addMessage(err, false);
            addMessageBubble(err, false);
        });

        new Thread(aiTask).start();
    }

    private void addMessageBubble(String text, boolean isUser) {
        HBox row = new HBox();
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        VBox bubble = new VBox();
        bubble.setMaxWidth(600);
        bubble.setPadding(new Insets(15));

        if (isUser) {
            bubble.setStyle("-fx-background-color: #3498db; -fx-background-radius: 15 15 0 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        } else {
            bubble.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 15 15 15 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        }

        String mainContent = text;
        String citation = null;

        if (!isUser && text.contains("Reference:")) {
            String[] parts = text.split("Reference:", 2);
            mainContent = parts[0].trim();
            if (parts.length > 1) citation = parts[1].trim();
        }

        Label textLabel = new Label(mainContent);
        textLabel.setWrapText(true);
        textLabel.setFont(new Font(14));
        textLabel.setStyle(isUser ? "-fx-text-fill: white;" : "-fx-text-fill: black;");
        bubble.getChildren().add(textLabel);

        if (citation != null && !citation.isEmpty()) {
            Hyperlink sourceLink = new Hyperlink("📄 Open Source: " + citation);
            sourceLink.setFont(Font.font("System", FontPosture.ITALIC, 12));
            sourceLink.setStyle("-fx-text-fill: #2980b9; -fx-border-color: transparent;");

            String fileName = citation;
            sourceLink.setOnAction(e -> {
                File file = new File("project_documents/" + fileName);
                if (file.exists()) {
                    DocumentViewerController.fileToView = file;
                    navService.navigateTo("DocumentViewer.fxml", sendButton);
                } else {
                    new AlertService().showErrorMessage("File Not Found", "Could not find: " + fileName);
                }
            });
            bubble.getChildren().add(sourceLink);
        }

        row.getChildren().add(bubble);
        Platform.runLater(() -> {
            if (messageContainer != null) {
                messageContainer.getChildren().add(row);
                if (chatScrollPane != null) chatScrollPane.setVvalue(1.0);
            }
        });
    }
}