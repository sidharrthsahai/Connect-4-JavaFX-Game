package sid;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * @author Sidharrth Sahai
 */

public class Main extends Application {
    private Controller controller;

    /**
     * The start function connects the Main.java with the Game_Layout.fxml
     * It calls methods in the controller
     * Sets up the Stage and creates a GridPane Object which is set in the Scene
     * The menubar is attached to the pane
     * The Scene is finally set on the Primary Stage
     * @param primaryStage is basically the Stage object
     * @throws Exception used to throw the appropriate exception
     */


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Game_Layout.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();//here is the menubar
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());//Binding to entire width

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);//get the 0th index which is a pane for menu bar
        menuPane.getChildren().add(menuBar);//Inserting menu bar in menuPane

        Scene scene = new Scene(rootGridPane);
        Image image=new Image(getClass().getResourceAsStream("Icon/logo.png"));
        primaryStage.getIcons().add(image);//Icon

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Two Dropdown - File and Help are created with their sub parts
     * Their functionality is set using the setOnAction event handler
     * @return
     */

    private MenuBar createMenu() {
        //File menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New game");

        newGame.setOnAction(event -> controller.resetGame());  //Action on new Game
        MenuItem resetGame = new MenuItem("Reset game");

        resetGame.setOnAction(event -> controller.resetGame());//Action on reset button
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit game");
        exitGame.setOnAction(event -> exitGame());
        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Connect 4");
        aboutGame.setOnAction(event -> aboutConnect4());
        SeparatorMenuItem separatorHelpItem = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());
        helpMenu.getItems().addAll(aboutGame, separatorHelpItem, aboutMe);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;

    }

    /**
     * This function pops up an alert window showing details about me
     */

    private void aboutMe() {


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Sidharrth Sahai");
        alert.setContentText("I love to play around with code and create games. " +
                "Connect 4 is one of them" +
                " Hope You Like this game. " +
                "\nThank You!!!");
        alert.show();
    }

    /**
     * This function pops up an alert window showing details about the game and how is it played
     */

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the players " +
                "first choose a color and then take turns dropping colored discs from the top into " +
                "a seven-column, six-row vertically suspended grid. The pieces fall straight down, " +
                "occupying the next available space within the column. The objective of the game is " +
                "to be the first to form a horizontal, vertical, or diagonal line of four of one's " +
                "own discs. Connect Four is a solved game. The first player can always win by playing " +
                "the right moves.");
        alert.show();
    }

    /**
     * This function basically shuts down the game and all the threads in the background
     */
    private void exitGame() {
        Platform.exit();//shutdown the application
        System.exit(0); //close all the resources such as thread of this application
    }

    /**
     * Having a main() method here is also very important.
     * This method is triggered first and the launch(args) statement is executed to point towards the start method
     * @param args
     */

    public static void main(String[] args) {
        launch(args);
    }
}

