module groupproject.apgroupproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens groupproject.apgroupproject to javafx.fxml;
    exports groupproject.apgroupproject;
}