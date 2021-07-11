package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/game.fxml"));
        stage.setTitle("TETRIS");
        Scene scene = new Scene(parent);
        scene.getRoot().requestFocus();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(GameApp.class, args);
    }
}