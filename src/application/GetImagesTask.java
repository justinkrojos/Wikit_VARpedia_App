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

    //private List<String> _imageList;

  //  private int _exit;

    public GetImagesTask(String term, String creationName, int numImages) {
        _term = term;
        _numImages = numImages;
        _creationName = creationName;
    }

    @Override
    protected Void call() throws Exception {
        flickr();
        makeVideo();
        return null;
    }

    /**
     * This method create the slideshow and adds sub titles.
     */
    private void makeVideo() {
        double length = getAudioLength();

        String path = Main.getCreationDir()+"/"+_creationName+"/";
        length = length/_numImages;
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path+"cmd.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < _numImages; i++) {
            if (i==_numImages-1) {
                writer.println("file "+path+"image"+i+".jpg");
                writer.println("duration "+length);
                writer.println("file "+path+"image"+i+".jpg");
                break;
            }
            writer.println("file "+path+"image"+i+".jpg");
            writer.println("duration "+length);
        }
        writer.close();



        //String command = command1+";"+command2;

        String command1 = "ffmpeg -y -f concat -safe 0 -i "+path+"cmd.txt"+ " -pix_fmt yuv420p -r 25 -vf 'scale=trunc(iw/2)*2:trunc(ih/2)*2' " +path+"video.mp4";
        String command2 = "ffmpeg -y -i "+Main.getCreationDir()+"/"+_creationName+"/"+"video.mp4 "+ "-vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+_term+"'\" "+"-r 25 "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".mp4";
        String command = command1+";"+command2;

        //System.out.println(command);
        ProcessBuilder pbb = new ProcessBuilder("/bin/bash","-c",command);
        try {
            Process p = pbb.start();
            p.waitFor();

            //System.out.println("Done");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // System.out.println(command);

    }

    /**
     * Gets the integer value length of the audio file, rounded up.
     * @return
     */
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

    /**
     * Download pictures from flikr.
     */
    private void flickr() {

        String apiKey = "e37d6b63e1b4bceb47a42a3a37f316e3";
        String sharedSecret = "42ccf0520e0515f1";
        try{

            try {
                apiKey = getAPIKey("apiKey");
                sharedSecret = getAPIKey("sharedSecret");
            } catch (Exception e) {
                apiKey = "e37d6b63e1b4bceb47a42a3a37f316e3";
                sharedSecret = "42ccf0520e0515f1";
            }



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

    /**
     * Get the api keys from a txt file.
     * @param key
     * @return
     * @throws Exception
     */
    public static String getAPIKey(String key) throws Exception {
        // TODO fix the following based on where you will have your config file stored

/*        String config = System.getProperty("user.dir")
                + System.getProperty("file.separator")+ "flickr-api-keys.txt";*/
        String creationsDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        creationsDir = creationsDir.substring(0,creationsDir.lastIndexOf("/"));
        String config = creationsDir+ "/flickr-api-keys.txt";

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
