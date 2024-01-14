import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.Socket;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact:melikenazlikaraca@gmail.com & kaan.n@hotmail.com
class PlayerHandlerTest {
    Socket mockSocket;

    @BeforeEach
    void setup(){ mockSocket= new Socket();

    }
@Test
    void testRemovePlayerHandler() {
        // Create a PlayerHandler instance
        PlayerHandler playerHandler = new PlayerHandler(mockSocket);

        // Add the playerHandler to the playerHandlers list
        ArrayList<PlayerHandler> playerHandlers = new ArrayList<>();
        playerHandlers.add(playerHandler);
        PlayerHandler.setPlayerHandlers(playerHandlers);

        // Call the removePlayerHandler method
        playerHandler.removePlayerHandler();

        // Verify that the playerHandler is removed from the playerHandlers list
        assertEquals(0, playerHandlers.size());
    }



}