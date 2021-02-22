package sid;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * This is the Controller class having all the variables initially defined
 * It is basically the brains of the program where the actual logic of the game is implemented
 */

public class Controller {

    private static final int COLUMNS=7;
    private static final int ROWS=6;
    private static final int CIRCLE_DIAMETER=80;
    private static final String discColor1="#24303E";
    private static final String discColor2="#4CAA88";

    private static String PLAYER_ONE="Player One";
    private static String PLAYER_TWO="Player Two";

    private boolean isPlayerOneTurn=true;


    private Disc[][] insertedDiscsArray=new Disc[ROWS][COLUMNS];//For structural changes

    private boolean isAllowedToInsert=true;
    private int counter=0;

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDiscPane;

    @FXML
    public Label playerNameLabel;

    @FXML
    public TextField playerOneTextField,playerTwoTextField;

    @FXML
    public Button setNamesButton;

    /**
     * It runs the specified Runnable thread at some point of time during the program execution when triggered by clicking on setNames Button
     * Calls method for creating the Game board and the clickable columns to add Discs
     * Creates and sets name of the two players playing the game
     */

    public void createPlayground(){


        Platform.runLater(() -> setNamesButton.requestFocus());
        Shape rectangleWithHoles=createGameStructureGrid();

        //now place this rectangle on pane which is rootgridpane
        rootGridPane.add(rectangleWithHoles,0,1);
        List<Rectangle> rectangleList=createClickableColumns();

        for (Rectangle rectangle:rectangleList){
            rootGridPane.add(rectangle,0,1);
        }


        setNamesButton.setOnAction(event -> {
            PLAYER_ONE=playerOneTextField.getText();
            PLAYER_TWO=playerTwoTextField.getText();
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });

    }

    /**
     * It creates a White rectangular canvas
     * A for loop traverses the whole ROWS * COLUMNS and creates identical circles on each grid
     * To obtain the final grid with holes, the circles are subtracted from the White rectangular canvas
     * @return rectangleWithHoles, which is basically the game board
     */

    public Shape createGameStructureGrid(){

        //Adding +1 to column and rows to increase display area
        Shape rectangleWithHoles=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

        //Subtracting circlular shapes to create a grid on the scene
        for (int row=0;row<ROWS;row++){
            for (int col=0;col<COLUMNS;col++){
                Circle circle=new Circle();
                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);
                circle.setSmooth(true);
                circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;
    }

    /**
     * It creates the 7 columns into 7 vertical rectangles so that they can be clicked anywhere to add the discs
     * Adds functionality to them for hovering and bringing the cursor out of the vicinity of the rectangles
     * If a rectangle is clicked, it adds a disc to the particular column by calling the insertDisc method
     * While calling insertDisc, The Disc class is called where the Disc constructor is invoked which sets the color of the disc according to the player
     * @return rectangleList
     */

    public List<Rectangle> createClickableColumns(){

        List<Rectangle> rectangleList=new ArrayList<>();
        for (int col=0;col<COLUMNS;col++){
            Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee66")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column=col;//because of lambda expression
            rectangle.setOnMouseClicked(event -> {
                if (isAllowedToInsert) {
                    isAllowedToInsert=false;//To avoid multiple disc fall at a time
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });
            rectangleList.add(rectangle);
        }

        return rectangleList;
    }

    /**
     * The function takes the disc and the particular clicked column for the addition of disc
     * The function traverses the rows one by one using a while loop and finds the empty grid spot as soon as it finds a null
     * getDiscIfPresent just checks if the row and column inputs are valid
     * The disc is added to the array structurally and to the pane for graphical changes
     * Animation of the drop-age of the disc from the top of the pane is done using TranslateTransition
     * After the animation is finished, a check if the game has ended or not is done using the gameEnded()
     * If a player wins, the gameEnded() condition is satisfied and the gameOver() is called
     * If no one wins, player one's turn is converted to player two's turn using the =! operator
     * @param disc is basically obtained after the invocation of the constructor of Disc class
     * @param column points to the column which was clicked
     */

    public void insertDisc(Disc disc,int column){

        int row=ROWS-1;
        while (row >= 0){

            if (getDiscIfPresent(row,column)==null)
                break;
            row--;
        }
        if (row<0)  //If it is full,we cannot insert anymore disc
            return;

        insertedDiscsArray[row][column]=disc;  //For Structural changes for  developers

        counter++;


        if(!isJUnitTest()){
            insertedDiscPane.getChildren().add(disc);//For visual changes
        }
        //Move the dropped circle towards right to fit the hole
        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

        int currRow=row;
        TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.4),disc);
        translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);


        //Now need to toggle between the player
        translateTransition.setOnFinished(event -> {
            isAllowedToInsert=true;
            if(gameEnded(currRow,column)){
                gameOver();
                return;//no further check if one player wins the game
            }

            isPlayerOneTurn =! isPlayerOneTurn;//Player one turn become player two turn
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });


