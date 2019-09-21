package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage _primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        //System.out.println(this.getClass().getResource("resources/Home.fxml"));

        _primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("resources/Home.fxml"));
        Parent layout = loader.load();
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return _primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
