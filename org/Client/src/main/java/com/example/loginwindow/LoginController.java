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
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.example.loginwindow.Scene.*;

/**
 * Klasa zarządzjąca plikiem XML - LOGOWANIEM.
 */
public class LoginController implements Initializable {

    @FXML
    private Button forgotPasswordButton;
    @FXML
    private PasswordField passwordLabel;
    @FXML
    private TextField usernameLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Label incorrectData;
    @FXML
    private Button signUpButton;
    @FXML
    private ImageView Logo;
    @FXML
    private AnchorPane backGround;
    private NetClient netClient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NetClientBuilder.initNetClient("130.61.184.233",8085);
        if(NetClientBuilder.Exist() == 1) {
            netClient = NetClientBuilder.getInstance();
        }
        else{
            print.print("fatal error, can not create net client");
        }


       this.fButtonStyle();
       Snow snow = new Snow(backGround);
       logoAnimation logoStyle = new logoAnimation(Logo);
    }


    /**
     * Obsługuje event po naciśnięciu przycisku "SIGN UP".
     * Zmienia scnene na rejstracje po nakliknięciu na przycisk "SIGN UP".
     * @param event
     * @throws IOException
     */
    @FXML
    void signUpButtonClicked(MouseEvent event) throws IOException {
        /**
         * @link Scene#setScene(Scenes)
         */
        setScene(Scenes.REGISTRATION);
    }

    /**
     * Obsługuje event po naciśnięciu przycisku "LOGIN".
     * @param event
     */
    @FXML
    void loginButtonClicked(MouseEvent event) throws SQLException, IOException, InterruptedException {
        print.log("login button clicked");
        // pobierania danych
        String username = usernameLabel.getText();
        String password = passwordLabel.getText();
        // kasowanie napisów
        usernameLabel.setText("");
        passwordLabel.setText("");

        // sprawdza czy któreś z pól nie jest puste
        if (username.equals("") || password.equals(""))
        {
            incorrectData.setVisible(true);
            return;
        }
        int login_code = netClient.login(username,password);
        if(login_code == 1){
            // zalogowano
            print.print("zalogowano");
            setScene(Scenes.MAINWINDOW);
        }
        else if(login_code == -1){
            // bledne haslo lub email
            incorrectData.setVisible(true);
        }
    }

    /**
     * Forgot Password Button Style - ustawia deafultowy wygląd przycisku.
     */
    @FXML
    void fButtonStyle(){ // #2D3447
        forgotPasswordButton.setStyle(  "-fx-background-color: #0c162d;" +
                                        "-fx-text-fill: #2D3447;" +
                                        "-fx-font-size: 18px;"
        );
    }

    /**
     * Zmienia wygląd przyciku (forgot password) po najechaniu muszką na niego.
     * @param event
     */
    @FXML
    void fButtonStyleMouseEntered(MouseEvent event) {
        forgotPasswordButton.setStyle("-fx-background-color: #0c162d;" +
                                      "-fx-text-fill: #eeeeee;" +
                                      "-fx-font-size: 18px;"
        );
    }
    /**
     * Zmienia wygląd przyciku (forgot password) po zjechaniu muszką z niego.
     * @param event
     */
    @FXML
    void fButtonStyleMouseExited(MouseEvent event) {
        forgotPasswordButton.setStyle("-fx-background-color: #0c162d;" +
                                      "-fx-text-fill: #2D3447;" +
                                      "-fx-font-size: 18px;"
        );
    }

}