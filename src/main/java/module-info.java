module com.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires atlantafx.base;
    requires spring.jdbc;
    requires transitive javafx.graphics;

    opens com.library to javafx.fxml;
    opens com.library.controller to javafx.fxml;
    opens com.library.model to com.google.gson;

    exports com.library;
}


