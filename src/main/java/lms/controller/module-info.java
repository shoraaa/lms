module lms.controller {
    requires javafx.controls;
    requires javafx.fxml;

    opens lms.controller to javafx.fxml;
    exports lms.controller;
}


