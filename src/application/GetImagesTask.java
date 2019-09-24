package application;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GetImagesTask extends Task<Void> {

    private String _term;
    private int _numImages;
    private String _creationName;

    private List<String> _imageList;

    private int _exit;

    public GetImagesTask(String term, String creationName, int numImages) {
        _term = term;
        _numImages = numImages;
        _creationName = creationName;
    }

    @Override
    protected Void call() throws Exception {
        List<String> imageList = getImages(_term,_numImages);
        _imageList = imageList;
        downloadImages(_imageList);
        return null;
    }

    public List<String> getImagesURL() {
        return _imageList;
    }

    private void downloadImages(List<String> urls) {
        int counter = 0;
        for (String s: urls) {
            try(InputStream in = new URL(s).openStream()){
                Files.copy(in, Paths.get(Main.getCreationDir() + "/"+_creationName+"/image"+counter+".jpg"));
                counter++;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private List<String> getImages(String term, int numImages) {
        String urlString = "https://www.flickr.com/search/?text=" + _term;
        String html = "";
        try {
            URL url = new URL(urlString);
            BufferedReader br = new BufferedReader((new InputStreamReader(url.openStream())));

            String line;
            while ((line = br.readLine()) != null) {
                html += line;
            }
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(html);
        List<String> output = new ArrayList<String>();

        //System.out.println(html.trim());
        for (String word: html.split(" ")) {
            //System.out.println(word);
            //if (word.matches("(?i)url.*")) {
            if (word.matches(".*live.staticflickr.com.*") && word.matches("(?i)url.*")) {
                //System.out.println(word);
                word = word.replace("url(//","http://");
                word = word.replace(")\"","");
                //System.out.println(word);
                word = finalURL(word);
                output.add(word);
            }
        }
        //System.out.println("?????");
        List<String> imageList = new ArrayList<String>();
        for (int i = 0; i < numImages; i++) {
            //System.out.println(output.get(i));
            imageList.add(output.get(i));
            //imageList.add(new Image(output.get(i),800,600,false,false));
        }

        return imageList;

    }

    private String finalURL(String url) {
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setInstanceFollowRedirects(false);
            con.connect();
            return con.getHeaderField("Location").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    //ffmpeg -r 1/5 -f image2 -s 800x600 -i /media/sf_VBoxSharedFolder/Ass3/IdeaProjects/206Assignment3/out/production/creations/apple3/image%01d.jpg -vcodec libx264 -crf 25 -pix_fmt yuv420p -vf "drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='apple'" out.mp4

}
