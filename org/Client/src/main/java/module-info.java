module com.example.loginwindow {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires json.simple;
    requires java.rmi;


    opens com.example.loginwindow to javafx.fxml;
    exports com.example.loginwindow;
}