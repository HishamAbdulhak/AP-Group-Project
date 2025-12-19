package groupproject.apgroupproject.controllers;

import groupproject.apgroupproject.models.AiConfig;
import groupproject.apgroupproject.services.ConfigService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsPageController extends BaseController {

    @FXML private Button backButton;

    @FXML private PasswordField apiKeyField;
    @FXML private TextField modelField;
    @FXML private Slider temperatureSlider;
    @FXML private Label temperatureLabel;

    @FXML private Button saveButton;
    @FXML private Button resetButton;

    private final ConfigService configService = new ConfigService();

    @FXML
    public void initialize() {
        super.setupSidebar();

        // Back navigation
        if (backButton != null) {
            backButton.setOnAction(_ ->
                    navService.navigateTo("AdminDashboard.fxml", backButton)
            );
        }

        // Load existing config
        AiConfig cfg = configService.loadConfig();

        if (apiKeyField != null) {
            apiKeyField.setText(cfg.getApiKey());
        }

        if (modelField != null) {
            modelField.setText(cfg.getChatModel());
        }

        if (temperatureSlider != null) {
            temperatureSlider.setMin(0.0);
            temperatureSlider.setMax(1.0);
            temperatureSlider.setValue(cfg.getTemperature());

            temperatureSlider.valueProperty().addListener((_, __, newV) ->
                    updateTempLabel(newV.doubleValue())
            );
        }

        updateTempLabel(cfg.getTemperature());

        // Save
        if (saveButton != null) {
            saveButton.setOnAction(_ -> save());
        }

        // Reset
        if (resetButton != null) {
            resetButton.setOnAction(_ -> resetDefaults());
        }
    }

    private void save() {
        String apiKey = apiKeyField != null ? apiKeyField.getText() : "";
        String model = modelField != null ? modelField.getText() : "gpt-4o-mini";
        double temp = temperatureSlider != null ? temperatureSlider.getValue() : 0.2;

        configService.saveConfig(apiKey, model, temp);

        new Alert(Alert.AlertType.INFORMATION, "Settings saved successfully.")
                .showAndWait();
    }

    private void resetDefaults() {
        if (apiKeyField != null) {
            apiKeyField.setText("");
        }

        if (modelField != null) {
            modelField.setText("gpt-4o-mini");
        }

        if (temperatureSlider != null) {
            temperatureSlider.setValue(0.2);
        }

        updateTempLabel(0.2);
    }

    private void updateTempLabel(double temp) {
        if (temperatureLabel != null) {
            temperatureLabel.setText(
                    String.format("Temperature (Creativity): %.2f", temp)
            );
        }
    }
}
