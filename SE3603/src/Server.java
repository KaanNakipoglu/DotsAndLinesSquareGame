import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact:melikenazlikaraca@gmail.com & kaan.n@hotmail.com
public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        System.out.println("Waiting for connection...");
    }

    // Getters and Setters
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new player has connected.");
                PlayerHandler playerHandler = new PlayerHandler(socket);
                Thread thread = new Thread(playerHandler);
                thread.start();
            }
        }catch (IOException e){
            closeServerSocket();
        }
    }

    public void closeServerSocket(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
