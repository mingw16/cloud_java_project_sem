package com.example.loginwindow;

import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


/**
 * klasa rozpoczynająca program
 * @author Młynarczyk, Śmierciak, Nowicki, Snochowski, Żurek
 * @version 1.0
 */
public class Application extends javafx.application.Application {
    Scene window; // -> zmienia sceny w programie
    //   BazaDanych bazaDanych; // -> dostęp do bazy danych
 //   BazaDanychRMI bazaDanychRMI;
    @Override
    public void start(Stage stage) throws IOException, SQLException {

        window = new Scene(stage);
        stage.setResizable(false);
        /**
         * @link Scene#setScene(Scenes)
         */
        window.setScene(Scenes.LOGIN);


    }

    public static void main(String[] args) {
//
        launch();


    }
}