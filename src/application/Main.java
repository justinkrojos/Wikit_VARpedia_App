package application;

import application.controllers.CreateController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    //private Stage _primaryStage;

    //private String _creationDir;

    @Override
    public void start(Stage primaryStage) throws IOException {
        //System.out.println(this.getClass().getResource("resources/Home.fxml"));

        //_primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("resources/Home.fxml"));
        Parent layout = loader.load();
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

/*    public Stage getPrimaryStage() {
        return _primaryStage;
    }*/

    /**
     * Gets the creation directory.
     * @return
     */
    public static String getCreationDir() {
        String creationsDir = null;
        try {
            creationsDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            creationsDir = creationsDir.substring(0,creationsDir.lastIndexOf("/"));
/*            System.out.println(creationsDir);
            String systemDir = System.getProperty("user.dir");
            System.out.println(systemDir);*/
            creationsDir = creationsDir + "/creations";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return creationsDir;
    }

    public static void main(String[] args) {
        try {
/*            String systemDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            systemDir = systemDir.substring(0,systemDir.lastIndexOf("/"));
            //System.out.println(systemDir);

            //String systemDir = System.getProperty("user.dir");
            String creationsDir = systemDir + "/creations";*/
            //System.out.println(System.getProperty("user.dir"));

            String creationsDir = getCreationDir();
            String cmd = "[ ! -d " + creationsDir + " ]";
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);

            Process process = pb.start();
            //System.out.println(process.waitFor());
            if (process.waitFor() == 0) {
                cmd = "mkdir -p " + creationsDir;
                ProcessBuilder mkdirpb = new ProcessBuilder("bash", "-c", cmd);
                Process mkdirP = mkdirpb.start();
            }
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
