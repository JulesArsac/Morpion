package morpion;

public class Player {
    private double piece;
    private String name;
    private boolean ia = false;

    private int score = 0;

    public Player(int piece, String name) {
        this.piece = piece;
        this.name = name;
    }

    public Player() {}

    public double getPiece() {
        return piece;
    }

    public void setPiece(double piece) {
        this.piece = piece;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIa() {
        return ia;
    }

    public void setIa(boolean ia) {
        this.ia = ia;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
