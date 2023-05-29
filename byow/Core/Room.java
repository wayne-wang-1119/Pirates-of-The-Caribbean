package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class Room {
    public final int width;
    public final int height;
    private final int i;
    private final int j;
    public Position position;
    private Random RANDOM;
    private final TETile content = Tileset.SAND;
    private final TETile fence = Tileset.TREE;
    private List<Position> side = new ArrayList<>();
    private List<Position> xSide = new ArrayList<>();
    private List<Position> ySide = new ArrayList<>();
    public Room(Position input) {
        i = input.getX_position(); //x pos
        j = input.getY_position(); //y pos
        position = new Position(i, j);
        width = 3;
        height = 3;
    }
    public Room(Random random, Position input, int room_width, int room_height) {
        RANDOM = random;
        i = input.getX_position(); //x pos
        j = input.getY_position(); //y pos
        position = new Position(i, j);
        width = room_width;
        height = room_height;
    }
    public void createRoom(TETile[][] world) {
        int widthLimit = world.length;
        int heightLimit = world[0].length;
        if (i - 1 <= 0 || i + width - 1 >= widthLimit || j - 1 <= 0 || j + height - 1 >= heightLimit) {
            return;
        }
        for (int row = i; row < i + width; row++) {
            for (int col = j; col < j + height; col++) {
                if (row == i || row == i + width - 1) {
                    world[row][col] = fence;
                    if (col > j && col < j + height - 1) {
                        side.add(new Position(row, col));
                        xSide.add(new Position(row, col));
                    }
                    //world[row][j + height - 1] = Tileset.WALL;
                } else if (col == j || col == j + height - 1) {
                    world[row][col] = fence;
                    if (row > i && row < i + width - 1) {
                        side.add(new Position(row, col));
                        ySide.add(new Position(row, col));
                    }
                    //world[i + width - 1][col] = Tileset.WALL;
                } else {
                    world[row][col] = content;
                }
            }
        }
    }
    public List<Position> roomCoordinates() {
        List<Position> result = new ArrayList<>();
        result.add(new Position(i, j)); //bottom left
        result.add(new Position(i, j + height - 1));//bottom right
        result.add(new Position(i + width - 1, j));//top left
        result.add(new Position(i+ width - 1, j + height - 1));//top right
        return result;
    }

    public boolean insideRoom(Position pos) {
        int x = pos.getX_position();
        int y = pos.getY_position();
        Position topLeft = roomCoordinates().get(2);
        Position bottomRight = roomCoordinates().get(1);
        int topLeft_x = topLeft.getX_position();
        int topLeft_y = topLeft.getY_position();
        int bottomRight_x = bottomRight.getX_position();
        int bottomRight_y = bottomRight.getY_position();
        if (x > bottomRight_x && x < topLeft_x && y > bottomRight_y && y < topLeft_y) {
            return true;
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public int getX_position() {
        return i;
    }
    public int getY_position() {
        return j;
    }
    public List<Position> getSide() {
        return side;
    }

    public  boolean noDoor(int x, int y, TETile[][] world) {
        if (x == i || x == i + width - 1) {
            for (int s = j + 1; s < j + height - 1; s++) {
                if (x < world.length && s < world[0].length && world[x][s].equals(Tileset.UNLOCKED_DOOR)) {
                    return false;
                }
            }
        }
        if (y == j || y == j + height - 1) {
            for (int s = i + 1; s < i + height - 1; s++) {
                if (s < world.length && y < world[0].length && world[s][y].equals(Tileset.UNLOCKED_DOOR)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Position> getxSide() {
        return xSide;
    }
    public List<Position> getySide() {
        return ySide;
    }
}

