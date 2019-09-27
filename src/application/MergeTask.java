package application;

import javafx.concurrent.Task;

public class MergeTask extends Task<Void> {

    String _creationName;
    public MergeTask(String creationName) {
        _creationName = creationName;
    }

    /**
     * Make the merged creation in creation folder, not in any of the sub folders under creation.
     * @return
     * @throws Exception
     */
    @Override
    protected Void call() throws Exception {
        String command = "ffmpeg -y -i "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".mp4 -i "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".wav " + Main.getCreationDir()+"/"+_creationName+".mp4";
        ProcessBuilder pb = new ProcessBuilder("bash","-c",command);
        Process p = pb.start();
        p.waitFor();
        return null;

    }
}
