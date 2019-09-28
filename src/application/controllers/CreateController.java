package application.controllers;

import application.*;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    @FXML
    private AnchorPane _ap;

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
    private ChoiceBox<String> voicesChoiceBox1;

    @FXML
    private Button btnPreviewAudio;

    @FXML
    private Button btnStopAudio;

    private Button btnDeleteAudio; // dynamically added

    private Stage _currentStage;
    private HomeController _homeController;


    /**
     * Check if creation name is taken, and if so let the user pick if they want to overwrite
     */
    @FXML
    private Button btnSaveAudioFile;

    ArrayList<String> existingAudio = new ArrayList<String>();

    public void setUp(Stage stage, HomeController homeController){
        _currentStage = stage;
        _homeController = homeController;
    }

    public void initialize(){
        btnCreate.setVisible(false);
        btnCreate.setDisable(true);

        // _currentStage = (Stage) _ap.getScene().getWindow();
    }

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
        //String cmd = "[ -e " + creationFile + " ]";
        String cmd = "[ -e " + creationFile+".mp4" + " ] || [ -e " + creationFile + " ]";
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
                    String cmdOverwrite = "rm -r " + creationFile + " " + creationFile + ".mp4";
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

    /**
     * Create new direction with name of creation.
     * @param creationFile
     */
    private void createNewDir(String creationFile) {
        ProcessBuilder overwritePB = new ProcessBuilder("bash", "-c", "mkdir -p " + creationFile);
        try {
            Process overwriteP = overwritePB.start();
            overwriteP.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle the wikit search term.
     */
    @FXML
    public void handleSearch() {
        WikitSearchTask task = new WikitSearchTask(_termField.getText());
        team.submit(task);
        btnSearch.setDisable(true);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                //TODO What happens when wikit search fails?? invalid wikie searches not handled.
                if (_termField.getText().isEmpty() | task.getExit() != 0 | task.getOutput().equals( _termField.getText()+" not found :^(")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Wikit Search");
                    alert.setHeaderText("Please enter a valid serach term");
                    alert.setContentText("Enter a valid search term and try again.");
                    alert.showAndWait();
                    btnSearch.setDisable(false);
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
        //TODO CHECK audio save button is pressed.
        if(btnSearch.isDisabled() == false || btnCheckCreationName.isDisabled() == false || !btnSaveAudioFile.getText().equals("Save and Overwrite")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cannot get images");
            alert.setHeaderText(null);
            alert.setContentText("Cannot get images. Make sure a valid term or creation name is entered. And make sure you have a saved audio file.");
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

    @FXML
    private void mergeVideoAudio(){
/*        if (!btnImage.getText().equals("Success!")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot merge video and audio file");
            alert.setHeaderText(null);
            alert.setContentText("Please check that the Make Video(under the Get image");
            alert.showAndWait();
            return;
        }*/
        String creationName = _creationNameField.getText();
        MergeTask task = new MergeTask(creationName);
        team.submit(task);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                btnCreate.setText("Success!");
            }
        });
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creation Complete");
        alert.setHeaderText(null);
        alert.setContentText("Creation complete, please refresh list of creations.");
        alert.showAndWait();
        _homeController.updateListTree();
    }

    private void getImages(String term, String creationName, int numImages) {
        GetImagesTask task = new GetImagesTask(term, creationName, numImages);
        team.submit(task);
        _currentStage.close();
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                btnImage.setText("Success!");
                mergeVideoAudio();


            }
        });
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creation is being created.");
        alert.setHeaderText(null);
        alert.setContentText("Creation is being created, you will get a popup when its done.");
        alert.showAndWait();
    }

    // Method below only handles when one chunk is highlighted
    @FXML
    public void handleAudioPreview() throws IOException {

        // System.out.println(_creationNameField.getSelectedText());

        PreviewAudioTask previewAudioTask = new PreviewAudioTask(_textArea.getSelectedText(), getVoicesObject(voicesChoiceBox1.getSelectionModel().getSelectedItem()).getVoicePackage());

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

                team.submit(previewAudioTask);

                // PREVIEW AS FEMALE - echo {"(voice_akl_nz_cw_cg_cg)",'(SayText "Hello There Young Sir")'} | bash -c festival
                // PREVIEW AS MALE - echo {"(voice_akl_nz_jdt_diphone)",'(SayText "Hello There Young Sir")'} | bash -c festival


            }
        }
    }

    @FXML
    public void handleSaveAudioBtn(ActionEvent event) throws IOException, InterruptedException {

        //ERROR HANDLING
        if (!btnSearch.getText().equals("Success!")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please wikit search a term and try again.");
            alert.showAndWait();
        } else if (_textArea.getSelectedText().isEmpty()) { // if none highighted, alerts that none was highlighted
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please highlight a maximum of 20 words and try again.");
            alert.showAndWait();
        } else  if (!_audioName.getText().matches("[a-zA-Z0-9_-]*") || _audioName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Audio file is unnamed");
            alert.setContentText("Please enter a name for the audio file and try again.");
            alert.showAndWait();
        } else if (!btnCheckCreationName.isDisabled()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Creation is unnamed");
            alert.setContentText("Please name your creation and try again.");
            alert.showAndWait();
        } else if (_textArea.getSelectedText().split("\\s+").length > 20) { // alerts if maxmimum word length exceeded
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Word Maximum Exceeded");
            alert.setContentText("Please highlight a maximum of 20 words and try again.");
            alert.showAndWait();
        } else if (existingAudio.contains(_audioName.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Audio name taken");
            alert.setContentText("Please rename your audio and try again.");
            alert.showAndWait();
        }// Create file
        // AUDIO NAME error handling? i.e. AUDIO NAME already exists?


        else {


            CreateAudioTask createAudioTask = new CreateAudioTask(_creationNameField.getText(), _textArea.getSelectedText(), _audioName.getText(), getVoicesObject(voicesChoiceBox.getSelectionModel().getSelectedItem()));
            team.submit(createAudioTask);
            ;

            createAudioTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    try {

                        if (createAudioTask.getError()) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Wikit Search");
                            alert.setHeaderText("The selected words cannot be synthesised");
                            alert.setContentText("Please highlight other words and try again. Otherwise use Voice1 for better results.");
                            alert.showAndWait();

                        } else {
                            existingAudio.add(_audioName.getText());


                            Text audioLabel = new Text(_audioName.getText());

                            btnDeleteAudio = new Button("Delete");
                            btnDeleteAudio.setVisible(false);

                            Region region1 = new Region();

                            HBox hb = new HBox(audioLabel, region1, btnDeleteAudio);
                            hb.setHgrow(region1, Priority.ALWAYS);
                            hb.setAlignment(Pos.CENTER_LEFT);

                            _audioList.getItems().addAll(hb);

                            // _audioList.getItems().add(_audioName.getText() + " [" + button.getText() + "]");


                            if ((btnStopAudio.isDisabled() && !btnPreviewAudio.isDisabled()) || (btnStopAudio.isDisabled() && btnPreviewAudio.isDisabled())) {
                                btnPreviewAudio.setDisable(false);
                            }

                            btnSaveAudioFile.setDisable(false);


                            // DELETE BUTTON EVENT HANDLING

                            String cmd2 = "rm '" + Main.getCreationDir() + "/" + _creationNameField.getText() + "/audio/" + _audioName.getText() + "'.wav";

                            final String cmdToDelete = cmd2;
                            final HBox hbToDelete = hb;
                            final Button btnToEnable = btnDeleteAudio;

                            // DEL BTN visible when listitem clicked
                            _audioList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {

                                    for (int i = 0; i < _audioList.getItems().size(); i++) {

                                        HBox hb = _audioList.getItems().get(i);
                                        Button button = (Button) hb.getChildren().get(2);
                                        button.setVisible(false);
                                        if (_audioList.getSelectionModel().getSelectedItem().equals(hb)) {
                                            button.setVisible(true);
                                        }
                                    }
                                }
                            });

                            btnDeleteAudio.setOnAction(new EventHandler<ActionEvent>() { // Confirmation message?
                                                           @Override
                                                           public void handle(ActionEvent actionEvent) {
                                                               // System.out.println(hbToDelete);
                                                               Text textToDelete = (Text) hbToDelete.getChildren().get(0);
                                                               existingAudio.remove(textToDelete.getText());
                                                               _audioList.getItems().remove(hbToDelete);
                                                               if (_audioList.getItems().size() == 0) {
                                                                   btnPreviewAudio.setDisable(true);
                                                                   btnSaveAudioFile.setDisable(true);
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




                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }

                }
            });


            // Save btn should create mp3 file and store it into a directory
            // Male or Female voices (for now)
            // When Create btn clicked: pop-up window that shows all mp3 files, with male/female button included that previews audio



    /* public Voices getVoicesObject(String voiceCode) {
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
    */
        }
        _audioName.clear();
        // Add success?
        _audioName.setPromptText("Name Selected Audio");
    }


    @FXML
    public void handlePreviewBtn() throws IOException, InterruptedException {

        btnPreviewAudio.setDisable(true);
        btnStopAudio.setDisable(false);

        AudioMergeTask audioMergeTask = new AudioMergeTask(_creationNameField.getText(), _audioList, btnPreviewAudio.isDisabled());
        team.submit(audioMergeTask);

        audioMergeTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                btnPreviewAudio.setDisable(false);
                btnStopAudio.setDisable(true);
            }
        });
