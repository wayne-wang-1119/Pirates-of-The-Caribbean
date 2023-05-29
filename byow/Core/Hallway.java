package byow.Core;
import byow.TileEngine.TETile;
import byow.Core.Position;
import byow.TileEngine.Tileset;
import java.util.*;
public class Hallway {
    public Random random;
    private final Position position;
    private final int x;
    private final int y;
    private final int hallwayWidth;
    private final int hallwayHeight;

    private final TETile content = Tileset.WATER;

    public Hallway(Random random, Position pos, int hallwayWidth, int hallwayHeight) {
        this.random = random;
        this.x = pos.getX_position();
        this.y = pos.getY_position();
        this.hallwayHeight = hallwayHeight;
        this.hallwayWidth = hallwayWidth;
        this.position = new Position(x, y);
    }
    public void createHallway(TETile[][] world) {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        if (x + hallwayWidth < 0 || x + hallwayWidth > worldWidth || y + hallwayHeight < 0 || y + hallwayHeight > worldHeight) {
            return;
        }
        for (int row = x; row < x + hallwayWidth; row++) {
            for (int col = y; col < y + hallwayHeight; col++) {
                    world[row][col] = content;
                }
            }
        }
    public int getHallwayWidth() {
        return hallwayWidth;
    }
    public int getHallwayHeight() {
        return hallwayHeight;
    }
    public Position getPosition() {
        return position;
    }
    public int getPosition_X() {
        return x;
    }
    public int getPosition_Y() {
        return y;
    }
}
