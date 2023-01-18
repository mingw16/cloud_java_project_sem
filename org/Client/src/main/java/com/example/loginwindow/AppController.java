package com.example.loginwindow;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.loginwindow.Scene.setScene;

/**
 * Klasa zarządzjąca plikiem XML - GŁÓWNE OKNO UŻYTKOWNIKA PO ZALOGOWANIU.
 */
public class AppController implements Initializable {

    /* UCHWYTY DO FXML */
    @FXML
    private Button signOutButton;
    @FXML
    private AnchorPane dragArea;
    @FXML
    private VBox userFileArea;
    @FXML
    private Label DragHere;
    @FXML
    private ImageView Logo;
    @FXML
    private ImageView refreshButton;
    @FXML
    private HBox FileVerse;
    HBox newFileVerse;

    /* PRZESYLANMIE PLIKOW DO SERWERA */
    private NetClient netClient;

    /* INICJALIZACJA DANYCH */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        print.log("switch scene to appControler");
        if(NetClientBuilder.Exist() == 1) {netClient = NetClientBuilder.getInstance();}
        else {print.print("fatal error, can not create net client");}


        this.signOutStyle();
        this.activateDragArea();
        this.dragHereStyle();
        this.refreshButtonStyle();
        logoAnimation logoStyle = new logoAnimation(Logo);
        try {
            readDataToFileExplorer();
        } catch (SQLException | IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshButtonStyle(){
        Tooltip refreshTip = new Tooltip("refresh files");
        refreshTip.setShowDelay(Duration.millis(100));
        refreshTip.setStyle("-fx-font-size: 15;");
        Tooltip.install(refreshButton, refreshTip);
    }

    @FXML
    public void readDataToFileExplorer() throws SQLException, IOException, ParseException {
        ArrayList< ArrayList<String>> files =netClient.getFileTree();
        if(files == null) return;
        userFileArea.getChildren().clear();
        userFileArea.getChildren().add(FileVerse);

        for(int i = 0; i < files.size(); i++){
            files.get(i).forEach(x->x.replace("[",""));
            String filename = new File(files.get(i).get(0)).getName();
            String size = files.get(i).get(1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String milis = files.get(i).get(2).replace("]","");
            String date = simpleDateFormat.format(new Date(Long.parseLong(milis)));
            createFileInFileExplorer(filename, size,date);
        }
        /* read file tree from server */
    }

    AnchorPane createDeleteButtonInFileExplorer(String fileName){
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(25, 25);
        anchorPane.setStyle("-fx-background-color: red");

        Image image1 = new Image("file:src/main/resources/ZDJECIA/delete.png");
        Image image2 = new Image("file:src/main/resources/ZDJECIA/_delete.png");

        ImageView imageView = new ImageView();
        imageView.setImage(image1);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);

        setStyleAnchorPane(anchorPane, "#76b5c5");

        anchorPane.addEventHandler(MouseEvent.MOUSE_ENTERED,
                e -> {
                    imageView.setImage(image2);
                    anchorPane.setStyle( "-fx-background-color: rgb(184, 70, 50);");
                });


        anchorPane.addEventHandler(MouseEvent.MOUSE_EXITED,
                e -> {
                    imageView.setImage(image1);
                    setStyleAnchorPane(anchorPane, "#76b5c5");
                });

        anchorPane.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> {
                    new DeleteAlert().display(fileName);
                    try {
                        readDataToFileExplorer();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                });

        anchorPane.getChildren().add(imageView);

        Tooltip deleteTip = new Tooltip("delete file");
        deleteTip.setShowDelay(Duration.millis(100));
        deleteTip.setStyle("-fx-font-size: 15;");
        Tooltip.install(anchorPane, deleteTip);

        return anchorPane;
    }

    void setStyleAnchorPane(AnchorPane anchorPane, String backgroundColor){
        anchorPane.setStyle( "-fx-background-color:" + backgroundColor + ";");
    }

    void createFileInFileExplorer(String fileName, String fileSize, String dateAdded){
        HBox verse = new HBox();
        Label fileNameLabel = new Label();
        Label sizeLabel = new Label();
        Label dateLabel = new Label();

        fileNameLabel.setText(fileName);
        sizeLabel.setText(fileSize+ " bajtów");
        dateLabel.setText(dateAdded);

        setStyleFileNameLabel(fileNameLabel, "#2596be");
        setStyleSizeLabel(sizeLabel, "#2596be");
        setStyleDateLabel(dateLabel, "#2596be");

        verse.setCursor(Cursor.HAND);
        verse.setTranslateY(2);

        verse.addEventHandler(MouseEvent.MOUSE_ENTERED,
                e -> {
                    setStyleFileNameLabel(fileNameLabel, "#76b5c5");
                    setStyleSizeLabel(sizeLabel, "#76b5c5");
                    setStyleDateLabel(dateLabel, "#76b5c5");
                });
        verse.addEventHandler(MouseEvent.MOUSE_EXITED,
                e -> {
                    setStyleFileNameLabel(fileNameLabel, "#2596be");
                    setStyleSizeLabel(sizeLabel, "#2596be");
                    setStyleDateLabel(dateLabel, "#2596be");
                });

        HBox newVerse = new HBox();
        newVerse.getChildren().addAll(fileNameLabel, sizeLabel, dateLabel);

        newVerse.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> {

                    System.out.println(fileNameLabel.getText());
                    try {
                        netClient.downloadFile(fileNameLabel.getText());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    } catch (NoSuchAlgorithmException ex) {
                        throw new RuntimeException(ex);
                    }
                });

        Tooltip tooltip = new Tooltip("download file");
        tooltip.setShowDelay(Duration.millis(100));
        fileNameLabel.setTooltip(tooltip);
        sizeLabel.setTooltip(tooltip);
        dateLabel.setTooltip(tooltip);

        AnchorPane deleteButton = createDeleteButtonInFileExplorer(fileNameLabel.getText());

        //  verse.getChildren().addAll(fileNameLabel, sizeLabel, dateLabel, deleteButton);
        verse.getChildren().addAll(newVerse, deleteButton);
        verse.setStyle("-fx-border-width:  1 0 1 0;" +
                "-fx-border-color:  #0c162d;" );

        userFileArea.getChildren().add(verse);
    }

