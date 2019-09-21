package application;

import javafx.concurrent.Task;

public class CreateVideoTask extends Task<Void> {

    private String _term;
    private int _length;
    private int _numImages;

    public CreateVideoTask(String term, int length, int numImages) {
        _term = term;
        _length = length;
        _numImages = numImages;
    }

    @Override
    protected Void call() throws Exception {
        
        return null;
    }
}
