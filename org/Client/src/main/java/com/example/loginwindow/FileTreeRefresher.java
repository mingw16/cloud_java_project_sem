package com.example.loginwindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileTreeRefresher{

    ScheduledExecutorService executor;
    public FileTreeRefresher(){
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("mainWindow.fxml"));
            try {
                Parent root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            AppController controller = loader.getController();
            controller.createFileInFileExplorer("qwreqwer","g","gasdf");
        }, 0, 2, TimeUnit.SECONDS);

    }

    public void close(){
        executor.close();
    }
}
