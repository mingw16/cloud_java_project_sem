package com.example.loginwindow;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import static com.example.loginwindow.Scene.*;

/**
 * Klasa zarządzjąca plikiem XML - REJESTRACJA.
 */
public class RegistrationController implements Initializable {

    @FXML
    private PasswordField ConfirmPassword;
    @FXML
    private TextField Email;
    @FXML
    private TextField FirstName;
    @FXML
    private TextField LastName;
    @FXML
    private PasswordField Password;
    @FXML
    private Button RegisterNowButton;
    @FXML
    private Label incorrectData;
    @FXML
    private ImageView Logo;
    @FXML
    private Button signInButton;
    @FXML
    private AnchorPane backGround;
    private NetClient netClient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(NetClientBuilder.Exist() == 1) {
            netClient = NetClientBuilder.getInstance();
        }
        else{
         //   print.print("fatal error, can not create net client");
        }
        Snow snow = new Snow(backGround);
        logoAnimation logoStyle = new logoAnimation(Logo);
    }


    /**
     * Zmienia scene po nakliknięciu na przyski "zaloguj się".
     * @param event
     * @throws IOException
     */
    @FXML
    void signInButtonClicked(MouseEvent event) throws IOException {
        setScene(Scenes.LOGIN);
    }

    /**
     * Akcja po naciśnięciu przycisku "zarejestruj się".
     * @param event
     */
    @FXML
    void RegisterButtonClicked(MouseEvent event) throws SQLException, IOException, NotBoundException {
        // ważne dane
        String firstName = FirstName.getText(); //            -- IMIE UZYRKOWNIKA
        String lastName = LastName.getText(); //              -- NAZWISKO UZYTKOWNIKA
        String email = Email.getText();//                     -- EMAIL UŻYTKONIKA
        String password = Password.getText();//               -- HASŁO UZYTKOWNIKA
        String confirmPassword = ConfirmPassword.getText();// -- POWTÓRZONE HASŁO UŻYTKONWIKA


        // sprawdzenie czy nie ma pustych pól
        if (firstName.equals("") ||
                lastName.equals("") ||
                email.equals("") ||
                password.equals("") ||
                confirmPassword.equals("")) {
            incorrectData.setText("Complete all data!");
            incorrectData.setVisible(true);
            return;
        }

        // sprawdzenie czy hasła nie są za krótkie
        if (password.length() < 6) {
            incorrectData.setText("Too short password!");
            incorrectData.setVisible(true);
            Password.setText("");
            ConfirmPassword.setText("");
            return;
        }

        // sparwdzenie czy email posiada "@"
        int czyToEmail = email.indexOf("@");
        if (czyToEmail == -1) {
            incorrectData.setText("Email is not correct!");
            incorrectData.setVisible(true);
            Email.setText("");
            return;
        }
        // sprawdzanie danych --------------- >>
        if (!checkPassword(password, confirmPassword)) {
            incorrectData.setText("Passwords are not identical!");
            incorrectData.setVisible(true);
            Password.setText("");
            ConfirmPassword.setText("");
        } else {
            int register_result = netClient.registerNewUser(firstName, lastName, email,password) ;
            if(register_result ==1){
                setScene(Scenes.LOGIN);
            }
            else if(register_result == -2){
                incorrectData.setText("this email already exists");
                incorrectData.setVisible(true);
                Email.setText("");
                Password.setText("");
                ConfirmPassword.setText("");
            }
            else{
                incorrectData.setText("upps! something went wrong, try later :)");
                incorrectData.setVisible(true);
                Email.setText("");
                Password.setText("");
                ConfirmPassword.setText("");
            }
        }
    }
        boolean checkPassword(String password, String confirmPassword){
            if(password.equals(confirmPassword)){return true;}
            return false;
        }
}
