package application.controllers;

import application.WikitSearchTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

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
    public void handleSearch() {
        WikitSearchTask task = new WikitSearchTask(_termField.getText());
        team.submit(task);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (_termField.getText().isEmpty() | task.getExit() != 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Wikit Search");
                    alert.setHeaderText("Please enter a valid serach term");
                    alert.setContentText("Enter a valid search term and try again.");
                    alert.showAndWait();
                    return;
                }
                _textArea.setText(task.getOutput());
            }
        });


    }

    // Method below only handles when one chunk is highlighted
    @FXML
    public void handleCreate() {

        System.out.println(_creationNameField.getSelectedText());

        if (_termField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Please enter a valid searchh term");
            alert.setContentText("Enter a valid search term and try again.");
            alert.showAndWait();
        }
        else if (_creationNameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Please enter a valid creation term");
            alert.setContentText("A creation term was not entered.");
            alert.showAndWait();
        }
        else {
            if (_textArea.getSelectedText().isEmpty()) { // if none highighted, selects all text by default??
                System.out.println(_textArea.getText());
            }
            else {
                System.out.println(_textArea.getSelectedText());
            }
        }

    }
            
}
