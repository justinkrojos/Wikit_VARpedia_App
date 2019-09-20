package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

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

}
