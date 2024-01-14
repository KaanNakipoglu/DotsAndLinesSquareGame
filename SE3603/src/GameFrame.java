import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.sqlite.SQLiteDataSource;

//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact:melikenazlikaraca@gmail.com & kaan.n@hotmail.com
public class GameFrame extends JFrame {

    private Connection connection;
    private JFrame scoreFrame;

    private JPanel dotPanel;
    private JLabel gameLabel;
    private JPanel scorePanel;
    private JLabel mySquareScore;
    private JLabel opponentSquareScore;
    private JDialog resultScreen;

    private JDialog warningScreen;

    private Player player;
    private PlayerAction playerAction;

    // Variables for plane setup:
    private final int ROWS = 5; // Number of rows in the matrix
    private final int COLS = 5; // Number of columns in the matrix
    private final int DOTSIZE = 10; // Size of each dot
    private final int GAP = 80; // Gap between dots
    private Color lineColor;
    private final BasicStroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0);
    private final List<Point> horizontalLines = new ArrayList<>();
    private final List<Point> verticalLines = new ArrayList<>();

    private static final String SELECT_QUERY = "SELECT player, square FROM scores";

    JLabel resultLabel;

    // Variables needed to play the game
    private ArrayList<Line> clickedLines = new ArrayList<>(); // clickedLines list contains every line drawn on the matrix by both of the players
    private ArrayList<Point> pointsThatFormSquare = new ArrayList<>();
    private ArrayList<Square> drawnSquares = new ArrayList<>();
    private int squareCount = 0;  // Might change if we read this from db
    private int opponentSquareCount = 0; // Might change if we read this from db


    // Getters and Setters
    public JLabel getResultLabel() {
        return resultLabel;
    }

    public void setResultLabel(JLabel resultLabel) {
        this.resultLabel = resultLabel;
    }
    public JFrame getScoreFrame() {
        return scoreFrame;
    }

    public void setScoreFrame(JFrame scoreFrame) {
        this.scoreFrame = scoreFrame;
    }

    public int getSquareCount() {
        return squareCount;
    }

    public int getOpponentSquareCount() {
        return opponentSquareCount;
    }
    public Color getLineColor() {
        return lineColor;
    }
    public void setLineColor(Color lineColor) {
        System.out.println("Line color is: " + Player.convertColorToString(lineColor));
        this.lineColor = lineColor;
    }
    public PlayerAction getPlayerAction() {
        return playerAction;
    }
    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public JPanel getDotPanel() {
        return dotPanel;
    }

    public void setDotPanel(JPanel dotPanel) {
        this.dotPanel = dotPanel;
    }
    private static final String INSERT_QUERY = "INSERT INTO scores (player, square) VALUES (?, ?)";

    public JLabel getGameLabel() {
        return gameLabel;
    }

    public void setGameLabel(JLabel gameLabel) {
        this.gameLabel = gameLabel;
    }

    public JPanel getScorePanel() {
        return scorePanel;
    }

    public void setScorePanel(JPanel scorePanel) {
        this.scorePanel = scorePanel;
    }

    public JLabel getMySquareScore() {
        return mySquareScore;
    }

    public void setMySquareScore(JLabel mySquareScore) {
        this.mySquareScore = mySquareScore;
    }

    public JLabel getOpponentSquareScore() {
        return opponentSquareScore;
    }

    public void setOpponentSquareScore(JLabel opponentSquareScore) {
        this.opponentSquareScore = opponentSquareScore;
    }

    public JDialog getResultScreen() {
        return resultScreen;
    }

    public void setResultScreen(JDialog resultScreen) {
        this.resultScreen = resultScreen;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getROWS() {
        return ROWS;
    }

    public int getCOLS() {
        return COLS;
    }

    public int getDOTSIZE() {
        return DOTSIZE;
    }

    public int getGAP() {
        return GAP;
    }

    public BasicStroke getDashedStroke() {
        return dashedStroke;
    }

    public List<Point> getHorizontalLines() {
        return horizontalLines;
    }

    public List<Point> getVerticalLines() {
        return verticalLines;
    }

    public ArrayList<Line> getClickedLines() {
        return clickedLines;
    }

    public void setClickedLines(ArrayList<Line> clickedLines) {
        this.clickedLines = clickedLines;
    }

    public ArrayList<Point> getPointsThatFormSquare() {
        return pointsThatFormSquare;
    }

    public void setPointsThatFormSquare(ArrayList<Point> pointsThatFormSquare) {
        this.pointsThatFormSquare = pointsThatFormSquare;
    }

    public ArrayList<Square> getDrawnSquares() {
        return drawnSquares;
    }

    public void setDrawnSquares(ArrayList<Square> drawnSquares) {
        this.drawnSquares = drawnSquares;
    }

    public void setSquareCount(int squareCount) {
        this.squareCount = squareCount;
    }

    public void setOpponentSquareCount(int opponentSquareCount) {
        this.opponentSquareCount = opponentSquareCount;
    }

    // Constructor
    public void insertData() {
        //you should connect your db here
        String URL ="your db";
        try {
            connection = DriverManager.getConnection(URL);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY);

            preparedStatement.setString(1, player.getUserName());
            preparedStatement.setInt(2, squareCount);

            preparedStatement.executeUpdate();

            System.out.println("Player inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately, log, or rethrow
        }
    }
    public GameFrame(Player player) {

        this.player = player;

        // GUI setup
        setTitle(player.getUserName() + " Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600); // Adjust the size as needed
        setLayout(new BorderLayout(5,5));
        setResizable(false);

        JPanel gameLabelPanel = new JPanel();
        gameLabel = new JLabel("SQUARE GAME", SwingConstants.CENTER);
        gameLabel.setVerticalTextPosition(SwingConstants.CENTER);
        gameLabel.setFont(new Font("Algerian", Font.PLAIN, 28));
        gameLabelPanel.add(gameLabel);

        gameLabelPanel.setPreferredSize(new Dimension(800, 100));

        calculateHorizontalLines();
        calculateVerticalLines();

        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(25,200));

        dotPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawFilledLines(g);
                drawSquares(g);
                drawDots(g);
                drawDashedLines(g);
                setScores();
                System.out.println(clickedLines);
            }
        };
        dotPanel.setPreferredSize(new Dimension(500, 200));

        scorePanel = new JPanel();
        GridLayout gridLayout = new GridLayout(0, 1);
        gridLayout.setHgap(5); // Horizontal gap
        gridLayout.setVgap(2); // Vertical gap
        scorePanel.setLayout(gridLayout);

        mySquareScore = new JLabel("Player 1: 5");
        mySquareScore.setFont(new Font("Arial", Font.PLAIN, 24));
        mySquareScore.setPreferredSize(new Dimension(200,50));
        mySquareScore.setVerticalAlignment(SwingConstants.CENTER);

        opponentSquareScore = new JLabel("Player 2: 10");
        opponentSquareScore.setFont(new Font("Arial", Font.PLAIN, 24));
        opponentSquareScore.setPreferredSize(new Dimension(200,50));
        opponentSquareScore.setVerticalAlignment(SwingConstants.TOP);

        scorePanel.add(mySquareScore);
        scorePanel.add(opponentSquareScore);
        scorePanel.setPreferredSize(new Dimension(200, 200));
        add(gameLabelPanel, BorderLayout.NORTH);
        add(dotPanel, BorderLayout.CENTER);
        add(scorePanel, BorderLayout.EAST);
        add(separator, BorderLayout.WEST);
        setScores();

        // Mouse Listener to get the coordinates of the clicked line
        dotPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(player.isTurn()){
                    int clickedX = e.getX();
                    int clickedY = e.getY();
                    Line clickedLine = findClosestLine(clickedX, clickedY);
                    if (clickedLine != null){
                        clickedLine.setColor(lineColor);
                        boolean isEqual = false;

                        // Checking if the clicked line was clicked before since players cannot draw one line onto another
                        for (int i=0;i<clickedLines.size();i++){
                            if (clickedLine.getPoint1().getX() == clickedLines.get(i).getPoint1().getX() && clickedLine.getPoint1().getY() == clickedLines.get(i).getPoint1().getY()
                                    && clickedLine.getPoint2().getX() == clickedLines.get(i).getPoint2().getX() && clickedLine.getPoint2().getY() == clickedLines.get(i).getPoint2().getY()){
                                isEqual = true;
                            }
                        }

                        // If line was not clicked before
                        if (!isEqual){

                            clickedLines.add(clickedLine);
                            // Assuming the clicked line does not create a square
                            player.setTurn(false);
                            // checkSquare() changes player's turn back to true if the line creates a square if not it remains false
                            checkSquare();
                            dotPanel.repaint();

                            // Sending action to player object to send other player
                            playerAction = new PlayerAction(clickedLine, squareCount);
                            player.sendAction();
                        }
                    }
                }
                else{
                    displayWarning();
                }

            }
        });
        setVisible(true);
    }


    // Method to find the closest line to the point user clicked
    public Line findClosestLine(int x, int y) {
        Line line;
        for (Point point : horizontalLines) {
            int px = (int) point.getX();
            int py = (int) point.getY();
            if (x >= px && x <= px + DOTSIZE + GAP && y >= py - 5 && y <= py + 5) {
                line = new Line(point, new Point(px + DOTSIZE + GAP, py));
                return line;
            }
        }
        for (Point point : verticalLines) {
            int px = (int) point.getX();
            int py = (int) point.getY();
            if (x >= px - 5 && x <= px + 5 && y >= py && y <= py + DOTSIZE + GAP) {
                line = new Line(point, new Point(px, py + DOTSIZE + GAP));
                return line;
            }
        }
        return null;
    }


    // Methods to create the matrix
    private void calculateHorizontalLines(){
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS - 1; col++) {
                horizontalLines.add(new Point(col * (DOTSIZE + GAP) + DOTSIZE / 2, row * (DOTSIZE + GAP) + DOTSIZE / 2));
            }
        }
    }

    private void calculateVerticalLines(){
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS - 1; row++) {
                verticalLines.add(new Point(col * (DOTSIZE + GAP) + DOTSIZE / 2, row * (DOTSIZE + GAP) + DOTSIZE / 2));
            }
        }
    }

    private void drawDots(Graphics g) {
        g.setColor(Color.BLACK);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = col * (DOTSIZE + GAP);
                int y = row * (DOTSIZE + GAP);
                g.fillOval(x, y, DOTSIZE, DOTSIZE);
            }
        }
    }

    private void drawDashedLines(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);

        // Draw dashed lines
        g2d.setStroke(dashedStroke);
        for (Point point : horizontalLines) {
            if (!clickedLines.contains(point)) {
                int x = (int) point.getX();
                int y = (int) point.getY();
                g2d.drawLine(x, y, x + DOTSIZE + GAP, y);
            }
        }
        for (Point point : verticalLines) {
            if (!clickedLines.contains(point)) {
                int x = (int) point.getX();
                int y = (int) point.getY();
                g2d.drawLine(x, y, x, y + DOTSIZE + GAP);
            }
        }
    }


    // Method to draw lines of players
    private void drawFilledLines(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        for (Line line: clickedLines) {
            g2d.setColor(line.getColor());
            int x1 = (int) line.getPoint1().getX();
            int y1 = (int) line.getPoint1().getY();
            int x2 = (int) line.getPoint2().getX();
            int y2 = (int) line.getPoint2().getY();

            g2d.drawLine(x1,y1,x2,y2);
        }
    }

    // Method to draw squares
    public void drawSquares(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        if (!drawnSquares.isEmpty()){
            for (Square square: drawnSquares){
                g2d.setColor(square.getColor());
                int x = (int) square.getTopLeftCorner().getX();
                int y = (int) square.getTopLeftCorner().getY();
                g2d.fillRect(x, y,GAP + DOTSIZE, GAP+DOTSIZE);
            }
        }
    }

    // Method to check if a square is formed from the drawn lines
    public void checkSquare(){
        if(clickedLines.size()>=4){
            // Each clicked line is checked to see if it forms a square. For that check other 3 edge's existence
            for(Line line : clickedLines){
                int x = (int) line.getPoint1().getX();
                int y = (int) line.getPoint1().getY();

                Line line2;
                Line line3;
                Line line4;

                if (line.isHorizontal()){
                    line2 = new Line(new Point(x+DOTSIZE+GAP, y), new Point(x+GAP+DOTSIZE,y+GAP+DOTSIZE));
                    line3 = new Line(new Point(x, y+GAP+DOTSIZE), new Point(x+GAP+DOTSIZE,y+GAP+DOTSIZE));
                    line4 = new Line(new Point(x, y), new Point(x,y+GAP+DOTSIZE));
                }
                else {
                    line2 = new Line(new Point(x, y), new Point(x+GAP+DOTSIZE,y));
                    line3 = new Line(new Point(x+DOTSIZE+GAP , y), new Point(x+GAP+DOTSIZE,y+GAP+DOTSIZE));
                    line4 = new Line(new Point(x, y+DOTSIZE+GAP), new Point(x+DOTSIZE+GAP,y+GAP+DOTSIZE));
                }

                boolean formsSquare = clickedLines.contains(line2) && clickedLines.contains(line3) && clickedLines.contains(line4);
                System.out.println(line + " if it forms a square -> " + formsSquare);

                if (formsSquare){
                    // If the point did not create a square before
                    if (!pointsThatFormSquare.contains(line.getPoint1())){
                        // Last drawn line's color should be the color of the square
                        Square square = new Square(line.getPoint1(), clickedLines.get(clickedLines.size()-1).getColor());
                        pointsThatFormSquare.add(line.getPoint1());

                        // Every player stores only their square number not the opponent's
                        if (square.getColor() == lineColor){
                            squareCount++;
                            // Current player created a square, so it will keep its turn
                            player.setTurn(true);
                        }
                        // if other player's line creates a square it's still other player's turn
                        else {
                            player.setTurn(false);
                        }
                        drawnSquares.add(square);
                    }
                    System.out.println("Square Count: " + squareCount);
                }
            }
        }
    }

    // Method to apply other player's action
    public void applyOtherPlayerMoves(PlayerAction otherPlayerAction){
        System.out.println("Inside applyOtherPlayerMoves() other player's action: " + otherPlayerAction.toString());
        clickedLines.add(otherPlayerAction.getLineDrawn());
        opponentSquareCount = otherPlayerAction.getPlayerSquareCounter();
        // Assuming the received line from other player did not create a square
        player.setTurn(true);
        // checkSquare() controls if the received line creates a square and changes the current player's turn accordingly
        checkSquare();
        dotPanel.repaint();
    }

    // Method to display square counts on the screen
    public void setScores(){
        mySquareScore.setText("Me: " + getSquareCount());
        opponentSquareScore.setText("Opponent: " + getOpponentSquareCount());

        // If all possible squares are drawn
        if (getSquareCount() + getOpponentSquareCount() == 16){
            checkResult();
        }
    }

    public void checkResult(){
        resultScreen = new JDialog(this, "Result");
        resultScreen.setSize(500,200);
        resultScreen.setLocation(this.getLocation());
        resultScreen.setLayout(new BorderLayout());

        resultLabel = new JLabel("Initial Text",SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        insertData();

        // Win condition
        if (getSquareCount() > getOpponentSquareCount()){
            resultLabel.setText("YOU WIN!!");
        }
        // Tie condition
        else if (getSquareCount() == getOpponentSquareCount()) {
            resultLabel.setText("IT'S A TIE!!");
        }
        // Lost Condition
        else {
            resultLabel.setText("YOU LOST :(");
        }

        // Replay Button that resets the game
        JPanel buttonPanel = new JPanel();
        JButton replayButton = new JButton("Replay");
        replayButton.setPreferredSize(new Dimension(100, 50));
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultScreen.dispose();
                replay();
            }
        });
        buttonPanel.add(replayButton);

        JButton showScoresButton = new JButton("Show Scores");
        String[] columnNames = {"player","square"};
        showScoresButton.setPreferredSize(new Dimension(100, 50));
        showScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scoreFrame = new JFrame("Scores");
                scoreFrame.setLayout(new BorderLayout());
                //TableModel tm = new TableModel();
                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(columnNames);
                JTable table = new JTable();
                table.setModel(model);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.setFillsViewportHeight(true);
                JScrollPane scroll = new JScrollPane(table);
                scroll.setHorizontalScrollBarPolicy(
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scoreFrame.add(scroll);
                scoreFrame.setVisible(true);
                scoreFrame.setSize(400, 300);
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()){
                        String playerName= resultSet.getString("player");
                        int playerScore= resultSet.getInt("square");
                        model.addRow(new Object[]{playerName, playerScore});

                    }
                }
                catch (Exception exception){}

            }
        });
        buttonPanel.add(showScoresButton);


        resultScreen.add(resultLabel, BorderLayout.CENTER);
        resultScreen.add(buttonPanel, BorderLayout.SOUTH);
        resultScreen.setVisible(true);
    }

    // Method to reset the game
    public void replay(){
                clickedLines.clear();
                pointsThatFormSquare.clear();
                drawnSquares.clear();
                squareCount = 0;
                opponentSquareCount = 0;
                dotPanel.repaint();
    }

    public void displayWarning(){
        warningScreen = new JDialog(this, "Warning");
        warningScreen.setModal(true);
        warningScreen.setSize(500,200);
        warningScreen.setLocation(this.getLocation());
        warningScreen.setLayout(new BorderLayout());
        JLabel warningLabel = new JLabel();
        warningLabel = new JLabel("It's not your turn!",SwingConstants.CENTER);
        warningLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        warningScreen.add(warningLabel, BorderLayout.CENTER);

        warningScreen.setVisible(true);
    }
}
class Line implements Serializable {
    @Serial
    private static final long serialVersionUID = 2044454309080283268L;
    private Point point1;
    private Point point2;
    private boolean isHorizontal;
    private boolean isVertical;
    private Color color;

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public Color getColor() {
        return color;
    }

