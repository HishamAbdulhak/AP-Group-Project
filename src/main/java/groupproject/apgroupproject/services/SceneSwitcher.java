package groupproject.apgroupproject.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneSwitcher implements NavigationService {

    public static void switchTo(Stage stage, String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneSwitcher.class.getResource("/groupproject/apgroupproject/" + fxmlFileName)
            );
            Parent root = loader.load();

            // Re-use the existing stage (window)
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Failed to load scene: " + fxmlFileName);
            e.printStackTrace();
        }
    }

    public void navigateTo(String fxmlFileName, Button sourceButton) {
        Stage stage = (Stage) sourceButton.getScene().getWindow();
        SceneSwitcher.switchTo(stage, fxmlFileName);
    }
}