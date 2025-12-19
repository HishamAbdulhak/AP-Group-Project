package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.services.NavigationService;
import groupproject.apgroupproject.services.SceneSwitcher;
import groupproject.apgroupproject.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.Node;

public class HomeController extends BaseController {

    //Connecting main content SceneBuilder fx:id tags with in-code buttons
    @FXML
    private TextField questionText;
    @FXML
    private Button askAiButton;

    //Connecting quick access SceneBuilder fx:id tags with in-code buttons
    @FXML
    private Button admissionsButtton;
    @FXML
    private Button examsAndGrades;
    @FXML
    private Button campusLife;
    @FXML
    private Button itSupport;
    @FXML
    private Button library;
    @FXML
    private Button financialAid;

    //Connecting trending questions SceneBuilder fx:id tags with in-code buttons
    @FXML
    private Hyperlink resetStudentEmail;
    @FXML
    private Hyperlink examPDF;
    @FXML
    private Hyperlink campusMap;
    @FXML
    private Hyperlink courseDeadline;
    @FXML
    private Hyperlink clubOptions;

    public HomeController() {
        this.navService = new SceneSwitcher();
    }

    @FXML
    public void initialize() {
        super.setupSidebar();

        // 1. Implementing the "Ask AI" button
        askAiButton.setOnAction(e -> {
            String question = questionText.getText();
            if (question != null && !question.trim().isEmpty()) {
                System.out.println("User Asked: " + question);

                // Store the question in the session so the Chat Controller can read it
                UserSession.getInstance().setPendingQuestion(question);

                navService.navigateTo("AIChat.fxml", askAiButton);
            } else {
                // Optional: Highlight the text field if empty
                questionText.setPromptText("Please type a question first...");
            }
        });

        // 2. Implementing Quick Access Grid
        // We use a helper method 'openBrowser' to handle setting the category
        admissionsButtton.setOnAction(e -> openBrowser("Admissions", admissionsButtton));
        examsAndGrades.setOnAction(e -> openBrowser("Exams", examsAndGrades));
        campusLife.setOnAction(e -> openBrowser("Campus Life", campusLife));
        itSupport.setOnAction(e -> openBrowser("IT Support", itSupport));
        library.setOnAction(e -> openBrowser("Library", library));
        financialAid.setOnAction(e -> openBrowser("Financial Aid", financialAid));

        // 3. Implementing Trending Questions hyperlinks

        // A. Links that lead to the AI Chat with a specific question
        resetStudentEmail.setOnAction(e -> askSpecificQuestion("How do I reset my student email?", resetStudentEmail));
        courseDeadline.setOnAction(e -> askSpecificQuestion("When are the course deadlines for this semester?", courseDeadline));
        clubOptions.setOnAction(e -> askSpecificQuestion("What clubs are available to join?", clubOptions));

        // B. Links that lead to the Document Browser (PDFs/Maps)
        examPDF.setOnAction(e -> openBrowser("Exam Schedules", examPDF));
        campusMap.setOnAction(e -> openBrowser("Campus Map", campusMap));
    }

    private void openBrowser(String category, Node sourceNode) {
        // Set the filter in the session
        UserSession.getInstance().setDocumentCategory(category);
        System.out.println("Navigating to browser with category: " + category);
        navService.navigateTo("BrowserPage.fxml", sourceNode);
    }

    private void askSpecificQuestion(String question, Node sourceNode) {
        // Set the question in the session
        UserSession.getInstance().setPendingQuestion(question);
        System.out.println("Navigating to chat with preset question: " + question);
        navService.navigateTo("AIChat.fxml", sourceNode);
    }
}