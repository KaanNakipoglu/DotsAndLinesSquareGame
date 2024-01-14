import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact:melikenazlikaraca@gmail.com & kaan.n@hotmail.com

public class GameFrameTest {
    GameFrame gameFrame;

    @BeforeEach
    void setUp() {
        gameFrame= new GameFrame(  new Player(new Socket()));
    }


    @Test
    void testFindClosestLine() {

        // Set up some dummy horizontal and vertical lines
        List<Point> horizontalLines = new ArrayList<>();
        List<Point> verticalLines = new ArrayList<>();
        horizontalLines.add(new Point(0, 5));
        verticalLines.add(new Point(5, 0));
        verticalLines.add(new Point(5, 5));

        // Set the lines on the GameFrame instance
        gameFrame.getHorizontalLines().addAll(horizontalLines);
        gameFrame.getVerticalLines().addAll(verticalLines);

        // Test case: x and y coordinates fall within a horizontal line
        Line resultLine1 = gameFrame.findClosestLine(0, 0);
        assertNotNull(resultLine1);
        // Test case: x and y coordinates fall within a vertical line
        Line resultLine2 = gameFrame.findClosestLine(0 ,0);
        assertNotNull(resultLine2);
    }

    @Test
    public void testCheckSquare_SquareNotFormed() {

        // Add lines that don't form a square
        gameFrame.getClickedLines().add(new Line(new Point(10, 10), new Point(90, 10)));
        gameFrame.getClickedLines().add(new Line(new Point(90, 10), new Point(90, 90)));

        gameFrame.checkSquare();
        // Verify no squares are detected
        assertEquals(0, gameFrame.getSquareCount());}

    @Test
    public void testCheckSquare() {

        // Add lines forming a horizontal square
        gameFrame.getClickedLines().add(new Line(new Point(5, 5), new Point(95, 5)));
        gameFrame.getClickedLines().add(new Line(new Point(95, 5), new Point(95, 95)));
        gameFrame.getClickedLines().add(new Line(new Point(5, 5), new Point(5, 95)));
        gameFrame.getClickedLines().add(new Line(new Point(5, 95), new Point(95, 95)));

        gameFrame.checkSquare();

        // Verify square is detected and counted
        assertEquals(1, gameFrame.getSquareCount());
        assertEquals(1, gameFrame.getDrawnSquares().size());
        assertEquals(new Point(5, 5), gameFrame.getDrawnSquares().get(0).getTopLeftCorner());
    }




    @Test
    public void testCheckResult() {
        gameFrame.setSquareCount(3);
        gameFrame.setOpponentSquareCount(2);

        gameFrame.checkResult();

        assertEquals("YOU WIN!!", gameFrame.getResultLabel().getText());

        gameFrame.setSquareCount(5);
        gameFrame.setOpponentSquareCount(5);

        gameFrame.checkResult();

        assertEquals("IT'S A TIE!!", gameFrame.getResultLabel().getText());

        gameFrame.setSquareCount(2);
        gameFrame.setOpponentSquareCount(4);

        gameFrame.checkResult();

        assertEquals("YOU LOST :(", gameFrame.getResultLabel().getText());
    }
    @Test
    void testApplyOtherPlayerMoves() {
        // Create two GameFrame instances for testing
        Socket mockSocket = new Socket();
        GameFrame player1Frame = new GameFrame(new Player(mockSocket));
        GameFrame player2Frame =new GameFrame(new Player(mockSocket));

        // Simulate player 1 drawing a line
        player1Frame.getClickedLines().add(new Line(new Point(0, 0), new Point(0, 10)));

        // Create a PlayerAction object for player 1's action
        PlayerAction player1Action = new PlayerAction(player1Frame.getClickedLines().get(0), player1Frame.getSquareCount());

        // Simulate player 2 applying player 1's moves
        player2Frame.applyOtherPlayerMoves(player1Action);

        // Assert that player 2's clickedLines list is updated
        assertEquals(true, !player2Frame.getClickedLines().isEmpty());
    }

    @Test
    void testSetScores() {
        // Create a GameFrame instance with a dummy Player
        Player dummyPlayer = new Player(new Socket());  // You may need to adjust this depending on your Player constructor

        // Set the square counts directly on the GameFrame instance
        gameFrame.setSquareCount( 5);
        gameFrame.setOpponentSquareCount(10);

        // Invoke the setScores method
        gameFrame.setScores();

        // Verify that the text of mySquareScore and opponentSquareScore has been set correctly
        assertEquals("Me: 5", gameFrame.getMySquareScore().getText());
        assertEquals("Opponent: 10", gameFrame.getOpponentSquareScore().getText());
    }



        @Test
        public void testReplay() {
            gameFrame.setSquareCount(3);
            gameFrame.setOpponentSquareCount(2);
            // Call the replay method
            gameFrame.replay();
            // Assert that all values are reset to their initial state
            assertEquals(0, gameFrame.getSquareCount());
            assertEquals(0, gameFrame.getOpponentSquareCount());
            assertTrue(gameFrame.getClickedLines().isEmpty());
            assertTrue(gameFrame.getPointsThatFormSquare().isEmpty());
            assertTrue(gameFrame.getDrawnSquares().isEmpty());
        }
    }