    public void setPoint1(Point point1) {
        this.point1 = point1;
    }

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    Line(Point point1, Point point2){
        this.point1 = point1;
        this.point2 = point2;
        if (point1.getY() == point2.getY()){
            isHorizontal = true;
            isVertical = false;
        }
        else {
            isHorizontal = false;
            isVertical = true;
        }
    }

    public boolean equals(Object o){
        if (o == null)
            return false;
        if (!(o instanceof Line))
            return false;
        Line line = (Line) o;
        if (line.point1.getX() == this.point1.getX()
                && line.point1.getY() == this.point1.getY()
                && line.point2.getX() == this.point2.getX()
                && line.point2.getY() == this.point2.getY())
            return true;
        else
            return false;
    }

    public String toString(){
        return "Point1=(" + point1.getX() + "," + point1.getY() + ") Point2=(" + point2.getX() + "," + point2.getY() + ") ";
    }
}
class Square{
    private Point topLeftCorner;  // Each square is identified by its top left corner
    private Color color;
    public Square(Point topLeftCorner, Color color){
        this.topLeftCorner = topLeftCorner;
        this.color = color;
    }
    public Point getTopLeftCorner() {
        return topLeftCorner;
    }
    public void setTopLeftCorner(Point topLeftCorner) {
        this.topLeftCorner = topLeftCorner;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}