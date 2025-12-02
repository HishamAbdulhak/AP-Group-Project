package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AccountSettingsController extends BaseController {

    @FXML private Button logOut;

    @FXML
    public void initialize() {
        super.setupSidebar();
        logOut.setOnAction(event -> {
            UserSession.cleanSession();
            navService.navigateTo("LogInScreen.fxml", logOut);
        });
    }

}
