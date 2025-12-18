package groupproject.apgroupproject.services;

import javafx.scene.control.Button;
import javafx.scene.Node;

public interface NavigationService {
    // This defines the contract: "Any navigator MUST have this method"
    void navigateTo(String fxmlFile, Node node);
}