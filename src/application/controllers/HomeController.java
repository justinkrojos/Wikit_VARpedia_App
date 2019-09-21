package application.controllers;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    private final File _folder = new File(Main.getCreationDir());
    private ObservableList<String> _items;
    private String _selectedItem = null;

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
    private Button btnPause;

    @FXML
    private Button btnMute;

    @FXML
    private Button btnForward;

    @FXML
    private Button btnBackward;

    @FXML
    private void handleBtnPlay() {
        System.out.println("Playing");
        if (_selectedItem == null) {
            alertNullSelection();
            return;
        }
        //System.out.println(Main.getCreationDir()+"/"+_selectedItem+"/"+_selectedItem+".mp4");
        File fileUrl = new File(Main.getCreationDir()+"/"+_selectedItem+"/"+_selectedItem+".mp4");
        Media video = new Media(fileUrl.toURI().toString());
        MediaPlayer player = new MediaPlayer(video);
        player.setAutoPlay(true);
        MediaView mediaView = new MediaView(player);
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                //Some observable map thing goes here.

            }
        });

        _player.getChildren().add(mediaView);
    }

    @FXML
    private void handleBtnDel() {
        //System.out.println("Deleting");
        if (_selectedItem == null) {
            alertNullSelection();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting...");
        alert.setHeaderText("Are you sure you want to delete " + _selectedItem + "?");
        alert.setContentText("Are you sure? Press okay to delete, cancel to keep.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            String delCmd = "rm -r "+ Main.getCreationDir() + "/" + _selectedItem;
            ProcessBuilder delBuilder = new ProcessBuilder("bash","-c",delCmd);

            Process delProcess = null;
            try {
                delProcess = delBuilder.start();
                delProcess.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            updateListTree();
        }
    }

    @FXML
    private void handleBtnCreate() throws IOException {
        //System.out.println("Creating");
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
        //System.out.println("Refresh");
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
            list.add(fileEntry.getName());//.replace(".mp4", ""));
        }

        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    @FXML
    private void selectItem() {
        _selectedItem = (String) _creationList.getSelectionModel().getSelectedItem();
        //System.out.println(_selectedItem);

    }

    private void alertNullSelection() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Please Select A Creation");
        alert.setHeaderText("No item has been selected.");
        alert.setContentText("Pease select a creation");
        alert.showAndWait();
    }

}
