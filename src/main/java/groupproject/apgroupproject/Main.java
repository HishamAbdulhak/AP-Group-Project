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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInScreen.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setTitle("UniHelp AI - Your Personalized FAQ System");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            // This prints an error if fxml file cannot be found
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}