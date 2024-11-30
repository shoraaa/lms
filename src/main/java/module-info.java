module com.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires atlantafx.base;
    requires spring.jdbc;
    requires spring.security.crypto;

    requires transitive javafx.graphics;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    // add icon pack modules
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.feather;
    requires com.zaxxer.hikari;

    opens com.library to javafx.fxml;
    opens com.library.controller to javafx.fxml;
    opens com.library.model to com.google.gson;

    
    exports com.library;
}


