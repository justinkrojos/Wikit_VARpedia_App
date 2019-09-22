package application;

import javafx.concurrent.Task;

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
        getHTML(_term);


        return null;
    }

    private List<String> getHTML(String term) {
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

        for (String word: html.split(" ")) {

        }


        //System.out.println(html.split(" "));
/*        for (String word : html.split(" ")) {

            if (word.matches("url(//*")) {

                output.add(word);
            }
            System.out.println(word);
        }

        for (String i : output) {
            System.out.println(i);
        }*/
        return output;

    }
}
