package sid;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    void test_createGameStructureGrid() {

        Controller c=new Controller();
        Shape rectangleWithHoles=  c.createGameStructureGrid();
        assertEquals(Color.WHITE, rectangleWithHoles.getFill());


    }

    @Test
    void test_createClickableColumns(){
        Controller c = new Controller();
        List<Rectangle> rectangleList=c.createClickableColumns();
        assertEquals(7, rectangleList.size());
    }


    @Test
    void test_insertDisc(){
        Controller c = new Controller();
        Controller.Disc d1 = new Controller.Disc(true);
        Controller.Disc d2 = new Controller.Disc(false);
        c.insertDisc(d1, 0);
        c.insertDisc(d2, 1);
        assertEquals(d1, c.getDiscIfPresent(5, 0));
        assertEquals(d2, c.getDiscIfPresent(5, 1));
        assertEquals(null, c.getDiscIfPresent(5, 2));
    }

    @Test
    void test_gameEnded(){
        Controller c = new Controller();
        Controller.Disc d1 = new Controller.Disc(true);
        Controller.Disc d2 = new Controller.Disc(false);

        c.insertDisc(d1, 0);
        c.insertDisc(d2, 1);

        c.insertDisc(d1, 0);
        c.insertDisc(d2, 1);

        c.insertDisc(d1, 0);
        c.insertDisc(d2, 1);

        c.insertDisc(d1, 0);
        assertEquals(true, c.gameEnded(2,0));
    }


    @Test
    void test_resetGame() {
        Controller c = new Controller();
        Controller.Disc d1 = new Controller.Disc(true);
        c.insertDisc(d1, 0);
        assertEquals(d1,c.getDiscIfPresent(5,0));
        c.resetGame();
        assertEquals(null,c.getDiscIfPresent(5,0));
    }
}