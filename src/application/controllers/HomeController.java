package application.controllers;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class HomeController {

    private final File _folder = new File(Main.getCreationDir());
    private ObservableList<String> _items;

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
    private Button btnRefresh;

    @FXML
    private void handleBtnPlay() {
        System.out.println("Playing");
       // System.out.println(Main.getCreationDir());

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

    @FXML
    public void handleBtnRefresh() {
        System.out.println("Refresh");
        refreshList();
    }

    public void refreshList() {
        updateListTree();
    }

    /**
     * Get the creations in the folder.
     */
    public void updateListTree() {
        _items = FXCollections.observableArrayList(listFilesOfFolder(_folder));
        _creationList.setItems(_items);
    }

    /**
     * Get the creations in the folder into an arrylist sorted.
     * @param folder
     * @return
     */
    private ArrayList<String> listFilesOfFolder(final File folder) {
        ArrayList<String> list = new ArrayList<String>();
        for (final File fileEntry : folder.listFiles()) {
            list.add(fileEntry.getName().replace(".mp4", ""));
        }

        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

}