    /**
     * tworzy animacje na napisie "drag here file"
     */
    public void dragHereStyle(){
        Light.Distant lighting = new Light.Distant();

        Lighting lighting1 = new Lighting();
        lighting1.setSpecularConstant(1.25);
        lighting1.setLight(lighting);
        DragHere.setEffect(lighting1);


        AtomicBoolean effects = new AtomicBoolean(true);
        AtomicReference<AtomicInteger> brightness = new AtomicReference<>(new AtomicInteger(0));


        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(10), evt -> {
                        if (lighting.getAzimuth() > 360 ){
                            brightness.set(new AtomicInteger(0));
                            effects.set(true);
                        }
                        brightness.get().updateAndGet(v -> (int) (v + 1));
                        lighting.setAzimuth(brightness.get().doubleValue());
                      //  colorAdjust.setBrightness(brightness.get().get());
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Tworzy miejsce do upuszczania plików przez użytkownika.
     */
    public void activateDragArea() {
        setDragOver();
        setDragDropped();
    }

    /**
     * Tworzy event działający podczas wykracia przeciągania pliku przez użytkownika,
     * do momentu upuszczenia, ale bez niego.
     */
    void setDragOver(){
        dragArea.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != dragArea
                        && event.getDragboard().hasFiles()) {
                    dragArea.setStyle("-fx-background-color:  #0c162d;"+
                                      "-fx-border-width: 2;" +
                                      "-fx-border-color: blue"      );
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });
    }

