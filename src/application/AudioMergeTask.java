package application;

import javafx.concurrent.Task;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AudioMergeTask extends Task<Void> {

    private String term;
    private ListView _audioList;
    private Process playAudioProcess;
    private String cmd;
    private Boolean preview;

    public AudioMergeTask(String term, ListView _audioList, Boolean preview) {
        this.term = term;
        this._audioList = _audioList;
        this.preview = preview;
    }

    @Override
    protected Void call() throws Exception {
        cmd = "sox";

        for (int i = 0; i < _audioList.getItems().size(); i++) {
            HBox audioListhb = (HBox)_audioList.getItems().get(i);
            Text audioListLabel = (Text)audioListhb.getChildren().get(0);

            cmd = cmd + " '" + Main.getCreationDir() + "/" + term + "/audio/" + audioListLabel.getText() + ".wav'";
            // System.out.println(cmd);
        }

        if (preview) {
            cmd = cmd + " '" + Main.getCreationDir() + "/" + term + "/" + term + "preview.wav' && ffplay -autoexit -nodisp '" + Main.getCreationDir() + "/" + term + "/" + term + "preview.wav' && rm '" + Main.getCreationDir() + "/" + term + "/" + term + "preview.wav' && ffplay -autoexit -nodisp '" + Main.getCreationDir() + "/" + term + "/" + term + "preview.wav'";
        }
        else {
            cmd = cmd + " '" + Main.getCreationDir() + "/" + term + "/" + term + ".wav'";
        }


        ProcessBuilder playFullAudiopb = new ProcessBuilder("bash", "-c", cmd);
        playAudioProcess = playFullAudiopb.start();
        playAudioProcess.waitFor();

       // System.out.println(cmd);

        // System.out.println(cmd);



        return null;

    }

    public Process getProcess() {
        return playAudioProcess;
    }


}
