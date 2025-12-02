package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.services.NavigationService;
import groupproject.apgroupproject.services.SceneSwitcher;
import groupproject.apgroupproject.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

        //Implementing the "Ask AI" button
        askAiButton.setOnAction(e -> {
            String question = questionText.getText();
            System.out.println("User Asked: "+ question);
            //TODO: implement passing text to chat controller
            navService.navigateTo("AIChat.fxml", askAiButton);
        });

        //Implementing Quick Access Grid (All go to Browse Page for now)
        //TODO: onAction passes to Document Viewer
        admissionsButtton.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", admissionsButtton));
        examsAndGrades.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", examsAndGrades));
        campusLife.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", campusLife));
        itSupport.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", itSupport));
        library.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", library));
        financialAid.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", financialAid));

        //Implementing Trending Questions hyperlinks
        //TODO: onAction passes with predefined questions / actions
        resetStudentEmail.setOnAction(e -> {
            //needs to be passed with predefined question and passed to ai chat
            navService.navigateTo("AIChat.fxml", aiChatButton);
        });
        examPDF.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", aiChatButton));
        campusMap.setOnAction(e -> navService.navigateTo("BrowserPage.fxml", aiChatButton));
        courseDeadline.setOnAction(e -> {
            //needs to be passed with predefined questions and passed to ai chat
            navService.navigateTo("AIChat.fxml", aiChatButton);
        });
        clubOptions.setOnAction(e -> {
            //needs to be passed with predefined questions and passed to ai chat
            navService.navigateTo("AIChat.fxml", aiChatButton);
        });

    }

}
