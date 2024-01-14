import java.io.Serial;
import java.io.Serializable;
//Authors: Melike Nazlı Karaca & Kaan Nakipoğlu
//For further use please contact:melikenazlikaraca@gmail.com & kaan.n@hotmail.com
public class PlayerAction implements Serializable {
    @Serial
    private static final long serialVersionUID = -8804101341462290125L;
    private Line lineDrawn;
    private int playerSquareCounter;

    public PlayerAction(Line lineDrawn, int playerSquareCounter) {
        this.lineDrawn = lineDrawn;
        this.playerSquareCounter = playerSquareCounter;
    }
    public Line getLineDrawn() {
        return lineDrawn;
    }

    public void setLineDrawn(Line lineDrawn) {
        this.lineDrawn = lineDrawn;
    }

    public int getPlayerSquareCounter() {
        return playerSquareCounter;
    }

    public void setPlayerSquareCounter(int playerSquareCounter) {
        this.playerSquareCounter = playerSquareCounter;
    }

    public String toString(){
        return "Line drawn: " + lineDrawn.toString() + " and Current Square Count: " + getPlayerSquareCounter();
    }
}
