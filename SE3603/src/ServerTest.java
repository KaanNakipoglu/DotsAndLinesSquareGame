import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact: melikenazlikaraca@gmail.com & kaan.n@hotmail.com

class ServerTest {
    private ServerSocket mockServerSocket;
    private Server server;

    @BeforeEach
    void setUp() throws IOException {
        mockServerSocket = new ServerSocket(0);
        server = new Server(mockServerSocket);
    }

    @AfterEach
    void tearDown() {
        server.closeServerSocket();
    }

    @Test
    void testStartServer() {
        Thread serverThread = new Thread(() -> {
            server.startServer();
        });
        serverThread.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertDoesNotThrow(() -> {
            Socket mockClientSocket = new Socket("localhost", mockServerSocket.getLocalPort());
            assertTrue(mockClientSocket.isConnected());
        });

        server.closeServerSocket();

        try {
            serverThread.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(mockServerSocket.isClosed());
    }
}
