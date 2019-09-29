package application;

import javafx.concurrent.Task;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CreateAudioTask extends Task<Void> {

   private String _creationNameField;
   private String _audioName;
   private String _textArea;
   private Voices voicePackage;


    public CreateAudioTask(String _creationNameField, String _textArea, String _audioName, Voices voicePackage) {
        this._creationNameField = _creationNameField;
        this._textArea = _textArea;
        this._audioName = _audioName;
        this.voicePackage = voicePackage;
    }

    @Override
    protected Void call() throws Exception {

        String cmd = "mkdir -p '" + Main.getCreationDir() + "/" + _creationNameField + "/audio' && " +
                "echo \"" + _textArea + "\" | text2wave -o '" + Main.getCreationDir() + "/" + _creationNameField + "/audio/" + _audioName + ".wav' -eval \"" +
                voicePackage.getVoicePackage() + "\" 2> '" + Main.getCreationDir() + "/" + _creationNameField + "/audio/error.txt'";

        ProcessBuilder saveAudiopb = new ProcessBuilder("bash", "-c", cmd);
        Process process1 = saveAudiopb.start();
        process1.waitFor();

        return null;

    }


    public boolean getError() throws FileNotFoundException {

        boolean error;

        File errorFile = new File(  Main.getCreationDir() + "/" + _creationNameField + "/audio/error.txt");
        Scanner sc = new Scanner(errorFile);

        if (sc.hasNextLine()) {
            error = true;
        }
        else {
            error = false;
        }

        sc.close();
        return error;
    }



}
