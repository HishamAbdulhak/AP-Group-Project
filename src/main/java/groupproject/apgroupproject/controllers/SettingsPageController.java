package groupproject.apgroupproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SettingsPageController extends BaseController{
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        super.setupSidebar();

        backButton.setOnAction(e -> navService.navigateTo("AdminDashboard.fxml", backButton));
    }
}

