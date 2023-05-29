package byow.Core;

public class Position {
    private final int x_position;
    private final int y_position;
    public Position(int x, int y) {
        this.x_position = x;
        this.y_position = y;
    }
    public int getX_position() {
        return x_position;
    }
    public int getY_position() {
        return y_position;
    }

    public boolean equals (Position otherPosition) {
        return this.getX_position() == (otherPosition.getX_position()) && this.getY_position() == otherPosition.getY_position();
    }
}
