import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ShapesTest {
    Circle circle1, circle2, circle3;
    Rectangle rectangle1, rectangle2, rectangle3;
    Board board1;
    Board board2;
    int boardSize = 9;
    double board1TotalArea;

    @BeforeEach
    void setUp() {
        circle1 = new Circle(Color.BLACK,10);
        circle2 = new Circle(Color.BLACK,10);
        circle3 = new Circle(Color.BLACK,5);
        rectangle1 = new Rectangle(Color.BLUE, 4,5);
        rectangle2 = new Rectangle(Color.BLUE, 4,5);
        rectangle3 = new Rectangle(Color.BLUE,2,10);

        board1 = new Board(boardSize);
        for (int i = 0; i < boardSize; i++ ){
            for (int j = 0; j < i; j++) {
                board1.add(new Rectangle(Color.BLUE, i+1, j+1), i, j);
                board1.add(new Circle(Color.RED, i+j+1), j, i);
            }
        }
        board1TotalArea = 11350.35309237555;
        board2 = new Board(1);
    }

    @Test
    void getAreaShouldMatchShapeFormula() {
        assertEquals(314.1592653589793, circle1.getArea());
        assertEquals(20, rectangle1.getArea());
    }

    @Test
    void getShapesAreaShouldAccumulateAllShapes() {
        assertEquals(board1TotalArea, board1.getShapesArea());
        assertEquals(0, board2.getShapesArea());
        // add and remove an item
        assertTrue(board1.add(rectangle1,3,3));
        assertNotNull(board1.remove(3,3));
        // check total shapes area invariant
        assertEquals(board1TotalArea, board1.getShapesArea());
    }

    @Test
    void equalsShouldFollowShapeRules() {
        assertEquals(circle1, circle2);
        assertEquals(rectangle1, rectangle2);
        assertNotEquals(circle1, circle3);
        assertNotEquals(rectangle2, rectangle3);
    }

    @Test
    @Disabled
    void hashCodeShouldFollowEquals() {
        assertEquals(circle1.hashCode(), circle2.hashCode());
        assertEquals(rectangle1.hashCode(), rectangle2.hashCode());
        assertNotEquals(circle1.hashCode(), circle3.hashCode());
    }

    @Test
    void addRemoveShouldPreserveBoardIntegrity(){
        //when a shape is added it returns true
        assertTrue(board1.add(rectangle1, 4, 4));
        //when a position is occupied it returns false
        assertFalse(board1.add(rectangle1, 4, 4));
        // remove returns the shape that has been removed
        assertEquals(rectangle1, board1.remove(4,4));
        // check shape has been removed
        assertNull(board1.remove(4, 4));
    }
    @Test
    void addRemoveShouldCheckOutOfBounds() {
        Throwable t;
        t = assertThrows(IndexOutOfBoundsException.class,
                () -> {board1.remove(-1,-1);}
        );
        assertEquals("Position -1,-1 is not available on a board of size "+boardSize, t.getMessage());

        t = assertThrows(IndexOutOfBoundsException.class,
                () -> {board1.add(circle2,-1,0);}
        );
        assertEquals("Position -1,0 is not available on a board of size "+boardSize, t.getMessage());

        t = assertThrows(IndexOutOfBoundsException.class,
                () -> {board2.add(circle2,0,1);}
        );
        assertEquals("Position 0,1 is not available on a board of size 1", t.getMessage());

        t = assertThrows(IndexOutOfBoundsException.class,
                () -> {board2.remove(1,1);}
        );
        assertEquals("Position 1,1 is not available on a board of size 1", t.getMessage());
    }

    @Test
    void getGridShouldExposeAllShapes(){
        //The getGrid method returns a 2D array of Shape
        Shape shapes [][] = board1.getGrid();
        for(int x = 0; x < shapes.length; x++){
            for(int y = 0; y < shapes.length;y++){
                Shape s = shapes[x][y];
                assertTrue(s == null || s instanceof Shape);
            }
        }
    }
}