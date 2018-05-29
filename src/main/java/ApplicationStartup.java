import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * @author Dylan Wei
 * @date 2018-05-24 14:27
 */
public class ApplicationStartup extends Application {
    public static void main(String[] args){
        launch(args);
    }

//    https://download.jetbrains.8686c.com/idea/ideaIU-2018.1.4.exe
    public void start(Stage primaryStage) throws Exception {
         URL url = new ClassPathResource("fxml/MainPane.fxml").getURL();
         FXMLLoader fxmlLoader = new FXMLLoader(url);
         primaryStage.setScene(new Scene((Parent) fxmlLoader.load()));
         primaryStage.setOnShowing(new AppInitializer());
         primaryStage.setOnCloseRequest(new PreClosedHandler());

         primaryStage.show();

    }

    private static class AppInitializer implements EventHandler<WindowEvent> {
        private static String workPath = System.getProperty("user.home") + "/aria2Downloader";
        private static File aria2c = null;
        private static File config = null;

        @Override
        public void handle(WindowEvent event) {
            startAria2();
        }

        private void startAria2(){
            try {
                copyAria2ToUserFolder();
                String aria2Path = aria2c.getCanonicalPath();
                String configPath = config.getCanonicalPath();
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", aria2Path, "--conf=" + configPath);
                Process aria2 = processBuilder.start();
                aria2Process = aria2;
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        private void copyAria2ToUserFolder() throws IOException {
            File directory = new File(workPath);
//            directory.mkdir();
            FileOutputStream aria2cOut = null;
            FileOutputStream configOut = null;
            try {
                // 复制文件到工作目录
                BufferedInputStream aria2cIn = new BufferedInputStream(
                        getClass().getResourceAsStream("/aria2/aria2c.exe"));
                File aria2cFile = new File(directory + "/aria2c.exe");
                aria2c = aria2cFile;
                aria2cOut= new FileOutputStream(aria2cFile);
                byte[] data = new byte[1024 * 4];
                int length = 0;
                while(((length = aria2cIn.read(data)) != -1)){
                    aria2cOut.write(data, 0, length);
                }
                BufferedInputStream configIn = new BufferedInputStream(
                        getClass().getResourceAsStream("/aria2/aria2.conf"));
                File configFile = new File(directory + "/aria2.conf");
                config = configFile;
                configOut = new FileOutputStream(configFile);
                while((length = configIn.read(data)) != -1){
                    configOut.write(data, 0, length);
                }
            }finally {
                aria2cOut.close();
                configOut.close();
            }

        }
    }

    private static Process aria2Process;

    private static class PreClosedHandler implements EventHandler<WindowEvent>{
        @Override
        public void handle(WindowEvent event) {
        }
    }

}
