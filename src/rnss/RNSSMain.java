package rnss;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * RNSSMain is main method() class for Residence Need Solution System (RNSS)
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public class RNSSMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // loading FXMLDocument for GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RNSS.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        // loading the FXMLDocument controller
        RNSSController controller = loader.getController();

        // call initializeStage() method to pass stage
        controller.initializeStage(primaryStage);

        // Add the favicon
        primaryStage.getIcons().add(new Image("file:favicon.png"));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
