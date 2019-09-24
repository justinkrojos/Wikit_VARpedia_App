package application.controllers;

import application.GetImagesTask;
import application.Main;
import application.Voices;
import application.WikitSearchTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
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
    private TextField _audioName;

    @FXML
    private ListView<HBox> _audioList;

    @FXML
    private ChoiceBox<String> voicesChoiceBox;

    @FXML
    private Button btnPreviewAudio;

    @FXML
    private Button btnSaveAudio;

    private Button btnDeleteAudio; // dynamically added

    @FXML
    public void handleCreationName() {
        if(!_creationNameField.getText().matches("[a-zA-Z0-9_-]*") || _creationNameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid Creation name");
            alert.setContentText("Please enter a valid non-empty creation name, allowed characters: a-z A-Z 0-9 _ -");
            alert.showAndWait();
            return;
        }
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

    // Method below only handles when one chunk is highlighted
    @FXML
    public void handleAudioPreview() throws IOException {

        // System.out.println(_creationNameField.getSelectedText());

        if (_textArea.getSelectedText().isEmpty()) { // if none highighted, alerts that none was highlighted
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please highlight a maximum of 20 words and try again.");
            alert.showAndWait();
        }
        else {
            String[] words = _textArea.getSelectedText().split("\\s+");
            if (words.length > 20) { // alerts if maxmimum word length exceeded
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Wikit Search");
                alert.setHeaderText("Word Maximum Exceeded");
                alert.setContentText("Please highlight a maximum of 20 words and try again.");
                alert.showAndWait();
            }
            else { // tts

                // PREVIEW AS FEMALE - echo {"(voice_akl_nz_cw_cg_cg)",'(SayText "Hello There Young Sir")'} | bash -c festival
                // PREVIEW AS MALE - echo {"(voice_akl_nz_jdt_diphone)",'(SayText "Hello There Young Sir")'} | bash -c festival

                String cmd = "echo \"" + _textArea.getSelectedText() + "\" | festival --tts";
                ProcessBuilder previewAudiopb1 = new ProcessBuilder("bash", "-c", cmd);
                Process process1 = previewAudiopb1.start();
            }
        }
    }

    @FXML
    public void handleSaveAudioBtn(ActionEvent event) throws IOException {

        //ERROR HANDLING
        if (!btnSearch.getText().equals("Success!")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please wikit search a term and try again.");
            alert.showAndWait();
        }
        else if (_textArea.getSelectedText().isEmpty()) { // if none highighted, alerts that none was highlighted
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please highlight a maximum of 20 words and try again.");
            alert.showAndWait();
        }
        else if (_audioName.getText().isEmpty()) { // No audio name given
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Audio file is unnamed");
            alert.setContentText("Please enter a name for the audio file and try again.");
            alert.showAndWait();
        }
        else if (!btnCheckCreationName.isDisabled()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Creation is unnamed");
            alert.setContentText("Please name your creation and try again.");
            alert.showAndWait();
        }
        else {
            String[] words = _textArea.getSelectedText().split("\\s+");
            if (words.length > 20) { // alerts if maxmimum word length exceeded
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Wikit Search");
                alert.setHeaderText("Word Maximum Exceeded");
                alert.setContentText("Please highlight a maximum of 20 words and try again.");
                alert.showAndWait();
            }
            else { // Create file
                // AUDIO NAME error handling? i.e. AUDIO NAME already exists?


                // System.out.println(getVoicesObject(voicesChoiceBox.getSelectionModel().getSelectedItem()).getVoicePackage());
                // System.out.println(Voices.Voice1.getVoicePackage());

                String cmd = "mkdir -p " + Main.getCreationDir() + "/" + _creationNameField.getText() + "/audio && " +
                        "echo \"" + _textArea.getSelectedText() + "\" | text2wave -o " + Main.getCreationDir() + "/" + _creationNameField.getText() + "/audio/'" + _audioName.getText() + "'.wav -eval \"" +
                        getVoicesObject(voicesChoiceBox.getSelectionModel().getSelectedItem()).getVoicePackage() + "\"";

                ProcessBuilder saveAudiopb = new ProcessBuilder("bash", "-c", cmd);
                Process process1 = saveAudiopb.start();

                Text audioLabel = new Text(_audioName.getText());

                btnDeleteAudio = new Button("Delete");
                Region region1 = new Region();

                HBox hb = new HBox(audioLabel, region1, btnDeleteAudio);
                hb.setHgrow(region1, Priority.ALWAYS);
                hb.setAlignment(Pos.CENTER_LEFT);

                _audioList.getItems().addAll(hb);

                // _audioList.getItems().add(_audioName.getText() + " [" + button.getText() + "]");



                btnPreviewAudio.setDisable(false);
                btnSaveAudio.setDisable(false);


                // DELETE BUTTON EVENT HANDLING

                String cmd2 = "rm '" + Main.getCreationDir() + "/" + _creationNameField.getText() + "/audio/" + _audioName.getText() + "'.wav";

                _audioName.clear();
                // Add success?
                _audioName.setPromptText("Name Selected Audio");

                final String cmdToDelete = cmd2;
                final HBox hbToDelete = hb;




                btnDeleteAudio.setOnAction(new EventHandler<ActionEvent>() { // Confirmation message?
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        // System.out.println(hbToDelete);
                        _audioList.getItems().remove(hbToDelete);

                        if (_audioList.getItems().size() == 0) {
                            btnPreviewAudio.setDisable(true);
                            btnSaveAudio.setDisable(true);
                        }
                        // System.out.println(cmd2);

                        ProcessBuilder deleteAudiopb = new ProcessBuilder("bash", "-c", cmdToDelete);
                        try {
                            Process deleteProcess = deleteAudiopb.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                );
            }



        }
        // Save btn should create mp3 file and store it into a directory
        // Male or Female voices (for now)
        // When Create btn clicked: pop-up window that shows all mp3 files, with male/female button included that previews audio
    }


    public Voices getVoicesObject(String voiceCode) {
        if (voiceCode.equals("Voice1")) {
            return Voices.Voice1;
        }
        else if (voiceCode.equals("Voice2")) {
            return Voices.Voice2;
        }
        else {
            return Voices.Voice3;
        }
    }


    @FXML
    public void handlePreviewBtn() throws IOException, InterruptedException {
        for (int i = 0; i < _audioList.getItems().size(); i++) {
            Text audioListLabel = (Text)_audioList.getItems().get(i).getChildren().get(0);

            String cmd = "ffplay -autoexit '" + Main.getCreationDir() + "/" + _creationNameField.getText() + "/audio/" + audioListLabel.getText() + ".wav'";
            // System.out.println(cmd);

            ProcessBuilder playFullAudiopb = new ProcessBuilder("bash", "-c", cmd);
            Process playAudioProcess = playFullAudiopb.start();
            playAudioProcess.waitFor();

            // System.out.println(audioListLabel.getText());
            // System.out.println(_audioList.getItems().get(i).getChildren().get(0));
            // System.out.println("REACHED");
        }
    }

}
