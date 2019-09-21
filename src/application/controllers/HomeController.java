package application.controllers;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private Text _title;

    @FXML
    private Text _creationTitle;

    @FXML
    private ListView _creationList;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnDel;

    @FXML
    private Button btnCreate;

    @FXML
    private Pane _player;

    @FXML
    private void handleBtnPlay() {
        System.out.println("Playing");
    }

    @FXML
    private void handleBtnDel() {
        System.out.println("Deleting");
    }

    @FXML
    private void handleBtnCreate() throws IOException {
        System.out.println("Creating");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("resources/Create.fxml"));
        Parent layout = loader.load();
        Scene scene = new Scene(layout);
        Stage creationStage = new Stage();
        creationStage.setScene(scene);
        creationStage.show();
    }

}
