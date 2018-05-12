package game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.Difficulty;

/**
 * @author Florian Lutze
 *
 * Handles all inputs from the launch screen
 */
public class LaunchController {

    private double x, y;
    private boolean dragging = false;
    private Difficulty difficulty = Difficulty.ADVANCED;
    private ToggleGroup toggleGroup;
    private boolean initDone = false;

    @FXML
    RadioButton beginner, advanced, professional, custom;

    @FXML
    HBox customBox, customBox2;

    @FXML
    TextField tbxCustomHeight, tbxCustomWidth, tbxCustomMines;


    /**
     * Initializes the Launch-Screen
     */
    @FXML
    public void initialize(){
        // Initialize Launchscreen
        if(!initDone) {
            toggleGroup = new ToggleGroup();

            beginner.setToggleGroup(toggleGroup);
            advanced.setToggleGroup(toggleGroup);
            professional.setToggleGroup(toggleGroup);
            custom.setToggleGroup(toggleGroup);

            toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(beginner)) {
                    difficulty = Difficulty.BEGINNER;
                } else if (newValue.equals(advanced)) {
                    difficulty = Difficulty.ADVANCED;
                } else if (newValue.equals(professional)) {
                    difficulty = Difficulty.PROFESSIONAL;
                } else if (newValue.equals(custom)) {
                    difficulty = Difficulty.CUSTOM;
                    customBox.setDisable(false);
                    customBox2.setDisable(false);
                }
                if (oldValue.equals(custom)) {
                    customBox.setDisable(true);
                    customBox2.setDisable(true);
                }
            });

            // Allow number input only
            tbxCustomHeight.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    tbxCustomHeight.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            // Allow number input only
            tbxCustomWidth.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    tbxCustomWidth.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            // Allow number input only
            tbxCustomMines.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    tbxCustomMines.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            initDone = true;
        }
    }


    /**
     * Starts a new Game Scene in this Stage
     * @param stage
     * @throws Exception
     */
    private void startGame(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/game.fxml"));
        Parent root = loader.load();
        GameController controller = loader.<GameController>getController();
        controller.init(difficulty);
        Scene scene = new Scene(root, difficulty.getWidth() * 35, difficulty.getHeight() * 35);
        stage.setScene(scene);
    }


    /**
     * Handles the start button click event
     * @param event
     */
    @FXML
    public void btnStartClicked(ActionEvent event){
        try {
            int cWidth = Integer.parseInt(tbxCustomWidth.getText()),
                    cHeight = Integer.parseInt(tbxCustomHeight.getText()),
                    cMines = Integer.parseInt(tbxCustomMines.getText());

            // Min. 9x9
            if(difficulty == Difficulty.CUSTOM) {
                if(cWidth <= 8 || cHeight <= 8 || cWidth >= 150 || cHeight >= 150 || cMines <= 5)
                    return;
                if(cMines > (cHeight * cWidth) - 20)
                    cMines = (cHeight * cWidth) - 20;
                difficulty.setHeight(cHeight);
                difficulty.setWidth(cWidth);
                difficulty.setMines(cMines);
            }
            startGame(((Stage) ((Node) event.getSource()).getScene().getWindow()));
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Handles the x - close button event
     * @param event
     */
    @FXML
    public void btnCloseClicked(ActionEvent event){
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    /**
     * Handles the window dragging
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
     * Handles the window dragging
     * @param event
     */
    @FXML
    private void dragEnter(MouseEvent event){
        x = event.getSceneX();
        y = event.getSceneY();
    }

}
