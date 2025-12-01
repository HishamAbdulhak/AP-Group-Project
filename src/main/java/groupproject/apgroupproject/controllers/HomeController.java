package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.services.SceneSwitcher;
import groupproject.apgroupproject.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HomeController {

    //Connecting sidebar SceneBuilder fx:id tags with in-code buttons
    @FXML
    private Button homeButton;
    @FXML
    private Button aiChatButton;
    @FXML
    private Button browseButton;
    @FXML
    private Button myProfileButton;
    @FXML
    //Unseen unless user is admin
    private Button adminDashboard;

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

    @FXML
    public void initialize() {

        //Scene changing when pressing sidebar buttons
        aiChatButton.setOnAction(e -> navigateTo("AIChat.fxml"));
        browseButton.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        myProfileButton.setOnAction(e -> navigateTo("AccountSettings.fxml"));

        if(UserSession.isAdmin()){
            adminDashboard.setVisible(true);
            adminDashboard.setManaged(true); //Allocates space for button in sidebar
            adminDashboard.setOnAction(e -> navigateTo("AdminDashboard.fxml"));
        }

        //Implementing the "Ask AI" button
        askAiButton.setOnAction(e -> {
            String question = questionText.getText();
            System.out.println("User Asked: "+ question);
            //TODO: implement passing text to chat controller
            navigateTo("AIChat.fxml");
        });

        //Implementing Quick Access Grid (All go to Browse Page for now)
        //TODO: onAction passes to Document Viewer
        admissionsButtton.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        examsAndGrades.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        campusLife.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        itSupport.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        library.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        financialAid.setOnAction(e -> navigateTo("BrowserPage.fxml"));

        //Implementing Trending Questions hyperlinks
        //TODO: onAction passes with predefined questions / actions
        resetStudentEmail.setOnAction(e -> {
            //needs to be passed with predefined question and passed to ai chat
            navigateTo("AIChat.fxml");
        });
        examPDF.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        campusMap.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        courseDeadline.setOnAction(e -> {
            //needs to be passed with predefined questions and passed to ai chat
            navigateTo("AIChat.fxml");
        });
        clubOptions.setOnAction(e -> {
            //needs to be passed with predefined questions and passed to ai chat
            navigateTo("AIChat.fxml");
        });

    }

    //Helper Method to reduce repetitive code
    private void navigateTo(String fxmlFile) {
        // Get the stage from one of the buttons that definitely exists
        Stage stage = (Stage) aiChatButton.getScene().getWindow();
        SceneSwitcher.switchTo(stage, fxmlFile);
    }

}
