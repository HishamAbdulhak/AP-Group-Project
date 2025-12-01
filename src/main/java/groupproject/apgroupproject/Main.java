package groupproject.apgroupproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            //Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Log In Screen.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setTitle("My Group Project"); // Optional: Give the window a title
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            // This prints an error if fxml file cannot be found
            e.printStackTrace();
        }
    }

    // 4. The standard main method to launch the application
    public static void main(String[] args) {
        launch();
    }
}