    /**
     * Tworzy event do wykrycia upuszczenia pliku przez użytkownika.
     */
    void setDragDropped(){
        dragArea.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                dragArea.setStyle("-fx-background-color: transparent;"+
                                  "-fx-border-width: 2;" +
                                  "-fx-border-color:  #55627e");
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    String path = db.getFiles().toString(); // [C://...]
                    path = path.substring(1, path.length()-1);
                    Path filePath = Paths.get(path);
                    java.io.File file = new java.io.File(path);
                    String fileName; //                 -- NAZWA PLIKU
                    int fileSize; //                    -- ROZMIAR PLIKU
                    String dateAdded; //                -- DATA DODANIA PLIKU
                    // ----------------------- //
                    fileName = String.valueOf(filePath.getFileName()); // pobieranie nazwy pliku
                    if(file.isFile()) {fileSize = (int) file.length();} // pobieranie rozmiaru pliku
                    else {fileSize = folderSize(file);}                 // ^ ^ ^ ^ ^
                    LocalDateTime timeAdded = LocalDateTime.now();  // pobieranie aktualnej daty
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    dateAdded  = dtf.format(timeAdded);
                    int result = 0;
                    for (char c : fileName.toCharArray()) {
                        if (Character.isWhitespace(c)) {
                            result = 1;
                        }
                    }
                    if(new File(filePath.toString()).isFile() && new File(filePath.toString()).length() <5000000 &&  result ==0) {
                        try {
                            netClient.sendFile(String.valueOf(fileSize), filePath.toString());
                            readDataToFileExplorer();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    success = true;
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    @FXML
    void RefreshButtonMouseClicked(MouseEvent event) {
        RotateTransition transition = new RotateTransition(Duration.seconds(2), refreshButton);
        try {
            readDataToFileExplorer();
        } catch (SQLException | IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        transition.setByAngle(360);
        transition.play();
    }

    /**
     * oblicza rommiar katalodu
     * @param directory ścieżka do katalogu
     * @return zwraca ilość KB
     */
    public int  folderSize(File directory) {
        int length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    /**
     * @param fileName nazwa pliku
     * @param fileSize rozmiar pliku
     * @param dateAdded data dodania
     */
    /**
     * @param fileNameLabel
     * @param backgroundColor
     */
    void setStyleFileNameLabel(Label fileNameLabel, String backgroundColor){
        fileNameLabel.setPrefSize(205, 25);
        fileNameLabel.setStyle("-fx-background-color:"+ backgroundColor+ ";" +
                "-fx-font-size: 15;" +
                "-fx-label-padding: 0 0 0 10;");
    }
    /**
     * @param sizeLabel
     * @param backgroundColor
     */
    void setStyleSizeLabel(Label sizeLabel, String backgroundColor){
        sizeLabel.setPrefSize(219, 25);
        sizeLabel.setStyle("-fx-background-color:" + backgroundColor + ";" +
                           "-fx-font-size: 15;" +
                           "-fx-label-padding: 0 0 0 10;" +
                           "-fx-border-color: black;" +
                           "-fx-border-width:  0 2 0 2"             );
    }
    /**
     * @param dateLabel
     * @param backgroundColor
     */
    void setStyleDateLabel(Label dateLabel, String backgroundColor){
        dateLabel.setPrefSize(205, 25);
        dateLabel.setStyle( "-fx-background-color:" + backgroundColor + ";" +
                            "-fx-font-size: 15;" +
                            "-fx-label-padding: 0 0 0 10;"          );
    }


    /**
     * @param event
     */
    @FXML
    void dragAreaMouseExited(MouseEvent event) {
        dragArea.setStyle(  "-fx-background-color: transparent;"+
                            "-fx-border-width: 2;" +
                            "-fx-border-color: #55627e;"               );
    }

    // SIGN OUT TERAZ !!
    @FXML
    void signOutMouseClicked(MouseEvent event) throws IOException {
        //SetEmail("");
        netClient.logout();
        setScene(Scenes.LOGIN);
    }

    @FXML
    void signOutStyle(){
        signOutButton.setStyle("-fx-font-size: 15px;-fx-background-color: #0c162d;-fx-border-width: 2;-fx-border-color: #55627e; -fx-alignment: center");
    }
    @FXML
    void signOutMouseEntered(MouseEvent event) {
        signOutButton.setStyle("-fx-font-size: 18px;-fx-background-color: #0c162d;-fx-border-width: 2;-fx-border-color: #55627e;  -fx-alignment: center");
    }

    @FXML
    void signOutMouseExited(MouseEvent event) {
        signOutButton.setStyle("-fx-font-size: 15px;-fx-background-color: #0c162d;-fx-border-width: 2;-fx-border-color: #55627e; -fx-alignment: center");
    }

}
