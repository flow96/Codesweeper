package game;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;


/**
 * @author Florian Lutze
 *
 * Field - Stores neighbor-fields and represents either an empty or a bomb-field
 */
public class Field extends GridPane {

    private ImageView imgView;
    private Label lblBombs;
    private ArrayList<Field> neighbors;
    private boolean isFlagged;
    private boolean hidden;
    private boolean isBomb;
    private int bombsNearby = 0;
    private GameController controller;

    /**
     * Initializes the field
     * @param isBomb
     * @param controller
     */
    public Field(boolean isBomb, GameController controller){
        this.controller = controller;
        this.isBomb = isBomb;
        neighbors = new ArrayList<>();
        hidden = true;
        isFlagged = false;
        // Create Label and Imageview for label or image representation
        lblBombs = new Label();
        imgView = new ImageView();

        imgView.setFitWidth(22);
        imgView.setFitHeight(22);
        setGridLinesVisible(false);

        // Center everything
        setAlignment(Pos.CENTER);

        lblBombs.setTextFill(Color.WHITE);
        this.setAlignment(Pos.CENTER);

        // If field was clicked
        setOnMouseReleased(event -> {
            // If not gameover and user is not dragging the window
            if(!controller.isDragging() && !controller.isGameOver()) {
                // Left mouse button was clicked
                if (event.getButton().equals(MouseButton.PRIMARY) && hidden) {
                    // If game was not started, it will be started now
                    controller.startTimer();
                    // If field is flagged it will be unflagged now
                    if (isFlagged) {
                        isFlagged = false;
                        getChildren().remove(0);
                        return;
                    }
                    // If field is not a bomb, show label and nearby fields if there are 0 bombs nearby
                    if (!isBomb) {
                        if (bombsNearby == 0) {  // Open empty spots
                            showEmptyNearbies();
                        } else   // Show bombs nearby
                            unhideField();
                    } else {
                        // If field is a bomb
                        hidden = false;
                        imgView.setImage(new Image(Field.class.getResourceAsStream("/img/explosion.png")));
                        imgView.setVisible(true);
                        this.add(imgView, 0, 0);
                        getStyleClass().remove("hidden-field");
                        getStyleClass().add("unhidden-field");
                        controller.gameOver();
                    }
                } else if (event.getButton().equals(MouseButton.SECONDARY) && hidden) { // Right mouse button was clicked
                    isFlagged = !isFlagged;
                    if (isFlagged) {
                        getStyleClass().add("field-flagged");
                        imgView.setImage(new Image(Field.class.getResourceAsStream("/img/flag.png")));
                        this.add(imgView, 0, 0);
                    } else {
                        getStyleClass().remove("field-flagged");
                        getChildren().remove(0);
                    }
                }
            }
        });
    }

    /**
     * Adds a neighbor to list of nearby fields, and checks if neighbor is a bomb-field
     * @param neighbor
     */
    public void addNeighbor(Field neighbor){
        neighbors.add(neighbor);
        if(neighbor.isBomb){
            bombsNearby++;
        }
        if(bombsNearby >= 3)
            lblBombs.setTextFill(Color.YELLOW);
        if(bombsNearby >= 4)
            lblBombs.setTextFill(Color.RED);
    }


    /**
     * Unhides this field and shows empty nearby fields if there are any
     */
    public void showEmptyNearbies(){
        if(!isBomb && hidden){
            unhideField();
            if(bombsNearby == 0){
                for (Field neighbor : neighbors){
                    neighbor.showEmptyNearbies();
                }
            }
        }
    }

    /**
     * Unhides this field
     */
    public void unhideField(){
        if(hidden) {
            hidden = false;
            // Remove labels and flags
            for (int i = getChildren().size() - 1; i >= 0; i--) {
                getChildren().remove(i);
            }
            getStyleClass().remove("hidden-field");
            getStyleClass().add("unhidden-field");
            if (bombsNearby != 0 && !isBomb) {
                lblBombs.setText("" + bombsNearby);
                this.add(lblBombs, 0, 0);
                setHalignment(lblBombs, HPos.CENTER);
            } else if (isBomb) {
                imgView.setImage(new Image(Field.class.getResourceAsStream("/img/bomb.png")));
                imgView.setVisible(true);
                this.add(imgView, 0, 0);
            }
        }
    }

    /**
     * Returns if this field is a bomb
     * @return
     */
    public boolean isBomb() {
        return isBomb;
    }

}