        if(!isJUnitTest()){
            translateTransition.play();
        }

    }

    /**
     * The Point2D class defines a point representing a location in (x,y) coordinate space.
     * This class is only the abstract superclass for all objects that store a 2D coordinate.
     * The actual storage representation of the coordinates is left to the subclass.
     * It is basically used to create a geometric list of discs which have been placed in the GridPane using the above methods
     * An Example is given below to demonstrate it's working from line 217
     * It calculates the horizontal, vertical and two diagonals for the particular row and column value it's passed
     * It calls the function checkCombination to analyze the winning conditions and simply returns a simple boolean true or false     *
     * @param row points to the current row in question
     * @param column points to the current column in question
     * @return isEnded, a boolean which gives true if the game is over and false if it is not
     */

    public boolean gameEnded(int row, int column){

        //EXAMPLE -
        //Vertical Points
        //A small example: player has inserted his last disc at row=2 , column=3
        //
        //index of each element present in column [row][column]:  0,3   1,3   2,3   3,3   4,3   5,3-->Poind2D
        //notice same column of 3.

        List<Point2D> verticalPointes=IntStream.rangeClosed(row-3,row+3)  //range of row values= 0,1,2,3,4,5
                .mapToObj(r-> new Point2D(r,column))  //0,3  1,3  2,3   3,3  4,3  5,3 ==> Point2D  x,y
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints=IntStream.rangeClosed(column-3,column+3)
                .mapToObj(col-> new Point2D(row,col))
                .collect(Collectors.toList());

        Point2D startPoint1 =new Point2D(row-3,column+3);
        List<Point2D> digonal1Point=    IntStream.rangeClosed(0,6)
                .mapToObj(i-> startPoint1.add(i,-i))
                .collect(Collectors.toList());

        Point2D startPoint2 =new Point2D(row-3,column-3);
        List<Point2D> digonal2Point=    IntStream.rangeClosed(0,6)
                .mapToObj(i-> startPoint2.add(i,i))
                .collect(Collectors.toList());


        boolean isEnded=checkCombination(verticalPointes) || checkCombination(horizontalPoints)
                || checkCombination(digonal1Point)
                || checkCombination(digonal2Point);

        return isEnded;
    }

    /**
     * This function takes the Point2D List as an argument
     * It converts the X,Y coordinates to array indexes for rows and columns
     * getDiscIfPresent is again called this time to simply get the exact Array value
     * It basically checks a condition which points to the fact if the last inserted Disc belongs to the current player
     * If it does, it increments the chain value
     * If the chain value gets to 4 without becoming 0 again (The case in which the opposite player's disc is present), it returns true else false
     * @param points points to the 2D coordinate values of the Disc in terms of Point2D object
     * @return true if chain == 4 else false
     */

    private boolean checkCombination(List<Point2D> points) {
        int chain = 0;

        for (Point2D point: points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            //getting disc at particular row and column
            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {
                // if the last inserted Disc belongs to the current player
                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }

        return false;//as we haven't got the combination
    }

    /**
     * Checks if the provided values for the rows and columns of the Array are valid
     * The function also comes handy in getting the current Disc and it's position by simply calling it
     * @param row points to the current row
     * @param column points to the current column
     * @return insertedDiscsArray[row][column] , the Disc location
     */

    public Disc getDiscIfPresent(int row,int column){  //To prevent ArrayIndexOutOfBoundIndex exception
        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)  // If row or column index is invalid
            return null;

        return insertedDiscsArray[row][column];//return element at this position within our array
    }


    /**
     * Pops up an alert bar stating that the player has won the match
     * After the animation end, it handles illegal state exception
     * Removing the functionality will never stop the program from exiting even after getting 4 Discs in a row
     */

    private void gameOver(){

        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is: " + winner);

        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText(winner+" won in "+counter+" chances");
        alert.setContentText("Want to play again?");
        ButtonType yeBtn=new ButtonType("Yes");
        ButtonType noBtn =new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yeBtn,noBtn);


        Platform.runLater(()->{
            Optional<ButtonType> btnClicked= alert.showAndWait();

            if (btnClicked.isPresent() && btnClicked.get() ==yeBtn){
                counter = 0;
                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });

    }

    /**
     * It removes the inserted discs graphically from the pane
     * It removes the inserted discs structurally from the array
     * sets isPlayerOneTurn to true which sets it's turn
     * calls createPlayground() to prepare a fresh new game environment
     */

    public void resetGame() {

        if(!isJUnitTest()){
            insertedDiscPane.getChildren().clear(); //Remove all Inserted disc from pane
        }

        //Now these loops will structurally make all elements of insertedDiscArray back to null
        for (int row = 0; row <insertedDiscsArray.length ; row++) {
            for (int column = 0; column < insertedDiscsArray[row].length; column++) {
                insertedDiscsArray[row][column] = null;
            }
        }
        isPlayerOneTurn=true;//let player one start the game
        if(!isJUnitTest()){
            playerNameLabel.setText(PLAYER_ONE);
            createPlayground();  //prepare a fresh playground
        }

    }

    /**
     *  Disc class which gets it's constructor called to decided the color of the disc in accordance with the current player's turn
     */

    public static class Disc extends Circle{

        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove=isPlayerOneMove;

            setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayerOneMove? Color.valueOf(discColor1) : Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
        }
    }


    public boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }


}
