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

    public AudioMergeTask(String term, ListView _audioList) {
        this.term = term;
        this._audioList = _audioList;
    }

    @Override
    protected Void call() throws Exception {
        mergeAudio();

        cmd = cmd + " '" + Main.getCreationDir() + "/" + term + "/" + term+ ".wav'" +
                " && ffplay -autoexit -nodisp '" + Main.getCreationDir() + "/" + term + "/" + term + ".wav'";

        doProcess(cmd);
       // System.out.println(cmd);

        // System.out.println(cmd);



        return null;

    }

    public Process getProcess() {
        return playAudioProcess;
    }

    public void mergeAudio() {
        cmd = "sox";

        for (int i = 0; i < _audioList.getItems().size(); i++) {
            HBox audioListhb = (HBox)_audioList.getItems().get(i);
            Text audioListLabel = (Text)audioListhb.getChildren().get(0);

            cmd = cmd + " '" + Main.getCreationDir() + "/" + term + "/audio/" + audioListLabel.getText() + ".wav'";
            // System.out.println(cmd);
        }
    }

    public void doProcess(String cmd) throws Exception {
        ProcessBuilder playFullAudiopb = new ProcessBuilder("bash", "-c", cmd);
        playAudioProcess = playFullAudiopb.start();
        playAudioProcess.waitFor();

    }

}
