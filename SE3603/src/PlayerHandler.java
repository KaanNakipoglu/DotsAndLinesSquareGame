import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact:melikenazlikaraca@gmail.com & kaan.n@hotmail.com
public class PlayerHandler implements Runnable{
    public static ArrayList<PlayerHandler> playerHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String playerUserName;

    // Getters and Setters
    public static ArrayList<PlayerHandler> getPlayerHandlers() {
        return playerHandlers;
    }

    public static void setPlayerHandlers(ArrayList<PlayerHandler> playerHandlers) {
        PlayerHandler.playerHandlers = playerHandlers;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public String getPlayerUserName() {
        return playerUserName;
    }

    public void setPlayerUserName(String playerUserName) {
        this.playerUserName = playerUserName;
    }

    public PlayerHandler(Socket socket){
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

            // Read userName
            this.playerUserName = bufferedReader.readLine();
            playerHandlers.add(this);
            System.out.println(playerUserName + " has entered the game.");

        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()){
            try {
                // Read Action of the player
                PlayerAction playerAction = (PlayerAction) objectInputStream.readObject();

                // Send playerAction to other player
                broadcastAction(playerAction);
            }catch (IOException e){
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void broadcastAction(PlayerAction action){
        for (PlayerHandler playerHandler : playerHandlers){
            try {
                // Send one player's action to the other
                if (!playerHandler.playerUserName.equals(playerUserName)){
                    playerHandler.objectOutputStream.writeObject(action);
                    playerHandler.objectOutputStream.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            }
        }
    }
    public void removePlayerHandler(){
        playerHandlers.remove(this);
        System.out.println(playerUserName + " has left the game.");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removePlayerHandler();
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        removePlayerHandler();
        try{
            if(objectInputStream != null){
                objectInputStream.close();
            }
            if (objectOutputStream != null){
                objectOutputStream.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
