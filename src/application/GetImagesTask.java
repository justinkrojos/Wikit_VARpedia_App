package application;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetImagesTask extends Task<Void> {

    private String _term;
    private int _numImages;

    private int _exit;

    public GetImagesTask(String term, int numImages) {
        _term = term;
        _numImages = numImages;
    }

    @Override
    protected Void call() throws Exception {
/*        String url = "wget https://www.flickr.com/search/?text=" + _term;
        ProcessBuilder getHTML = new ProcessBuilder("bash", "-c", "pwd");
        Process getHTMLP = getHTML.start();
        _exit = getHTMLP.waitFor();
        BufferedReader stdout = new BufferedReader(new InputStreamReader(getHTMLP.getInputStream()));
        System.out.println(stdout.readLine());*/
        getImages(_term,_numImages);

        System.out.println("PRay");

        return null;
    }

    private List<Image> getImages(String term, int numImages) {
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
            if (word.matches("(?i)url.*")) {
                //System.out.println(word);
                word = word.replace("url(//","http://");
                word = word.replace(")\"","");
                //System.out.println(word);
                output.add(word);
            }
        }

        List<Image> imageList = new ArrayList<Image>();
        for (int i = 0; i < numImages; i++) {
            System.out.println(output.get(i));
            imageList.add(new Image(output.get(i),800,600,false,false));
        }

        return imageList;

    }
}
