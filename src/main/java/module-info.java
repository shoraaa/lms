module lms {
    requires javafx.controls;
    requires javafx.fxml;

    opens lms to javafx.fxml;
    exports lms;
}


