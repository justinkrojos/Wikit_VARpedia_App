package application;

import javafx.concurrent.Task;

public class MakeSlideShow extends Task<Void> {

    public MakeSlideShow() {

    }
    @Override
    protected Void call() throws Exception {
        String command = "ffmpeg -framerate 1/2 -i image%01d.jpg -r 25 -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" /media/sf_VBoxSharedFolder/Ass3/IdeaProjects/206Assignment3/out/production/creations/213/video.mp4;ffmpeg -i /media/sf_VBoxSharedFolder/Ass3/IdeaProjects/206Assignment3/out/production/creations/213/video.mp4 -vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='apple'\" /media/sf_VBoxSharedFolder/Ass3/IdeaProjects/206Assignment3/out/production/creations/213/213.mp4";
        ProcessBuilder pb = new ProcessBuilder("bash","-c",command);
        Process p = pb.start();
        p.waitFor();
        return null;
    }
}
