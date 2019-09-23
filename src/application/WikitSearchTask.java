package application;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WikitSearchTask extends Task<Void> {
    private String _term;
    private String _output;
    private int _exit;
    public WikitSearchTask(String term) {
        _term = term;
    }

    @Override
    protected Void call() throws Exception {
        String cmd = "wikit " + _term;
        ProcessBuilder wikitpb = new ProcessBuilder("bash", "-c", cmd);


        Process wikitp = wikitpb.start();
        _exit = wikitp.waitFor();
        BufferedReader stdout = new BufferedReader(new InputStreamReader(wikitp.getInputStream()));

        //exitStatus = wikitTask.getValue();
        _output = stdout.readLine();

        if (_output.startsWith("  ")) {
            _output = _output.replaceFirst("  ", "");
        }
        _output = _output.replace(". ", ".\n");

        //System.out.println(_output);
        //splitWikitOut = wikitOutput.split("\n");

        stdout.close();
        return null;

    }

    public int getExit() {
        return _exit;
    }
    public String getOutput() {
        return _output;
    }
}
