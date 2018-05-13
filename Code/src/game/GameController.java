package game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Difficulty;

import java.util.Random;


/**
 * @author Florian Lutze
 *
 * GameController - handles all user inputs and game logic
 */
public class GameController {

    // Defines how big a game field is
    private Difficulty difficulty;
    private double x, y;
    private boolean dragging = false, playing, gameover;
    private int totalSeconds;
    private game.Field[][] fields;
    private Timeline timeline;
    private int unhiddenFields = 0;


    @FXML
    GridPane gameField;

    @FXML
    Label lblTime;

    @FXML
    MenuItem ctxClose, ctxNewGame, ctxRestart;


    /**
     * Init method creates the gamefield with the bombs, depending on the difficulty
     * @param difficulty
     */
    public void init(Difficulty difficulty){
        this.difficulty = difficulty;

        playing = false;
        gameover = false;
        dragging = false;
        totalSeconds = 0;
        boolean rowInit = true;

        Random rnd = new Random();

        fields = new game.Field[difficulty.getWidth()][difficulty.getHeight()];

        // Add Bombs at random positions
        for (int i = difficulty.getMines(); i > 0; i--) {
            int x, y;
            do {
                x = rnd.nextInt(difficulty.getWidth());
                y = rnd.nextInt(difficulty.getHeight());
            }while(fields[x][y] != null);
            fields[x][y] = new Field(true, this);
        }

        for (int i = 0; i < difficulty.getWidth(); i++) {
            // Make every column and row the same size
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / difficulty.getWidth());
            gameField.getColumnConstraints().add(colConst);
            for (int j = 0; j < difficulty.getHeight(); j++) {
                if(rowInit){
                    RowConstraints rowConst = new RowConstraints();
                    rowConst.setPercentHeight(100.0 / difficulty.getHeight());
                    gameField.getRowConstraints().add(rowConst);
                }

                // Random value defines if field is a bomb
                if(fields[i][j] == null)
                    fields[i][j] = new Field(false, this);
                gameField.add(fields[i][j], i, j);
                gameField.setHalignment(fields[i][j], HPos.CENTER);
                gameField.setValignment(fields[i][j], VPos.CENTER);
            }
            rowInit = false;
        }
        addNeighbors();

    }

    /**
     * Adds neighbor fields to each field in the gamefield
     */
    private void addNeighbors(){
        // Go through all columns
        for (int i = 0; i < difficulty.getWidth(); i++) {
            // Go through all rows
            for (int j = 0; j < difficulty.getHeight(); j++) {

                // X-Offset for the 8 fields around a field
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    // Y-Offset for the 8 fields around a field
                    for (int offsetY = -1; offsetY <= 1; offsetY++) {
                        // Neighbor index
                        int neighborI = i + offsetX, neighborJ = j + offsetY;
                        // Skip if it is the same field
                        if(neighborI == i && neighborJ == j)
                            continue;

                        if(neighborI >= 0 && neighborI < difficulty.getWidth() &&
                                neighborJ >= 0 && neighborJ < difficulty.getHeight() &&
                                fields[neighborI][neighborJ] != null){
                            fields[i][j].addNeighbor(fields[neighborI][neighborJ]);
                        }
                    }
                }
            }
        }
    }

    public void fieldUnhidden(){
        if(!gameover) {
            unhiddenFields++;
            if (unhiddenFields == (difficulty.getWidth() * difficulty.getHeight()) - difficulty.getMines()) {
                gameOver();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Gewonnen!");
                alert.setHeaderText("Glückwunsch du hast gewonnen!");
                alert.setOnCloseRequest(e -> {
                    alert.close();
                });
                alert.showAndWait();
            }
        }
    }

    /**
     * Starts the timer, as soon as a field has been clicked
     */
    public void startTimer(){
        if(!playing) {
            playing = true;
            timeline = new Timeline(new KeyFrame(
                    Duration.millis(1000),
                    ae -> {
                        totalSeconds++;
                        int mins = totalSeconds / 60;
                        int secs = totalSeconds % 60;
                        lblTime.setText(String.format("%02d:%02d", mins, secs));
                    }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
    }


    /**
     * Closes the game
     * @param event
     */
    @FXML
    public void btnCloseClicked(ActionEvent event){
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    /**
     * Handles the drag event of the window
     * @param event
     */
    @FXML
    private void dragging(MouseEvent event){
        dragging = true;
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    /**
     * Handles the drag event of the window
     * @param event
     */
    @FXML
    private void dragEnter(MouseEvent event){
        x = event.getSceneX();
        y = event.getSceneY();
    }

    /**
     * Handles the drag event of the window
     * @param event
     */
    @FXML
    private void dragStop(MouseEvent event){
        dragging = false;
    }

    /**
     * Returns if Game Over state
     * @return
     */
    public boolean isGameOver() {
        return gameover;
    }

    /**
     * Sets Game Over to true
     */
    public void gameOver(){
        timeline.stop();
        gameover = true;

        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields[i].length; j++) {
                if(fields[i][j].isBomb())
                    fields[i][j].unhideField();
            }
        }
    }

    /**
     * Handles the menu-bar
     * @param event
     */
    @FXML
    public void menuItemClicked(ActionEvent event){
        MenuItem item = (MenuItem)event.getSource();
        if(item == ctxClose){   // Close game
            Stage stage = (Stage)lblTime.getScene().getWindow();
            stage.close();
            return;
        }
        if(item == ctxNewGame){ // Start new game
            try {
                new Main().start(new Stage());
            }catch (Exception e){}
        }
        if(item == ctxRestart){ // Restart game
            try {
                Stage stage = (Stage) lblTime.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/game.fxml"));
                Parent root = loader.load();
                GameController controller = loader.<GameController>getController();
                controller.init(difficulty);
                Scene scene = new Scene(root, difficulty.getWidth() * 35, difficulty.getHeight() * 35);
                stage.setScene(scene);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public boolean isDragging() {
        return dragging;
    }
}
