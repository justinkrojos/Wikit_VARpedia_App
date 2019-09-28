package application;

import javafx.concurrent.Task;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class PreviewAudioTask extends Task<Void> {


    private String textArea;
    private String voice;
    private Process processAudioPreview;


    public PreviewAudioTask(String textArea, String voice) {
        this.textArea = textArea;
        this.voice = voice;
    }

    @Override
    protected Void call() throws Exception {

        String cmd = "echo {\"(" + voice + ")\".'(SayText \"" + textArea + "\")'} | bash -c festival";
        ProcessBuilder previewAudiopb1 = new ProcessBuilder("bash", "-c", cmd);
        processAudioPreview = previewAudiopb1.start();
        processAudioPreview.waitFor();

        return null;

    }

    public void stopProcess() {
        processAudioPreview.destroyForcibly();

    }

}
