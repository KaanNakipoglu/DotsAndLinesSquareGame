import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact:melikenazlikaraca@gmail.com & kaan.n@hotmail.com
public class Player {
    private boolean isTurn;

    private GameFrame gameFrame;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String userName;
    private Color playerColor;

    public static ArrayList<Color> colors = new ArrayList<>(){
        {
            add(Color.BLUE);
            add(Color.CYAN);
            add(Color.GRAY);
            add(Color.GREEN);
            add(Color.MAGENTA);
            add(Color.ORANGE);
            add(Color.PINK);
            add(Color.RED);
            add(Color.YELLOW);
            add(Color.LIGHT_GRAY);
            add(Color.DARK_GRAY);
            add(Color.WHITE);
        }
    };
    private static ArrayList<Color> usedColors = new ArrayList<>();


    // Getters and Setters
    public GameFrame getGameFrame() {
        return gameFrame;
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }
    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(Color playerColor) {
        this.playerColor = playerColor;
    }

    public static ArrayList<Color> getColors() {
        return colors;
    }

    public static void setColors(ArrayList<Color> colors) {
        Player.colors = colors;
    }

    public static ArrayList<Color> getUsedColors() {
        return usedColors;
    }

    public static void setUsedColors(ArrayList<Color> usedColors) {
        Player.usedColors = usedColors;
    }

    // Constructor
    public Player(Socket socket){
        // Player is assigned a random color from the list
        assignColor();
        userName =  "Player " + convertColorToString(playerColor);
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            gameFrame = new GameFrame(this);
            gameFrame.setLineColor(playerColor);
            this.isTurn = true;
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter, objectInputStream, objectOutputStream);
        }
    }
    public void assignColor(){
        Random rand = new Random();
        while (true){
            Color chosenColor = colors.get(rand.nextInt(colors.size()));
            if (!usedColors.contains(chosenColor)){
                playerColor = chosenColor;
                break;
            }
        }
    }
    public static String convertColorToString(Color color){
        if (color == Color.BLUE){
            return "Blue";
        } else if (color == Color.CYAN) {
            return "Cyan";
        } else if (color == Color.GRAY) {
            return "Gray";
        }else if (color == Color.GREEN){
            return "Green";
        } else if (color == Color.MAGENTA) {
            return "Magenta";
        } else if (color == Color.ORANGE) {
            return "Orange";
        } else if (color == Color.PINK) {
            return "Pink";
        } else if (color == Color.RED) {
            return "Red";
        } else if (color == Color.DARK_GRAY) {
            return "Dark Gray";
        }else if (color == Color.LIGHT_GRAY) {
            return "Light Gray";
        } else if (color == Color.WHITE) {
            return "White";
        } else {
            return "Yellow";
        }
    }

    public void sendUserName() {
        try {
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter, objectInputStream, objectOutputStream);
        }
    }
    public void sendAction(){
        try {
            if (socket.isConnected() && gameFrame.getPlayerAction() != null){
                PlayerAction playerAction = gameFrame.getPlayerAction();
                System.out.println("Player action: " + playerAction.toString());
                objectOutputStream.writeObject(playerAction);
                objectOutputStream.flush();
            }
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter, objectInputStream, objectOutputStream);
        }
    }

    public void listenForAction(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PlayerAction otherPlayerAction;

                while (socket.isConnected()){
                    try {
                        otherPlayerAction = (PlayerAction) objectInputStream.readObject();
                        System.out.println("Other player's action: " + otherPlayerAction.toString());
                        gameFrame.applyOtherPlayerMoves(otherPlayerAction);
                    }catch (IOException e){
                        closeEverything(socket, objectInputStream, objectOutputStream);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(objectInputStream != null){
                objectInputStream.close();
            }
            if (objectOutputStream != null){
                objectOutputStream.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
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

    public static void main(String[] args) throws IOException{
        // Creating socket to connect to server that is listening in port 1234
        Socket socket = new Socket("localhost", 1234);

        // Creating player object
        Player player = new Player(socket);
        player.sendUserName();
        player.listenForAction();
    }
}
