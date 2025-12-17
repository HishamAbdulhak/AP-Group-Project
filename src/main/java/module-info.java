module groupproject.apgroupproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires langchain4j.core;
    requires langchain4j.open.ai;


    opens groupproject.apgroupproject to javafx.fxml;
    exports groupproject.apgroupproject;
    exports groupproject.apgroupproject.controllers;
    opens groupproject.apgroupproject.controllers to javafx.fxml;
    exports groupproject.apgroupproject.services;
    opens groupproject.apgroupproject.services to javafx.fxml;
    exports groupproject.apgroupproject.models;
    opens groupproject.apgroupproject.models to javafx.fxml;
}