package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Florian Lutze
 *
 * Starts a new instance of the Launch-Screen
 */
public class Main extends Application {


    /**
     * Starts a new Launch-Screen
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/views/launch.fxml"));
        primaryStage.setTitle("CodesWeeper");
        primaryStage.setScene(new Scene(root, 360, 325));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/img/icon.png"));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    /**
     * Application start point
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
