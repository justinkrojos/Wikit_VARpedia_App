package application;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
        //List<String> imageList = getImages(_term,_numImages);
        //_imageList = imageList;
        //downloadImages(_imageList);
       // makeVideo();
        flickr();
        makeVideo();
/*        Thread thread = new Thread(new MakeSlideShow());
        thread.start();*/
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
   //ffmpeg -framerate 0.3 -i apple%02d.jpg -r 25  out.mp4
    //ffmpeg -framerate 0.3 -i apple%02d.jpg -vf "drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='apple'" out.mp4
    private void makeVideo() {
        double length = getAudioLength();
        length = _numImages/length;

        //System.out.println(length);

       //String command = "ffmpeg -r 1/"+length+" -f image2 -s 800x600 -i "+Main.getCreationDir()+"/"+_creationName+"/image%01d.jpg -vcodec libx264 -crf 25 -pix_fmt yuv420p -vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+_term+"'\" "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".mp4";
        //String command = "ffmpeg -framerate "+length+" -i image%01d.jpg -r 25 -vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+_term+"'\" "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".mp4";
        String command1 = "ffmpeg -y -framerate "+length+" -i "+Main.getCreationDir()+"/"+_creationName+"/"+"image%01d.jpg -r 25 -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" "+Main.getCreationDir()+"/"+_creationName+"/"+"video.mp4";
        String command2 = "ffmpeg -y -i "+Main.getCreationDir()+"/"+_creationName+"/"+"video.mp4 "+ "-vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+_term+"'\" "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".mp4";
        String command = command1+";"+command2;

        //System.out.println(command);
        ProcessBuilder pbb = new ProcessBuilder("/bin/bash","-c",command);
        try {
            Process p = pbb.start();
            p.waitFor();

          //  pb.redirectOutput(Paths.get(Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".mp4"));
            System.out.println("Done");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // System.out.println(command);

    }

    private int getAudioLength() {
        String command = "soxi -D "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".wav";
        ProcessBuilder audioLenBuilder = new ProcessBuilder("bash","-c",command);
        try {
            Process audioLenProcess = audioLenBuilder.start();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(audioLenProcess.getInputStream()));
            audioLenProcess.waitFor();
            String audioString = stdout.readLine();
            stdout.close();
            return (int)Double.parseDouble(audioString) + 1;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void flickr() {
        try{
            String apiKey = "e37d6b63e1b4bceb47a42a3a37f316e3";
            String sharedSecret = "42ccf0520e0515f1";
            /*String apiKey = getAPIKey("apiKey");
            String sharedSecret = getAPIKey("sharedSecret");*/
            Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
            String query = _term;
            int resultsPerPage = _numImages;
            int page = 0;

            PhotosInterface photos = flickr.getPhotosInterface();
            SearchParameters params = new SearchParameters();
            params.setSort(SearchParameters.RELEVANCE);
            params.setMedia("photos");
            params.setText(query);

            PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
           // System.out.println("Retrieving " + results.size()+ " results");
            int count = 0;
            for (Photo photo: results) {
                try {
                    BufferedImage image = photos.getImage(photo, Size.LARGE);
                    //String filename = query.trim().replace(' ', '-')+"-"+System.currentTimeMillis()+"-"+photo.getId()+".jpg";
                    String filename = "image"+count+".jpg";
                    File outputfile = new File(Main.getCreationDir()+"/"+_creationName,filename);
                    ImageIO.write(image, "jpg", outputfile);
                    //System.out.println("Downloaded "+filename);
                    count++;
                } catch (FlickrException | IOException fe) {
                   // System.err.println("Ignoring image " +photo.getId() +": "+ fe.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

       // System.out.println("\nDone");
    }
    public static String getAPIKey(String key) throws Exception {
        // TODO fix the following based on where you will have your config file stored

        String config = System.getProperty("user.dir")
                + System.getProperty("file.separator")+ "flickr-api-keys.txt";

//		String config = System.getProperty("user.home")
//				+ System.getProperty("file.separator")+ "bin"
//				+ System.getProperty("file.separator")+ "flickr-api-keys.txt";
        File file = new File(config);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ( (line = br.readLine()) != null ) {
            if (line.trim().startsWith(key)) {
                br.close();
                return line.substring(line.indexOf("=")+1).trim();
            }
        }
        br.close();
        throw new RuntimeException("Couldn't find " + key +" in config file "+file.getName());
    }
}
