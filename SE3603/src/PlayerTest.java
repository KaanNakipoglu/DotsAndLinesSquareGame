import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact:melikenazlikaraca@gmail.com & kaan.n@hotmail.com
class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(new Socket());
    }
    @Test
    public void testConstructor() throws IOException {
        Socket socket = new Socket();
        Player player = new Player(socket);

        // Verify socket assignment
        assertSame(socket, player.getSocket());

        // Verify streams and writers are initialized
        assertNull(player.getBufferedReader());
        assertNull(player.getBufferedWriter());
        assertNull(player.getObjectInputStream());
        assertNull(player.getObjectOutputStream());

        // Verify color assignment
        assertNotNull(player.getPlayerColor());
        assertTrue(Player.getColors().contains(player.getPlayerColor()));

        // Verify username generation
        assertNotNull(player.getUserName());
        assertTrue(player.getUserName().startsWith("Player "));

        // Close resources
        socket.close();
    }

    @Test
    void testAssignColor() {
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        player.setColors(colors);
        player.setUsedColors(new ArrayList<>());

        player.assignColor();


        assertNotNull(player.getPlayerColor());
        assertTrue(colors.contains(player.getPlayerColor()));
    }

    @Test
    void testConvertColorToString() {
        player.setPlayerColor(Color.BLUE);

        assertEquals("Blue", player.convertColorToString(player.getPlayerColor()));
    }












}
