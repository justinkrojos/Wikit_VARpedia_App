package application.controllers;

import application.GetImagesTask;
import application.Main;
import application.WikitSearchTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    @FXML
    private TextField _numImageField;

    @FXML
    private Button btnImage;

    @FXML
    private Button btnCheckCreationName;

    @FXML
    private TextArea _textArea;

    @FXML
    private TextField _termField;

    @FXML
    private Button btnSearch;

    @FXML
    private TextField _creationNameField;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCreate;

    @FXML
    public void handleCreationName() {
        String creationDir = Main.getCreationDir();
        String creationFile = creationDir + "/" +_creationNameField.getText();
        String cmd = "[ -e " + creationFile + " ]";
        ProcessBuilder checkName = new ProcessBuilder("bash", "-c", cmd);

        Process checkNamep = null;
        try {
            checkNamep = checkName.start();
            int exitStatus = checkNamep.waitFor();
            if (exitStatus == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Creation Name Error");
                alert.setHeaderText("Creation Name already in use, enter another name or overwrite the existing creation");
                alert.setContentText("WARNING: By selecting overwrite the old creation will be deleted!!!");
                ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType btnOverwrite = new ButtonType("Overwrite");
                alert.getButtonTypes().setAll(btnCancel,btnOverwrite);

                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == btnOverwrite) {
                    String cmdOverwrite = "rm -r " + creationFile;
                    ProcessBuilder overwritePB = new ProcessBuilder("bash", "-c", cmdOverwrite);
                    Process overwriteP = overwritePB.start();
                    overwriteP.waitFor();
                    createNewDir(creationFile);
                    _creationNameField.setDisable(true);
                    btnCheckCreationName.setText("Success!");
                    btnCheckCreationName.setDisable(true);
                }
                return;
            }
            createNewDir(creationFile);
            _creationNameField.setDisable(true);
            btnCheckCreationName.setText("Success!");
            btnCheckCreationName.setDisable(true);



        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createNewDir(String creationFile) {
        ProcessBuilder overwritePB = new ProcessBuilder("bash", "-c", "mkdir -p " + creationFile);
        try {
            Process overwriteP = overwritePB.start();
            overwriteP.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearch() {
        WikitSearchTask task = new WikitSearchTask(_termField.getText());
        team.submit(task);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                //TODO What happens when wikit search fails?? invalid wikie searches not handled.
                if (_termField.getText().isEmpty() | task.getExit() != 0 | task.getOutput() == _termField.getText()+" not found :^(") {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Wikit Search");
                    alert.setHeaderText("Please enter a valid serach term");
                    alert.setContentText("Enter a valid search term and try again.");
                    alert.showAndWait();
                    return;
                }

                _textArea.setText(task.getOutput());
                btnSearch.setText("Success!");
                btnSearch.setDisable(true);
                _termField.setDisable(true);
            }
        });
    }

    @FXML
    private void handleGetImages() {
        if(btnSearch.isDisabled() == false || btnCheckCreationName.isDisabled() == false) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cannot get images");
            alert.setHeaderText(null);
            alert.setContentText("Cannot get images. Make sure a valid term or creation name is entered.");
            alert.showAndWait();
            return;
        }

        try {
            int num = Integer.parseInt(_numImageField.getText());
            if (num <=0 || num > 10) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid number of images");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a number between 1 and 10, inclusive.");
                alert.showAndWait();
                return;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid number of images");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a number between 1 and 10, inclusive.");
            alert.showAndWait();
            return;
        }

        String term = _termField.getText();
        int numImages = Integer.parseInt(_numImageField.getText());
        String creationName = _creationNameField.getText();
        getImages(term,creationName,numImages);
        _numImageField.setDisable(true);
        btnImage.setDisable(true);
    }

    private void getImages(String term, String creationName, int numImages) {
        GetImagesTask task = new GetImagesTask(term, creationName, numImages);
        team.submit(task);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                btnImage.setText("Success!");
            }
        });
    }
}