/*
        String cmd = "sox";

        for (int i = 0; i < _audioList.getItems().size(); i++) {
            Text audioListLabel = (Text)_audioList.getItems().get(i).getChildren().get(0);

            cmd = cmd + " '" + Main.getCreationDir() + "/" + _creationNameField.getText() + "/audio/" + audioListLabel.getText() + ".wav'";
            // System.out.println(cmd);
        }
        cmd = cmd + " '" + Main.getCreationDir() + "/" + _creationNameField.getText() + "/" + _creationNameField.getText() + ".wav'" +
                " && ffplay -autoexit -nodisp '" + Main.getCreationDir() + "/" + _creationNameField.getText() + "/" + _creationNameField.getText() + ".wav'";

        // System.out.println(cmd);
        ProcessBuilder playFullAudiopb = new ProcessBuilder("bash", "-c", cmd);
        Process playAudioProcess = playFullAudiopb.start();
*/


        btnStopAudio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                audioMergeTask.stopProcess();
                btnPreviewAudio.setDisable(false);
                btnStopAudio.setDisable(true);

            }
        });

    }

    @FXML
    public void handleSaveFinalAudioBtn() {
        AudioMergeTask audioMergeTask = new AudioMergeTask(_creationNameField.getText(), _audioList, btnPreviewAudio.isDisabled());
        team.submit(audioMergeTask);
        btnSaveAudioFile.setText("Save and Overwrite");

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
    // FOR WAV FILE PATHNAME: Main.getCreationDir() + "/" + _creationNameField.getText() + "/" + _creationNameField.getText() + ".wav"

}
