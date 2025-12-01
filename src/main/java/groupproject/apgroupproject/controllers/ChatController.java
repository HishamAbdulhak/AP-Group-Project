package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.services.SceneSwitcher;
import groupproject.apgroupproject.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ChatController {
    //Connecting sidebar SceneBuilder fx:id tags with in-code buttons
    @FXML
    private Button homeButton;
    @FXML
    private Button browseButton;
    @FXML
    private Button myProfileButton;
    @FXML
    //Unseen unless user is admin
    private Button adminDashboard;

    @FXML
    public void initialize() {

        //Scene changing when pressing sidebar buttons
        homeButton.setOnAction(e -> navigateTo("HomeScreen.fxml"));
        browseButton.setOnAction(e -> navigateTo("BrowserPage.fxml"));
        myProfileButton.setOnAction(e -> navigateTo("AccountSettings.fxml"));

        if(UserSession.isAdmin()){
            adminDashboard.setVisible(true);
            adminDashboard.setManaged(true); //Allocates space for button in sidebar
            adminDashboard.setOnAction(e -> navigateTo("AdminDashboard.fxml"));
        }
    }

    //Helper Method to reduce repetitive code
    private void navigateTo(String fxmlFile) {
        // Get the stage from one of the buttons that definitely exists
        Stage stage = (Stage) homeButton.getScene().getWindow();
        SceneSwitcher.switchTo(stage, fxmlFile);
    }
}
