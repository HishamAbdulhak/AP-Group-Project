package groupproject.apgroupproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneSwitcher {

    public static void switchTo(Stage stage, String fxmlFileName) {
        try {
            // The path assumes FXML files are in: src/main/resources/groupproject/apgroupproject/
            // Change the path based on the structure we discussed earlier.
            FXMLLoader loader = new FXMLLoader(
                    SceneSwitcher.class.getResource("/groupproject/apgroupproject/" + fxmlFileName)
            );
            Parent root = loader.load();

            // Re-use the existing stage (window)
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Failed to load scene: " + fxmlFileName);
            e.printStackTrace();
            // Handle error, maybe show a popup to the user
        }
    }
}