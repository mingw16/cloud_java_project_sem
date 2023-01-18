package com.example.loginwindow;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class DeleteAlert {

    NetClient netClient1;
    public DeleteAlert(){
        if(NetClientBuilder.Exist() == 1) {netClient1 = NetClientBuilder.getInstance();}
        else {print.print("fatal error");}
    }
    public void display(String fileName){
        Stage window = new Stage();
    //    window.initStyle(StageStyle.UNDECORATED);
        window.initStyle(StageStyle.TRANSPARENT);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Alert");
        window.setWidth(350);
        window.setHeight(100);

        Label label = new Label();
        label.setText("Are you sure you want to delete this item?");
        label.setStyle("-fx-font-size: 15;" +
                "-fx-font-weight: bold;");
        Button YesButton = new Button("Yes");
        Button NoButton = new Button("No");
        YesButton.setPrefWidth(50);
        NoButton.setPrefWidth(50);

        YesButton.setOnMouseClicked(
                e -> {
                    try {
                        netClient1.deleteFile(fileName);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                    window.close();
                });

        NoButton.setOnMouseClicked(
                e -> {
                    window.close();
                });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(YesButton, NoButton);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, hBox);
        hBox.setAlignment(Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle( "-fx-background-color: rgb(184, 70, 50);" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: black;"
        );


        javafx.scene.Scene scene = new javafx.scene.Scene(layout, Color.TRANSPARENT);
        window.setScene(scene);
        window.showAndWait();
    }
}
