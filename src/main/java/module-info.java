module lms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens lms to javafx.fxml;
    exports lms;
}


