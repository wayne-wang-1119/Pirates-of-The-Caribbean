package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;
public class Generator {
    TETile[][] map;
    List<Room> rooms = new ArrayList<>();
    private Random random;
    private int width = 80;
    private int height = 30;
    private final TETile background = Tileset.NOTHING;
    private final TETile fence_room = Tileset.TREE;
    private final TETile content_room = Tileset.SAND;
    private final TETile content_hall = Tileset.WATER;
    private final TETile fence_hall = Tileset.MOUNTAIN;
    private final TETile door = Tileset.UNLOCKED_DOOR;
    private final TETile player = Tileset.AVATAR;
    private final TETile endTile = Tileset.FLOWER;
    private final TETile lockedDoor = Tileset.TREE;
    private boolean gameStatus = false;
    public Position playerPosition;
    private Position endPosition;
    private final int num_hallways;

    public Generator(long seed, int width, int height) {
        this.width = width;
        this.height = height;
        random = new Random(seed);
        this.map = new TETile[width][height];
        spawnWorld();
        spawnRooms(map, RandomUtils.uniform(random, 8, 20));
        num_hallways = Math.max(5, random.nextInt(15));
        spawnHallways(map);
        openRooms(map);
        fixRoom(map);
        FillHallFence(map);
        spawnPlayer(map);
        closeDoor(map);
        fixRoom(map);
    }

    private void fixRoom(TETile[][] world) {
        for (Room room : rooms) {
            int row = room.getX_position();
            int col = room.getY_position();
            for (int i = row + 1; i < row + room.width - 1; i++) {
                for (int j = col + 1; j < col + room.height - 1; j++) {
                    if (!world[i][j].equals(content_room) && !world[i][j].equals(endTile) && !world[i][j].equals(player)) {
                        world[i][j] = content_room;
                    }
                }
            }
        }
    }
    private void closeDoor(TETile[][] world) {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                checkDoor(world, i, j);
            }
        }
    }

    private void checkDoor(TETile[][] world, int x, int y) {
        if (vertex(x, y)) {
            world[x][y] = fence_room;
        }
        if (world[x][y].equals(door)) {
            if (x - 1 >= 0 && world[x - 1][y].equals(door)) {
                world[x - 1][y] = lockedDoor;
            }
            if (x + 1 < world.length && world[x + 1][y].equals(door)) {
                world[x + 1][y] = lockedDoor;
            }
            if (y - 1 >= 0 && world[x][y - 1].equals(door)) {
                world[x][y - 1] = lockedDoor;
            }
            if (y + 1 < world[0].length && world[x][y + 1].equals(door)) {
                world[x][y + 1] = lockedDoor;
            }
        }
    }

    private boolean vertex(int x, int y) {
        for (Room r : rooms) {
            if (x == r.getX_position() && y == r.getY_position()) {
                return true;
            } else if (x == r.getX_position() && y == r.getY_position() + r.height - 1) {
                return  true;
            } else if (x == r.getX_position() + r.width - 1 && y == r.getY_position()) {
                return  true;
            } else if (x == r.getX_position() + r.width - 1 && y == r.getY_position() + r.height - 1) {
                return  true;
            }
        }
        return false;
    }


    private void spawnPlayer(TETile[][] world) {
        Room start = rooms.get(RandomUtils.uniform(random, 0, rooms.size()));
        int playerX = start.getX_position() + start.width / 2;
        int playerY = start.getY_position() + start.height/2;
        world[playerX][playerY] = player;
        playerPosition = new Position(playerX, playerY);
        Room end = rooms.get(RandomUtils.uniform(random, 0, rooms.size()));
        while (start.equals(end)) {
            end = rooms.get(RandomUtils.uniform(random, 0, rooms.size()));
        }
        int endX = end.getX_position() + end.width / 2;
        int endY = end.getY_position() + end.height / 2;
        endPosition = new Position(endX, endY);
        world[endX][endY] = endTile;
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }
    public Position getEndPosition() {
        return endPosition;
    }
    public boolean endGame() {
        if (playerPosition.equals(endPosition)) {
            gameStatus = true;
        }
        return gameStatus;
    }

    public void playerRight() {
        int nextX = playerPosition.getX_position() + 1;
        int nextY = playerPosition.getY_position();
        map = movePlayer(map, nextX, nextY, playerPosition.getX_position(), playerPosition.getY_position());
    }

    public void playerLeft() {
        int nextX = playerPosition.getX_position() - 1;
        int nextY = playerPosition.getY_position();
        map = movePlayer(map, nextX, nextY, playerPosition.getX_position(), playerPosition.getY_position());
    }

    public void playerUp() {
        int nextX = playerPosition.getX_position();
        int nextY = playerPosition.getY_position() + 1;
        map = movePlayer(map, nextX, nextY, playerPosition.getX_position(), playerPosition.getY_position());
    }

    public void playerDown() {
        int nextX = playerPosition.getX_position();
        int nextY = playerPosition.getY_position() - 1;
        map = movePlayer(map, nextX, nextY, playerPosition.getX_position(), playerPosition.getY_position());
    }

    private TETile[][] movePlayer(TETile[][] world, int newX, int newY, int oldX, int oldY) {
        if (newX <= 0 || newY <= 0 || newX >= world.length - 1 || newY >= world[0].length) {
            return world;
        }
        if (world[newX][newY].equals(fence_room) || world[newX][newY].equals(fence_hall)) {
            return world;
        }
        if (world[newX][newY].equals(door) && roomNextHall(world, oldX, oldY)) {
            //case1: going from hallway into a room, next step is a door
            world[newX][newY] = player;
            world[oldX][oldY] = content_hall;
            playerPosition = new Position(newX, newY);
        } else if (world[newX][newY].equals(door) && nextRoomContent(world, oldX, oldY)) {
            //case2: going from room into a hallway, next step is a door
            world[newX][newY] = player;
            world[oldX][oldY] = content_room;
            playerPosition = new Position(newX, newY);
        } else if (world[newX][newY].equals(content_hall) && roomNextHall(world, oldX, oldY) && !nextRoomContent(world, oldX, oldY)) {
            //case3: going from hallway into hallway, next step is a hallway
            world[newX][newY] = player;
            world[oldX][oldY] = content_hall;
            playerPosition = new Position(newX, newY);
        } else if (world[newX][newY].equals(content_room) && nextRoomContent(world, oldX, oldY) && !roomNextHall(world, oldX, oldY)) {
            //case4: going from room into room, next step is a room
            world[newX][newY] = player;
            world[oldX][oldY] = content_room;
            playerPosition = new Position(newX, newY);
        } else if (world[newX][newY].equals(content_hall) && nextRoomContent(world, oldX, oldY)) {
            //case5: going from door to hallway, next step is a hallway
            world[newX][newY] = player;
            world[oldX][oldY] = door;
            playerPosition = new Position(newX, newY);
        } else if (world[newX][newY].equals(content_room) && roomNextHall(world, oldX, oldY)) {
            //case6: going from door to room, next step is a room
            world[newX][newY] = player;
            world[oldX][oldY] = door;
            playerPosition = new Position(newX, newY);
        } else if (world[newX][newY].equals(endTile)) {
            world[newX][newY] = player;
            world[oldX][oldY] = content_room;
            playerPosition = new Position(newX, newY);
            gameStatus = true;
        }
        return world;
    }

    private boolean nextRoomContent (TETile[][] world, int i, int j) {
        boolean cond1 = i - 1 >= 0 && world[i - 1][j].equals(content_room);
        boolean cond2 = i + 1 < world.length && world[i + 1][j].equals(content_room);
        boolean cond3 = j - 1 >= 0 && world[i][j - 1].equals(content_room);
        boolean cond4 = j + 1 < world[0].length && world[i][j + 1].equals(content_room);
        return (cond1 || cond2 || cond3 || cond4);
    }


    private void openRooms(TETile[][] world) {
        for (Room r : rooms) {
            for (Position pos : r.getSide()) {
                int cur_x = pos.getX_position();
                int cur_y = pos.getY_position();
                if (roomNextHall(world, cur_x, cur_y) && r.noDoor(cur_x, cur_y, world)) {
                    world[cur_x][cur_y] = door;
                }
            }
        }
        for (Room r : rooms) {
            fillDoorsAndPath(world, r);
        }
    }

    private void fillDoorsAndPath(TETile[][] world, Room r) {
        int temp_x = r.getX_position();
        int temp_y = r.getY_position();
        int width = r.width;
        int height = r.height;
        boolean cond1 = r.noDoor(temp_x, temp_y + 1, world);
        boolean cond2 = r.noDoor(temp_x + width - 1, temp_y + 1, world);
        boolean cond3 = r.noDoor(temp_x + 1, temp_y, world);
        boolean cond4 = r.noDoor(temp_x + 1, temp_y + height - 1, world);
        if (cond1) {
            int doorX = temp_x;
            int doorY = RandomUtils.uniform(random, temp_y + 1, temp_y + height - 1);
            world[doorX][doorY] = door;
            straightHallways(world,doorX - 1, doorY, 1);
        }
        if (cond2) {
            int doorX = temp_x + width - 1;
            int doorY = RandomUtils.uniform(random, temp_y + 1, temp_y + height - 1);
            world[doorX][doorY] = door;
            straightHallways(world,doorX + 1, doorY, 0);
        }
        if (cond3) {
            int doorX = RandomUtils.uniform(random, temp_x + 1, temp_x + width - 1);
            int doorY = temp_y;
            straightHallways(world, doorX, doorY - 1,2);
        }
        if (cond4) {
            int doorX = RandomUtils.uniform(random, temp_x + 1, temp_x + width - 1);
            int doorY = temp_y + height - 1;
            world[doorX][doorY] = door;
            straightHallways(world,doorX, doorY + 1, 3);
        }
    }

    private void straightHallways(TETile[][] world, int x, int y, int direction) {
        boolean stop = !notValid(new Position(x, y), world);
        while (stop) {
            if (direction == 0) {
                //up
                world[x][y] = content_hall;
                x++;
                if (x > world.length - 1 || world[x][y].equals(fence_room) || world[x][y].equals(door)) {
                    stop = false;
                }
            }
            if (direction == 1) {
                //down
                world[x][y] = content_hall;
                x--;
                if (x <= 0 || world[x][y].equals(fence_room) || world[x][y].equals(door)) {
                    stop = false;
                }
            }
            if (direction == 2) {
                //left
                world[x][y] = content_hall;
                y--;
                if (y <= 0 || world[x][y].equals(fence_room) || world[x][y].equals(door)) {
                    stop = false;
                }
            }
            if (direction == 3) {
                //right
                world[x][y] = content_hall;
                y++;
                if (y > world[0].length - 1 || world[x][y].equals(fence_room) || world[x][y].equals(door)) {
                    stop = false;
                }
            }
        }
        if (x > 0 && y > 0 && x < world.length && y < world[0].length && world[x][y].equals(fence_room)) {
            world[x][y] = door;
        }
    }

    private boolean roomNextHall(TETile[][] world, int i, int j) {
        boolean cond1 = i - 1 >= 0 && world[i - 1][j].equals(content_hall);
        boolean cond2 = i + 1 < world.length && world[i + 1][j].equals(content_hall);
        boolean cond3 = j - 1 >= 0 && world[i][j - 1].equals(content_hall);
        boolean cond4 = j + 1 < world[0].length && world[i][j + 1].equals(content_hall);
        return (cond1 || cond2 || cond3 || cond4);
    }


    private void spawnHallways(TETile[][] world) {
        generateHallways(world);
    }

    private void generateHallways(TETile[][] world) {
        for (Room r : rooms) {
            if (!r.getxSide().isEmpty() && !r.getySide().isEmpty()) {
                Position horizontalDoor = r.getxSide().get(RandomUtils.uniform(random, 0, r.getxSide().size()));
                Position verticalDoor = r.getySide().get(RandomUtils.uniform(random, 0, r.getySide().size()));
                world[horizontalDoor.getX_position()][horizontalDoor.getY_position()] = door;
                world[verticalDoor.getX_position()][verticalDoor.getY_position()] = door;
                buildHallways(world, horizontalDoor);
                buildHallways(world, verticalDoor);
            }
        }
        for (int i = 0; i < num_hallways; i++) {
            int x = RandomUtils.uniform(random, 1, world.length - 1);
            int y = RandomUtils.uniform(random, 1, world[0].length - 1);
            while (!world[x][y].equals(background)) {
                x = RandomUtils.uniform(random, 1, world.length - 1);
                y = RandomUtils.uniform(random, 1, world[0].length - 1);
            }
            buildHallways(world, new Position(x, y));
        }
    }

    private void buildHallways(TETile[][] world, Position pos) {
        int hallwayWidth = 1;
        int hallwayHeight = 1;
        randomHallways(world, pos, hallwayWidth, hallwayHeight, null);
    }

    private void randomHallways(TETile[][] world, Position pos, int hallwayWidth, int hallwayHeight, Position prev) {
        while (!notValid(pos, world)) {
            int cur_x = pos.getX_position();
            int cur_y = pos.getY_position();
            List<Position> possible_next_step = new ArrayList<>();
            if (cur_x - 1 > 0 && world[cur_x - 1][cur_y].equals(background)) {
                possible_next_step.add(new Position(cur_x - 1, cur_y));
            }
            if (cur_x + 1 < world.length && world[cur_x + 1][cur_y].equals(background)) {
                possible_next_step.add(new Position(cur_x + 1, cur_y));
            }
            if (cur_y - 1 > 0 && world[cur_x][cur_y - 1].equals(background)) {
                possible_next_step.add(new Position(cur_x, cur_y - 1));
            }
            if (cur_y + 1 < world[0].length && world[cur_x][cur_y + 1].equals(background)) {
                possible_next_step.add(new Position(cur_x, cur_y + 1));
            }
            if (possible_next_step.isEmpty()) {
                return;
            }
            int direction = RandomUtils.uniform(random, 0, possible_next_step.size());
            Hallway newHallWay = new Hallway(random, possible_next_step.get(direction), hallwayWidth, hallwayHeight);
            newHallWay.createHallway(world);
            addFence(cur_x, cur_y, world, hallwayWidth, hallwayHeight, possible_next_step.get(direction), prev);
            prev = new Position(cur_x, cur_y);
            pos = possible_next_step.get(direction);
        }
    }

    private void addFence(int x, int y, TETile[][] world, int hallwayWidth, int hallwayHeight, Position next, Position prev) {
        int direction = hallwayAlign(x, y, next, prev);
        if (direction == 0) {
            checkThenAdd(x - 1, y, world);
            //world[x - 1][y] = fence_hall;
            checkThenAdd(x + hallwayHeight, y, world);
            //world[x + hallwayHeight][y] = fence_hall;
        } else if (direction == 1) {
            checkThenAdd(x, y - 1, world);
            //world[x][y - 1] = fence_hall;
            checkThenAdd(x, y + hallwayHeight, world);
            //world[x][y + hallwayHeight] = fence_hall;
        } else if (direction == 2 || direction == 9) {//go right then up or down then left
            checkThenAdd(x - 1, y, world);
            //world[x - 1][y] = fence_hall;//add below
            checkThenAdd(x - 1, y + hallwayHeight, world);
            //world[x - 1][y + hallwayHeight] = fence_hall;//add triangle
            checkThenAdd(x, y + hallwayHeight, world);
            //world[x][y + hallwayHeight] = fence_hall;// add right
        } else if (direction == 3 || direction == 7) {//go right then down or up then left
            checkThenAdd(x + hallwayHeight, y, world);
            //world[x + hallwayHeight][y] = fence_hall; //add above
            checkThenAdd(x + hallwayHeight, y + hallwayHeight, world);
            //world[x + hallwayHeight][y + hallwayHeight] = fence_hall; //add triangle
            checkThenAdd(x, y + hallwayHeight, world);
            //world[x][y + hallwayHeight] = fence_hall; // add right
        } else if (direction == 4 || direction == 8) {//go left then up or down then right
            checkThenAdd(x - 1, y, world);
            //world[x - 1][y] = fence_hall;//add below
            checkThenAdd(x - 1, y - 1, world);
            //world[x - 1][y - 1] = fence_hall;//add triangle
            checkThenAdd(x, y - 1, world);
            //world[x][y - 1] = fence_hall;// add left
        } else if (direction == 5 || direction == 6) {//go left then down or up then right
            checkThenAdd(x + hallwayHeight, y, world);
            //world[x + hallwayHeight][y] = fence_hall;//add above
            checkThenAdd(x + hallwayHeight, y - 1, world);
            //world[x + hallwayHeight][y - 1] = fence_hall;//add triangle
            checkThenAdd(x, y - 1, world);
            //world[x][y - 1] = fence_hall;// add left
        }

    }
    private void checkThenAdd(int i, int j, TETile[][] world) {
        if (world[i][j].equals(background)) {
            world[i][j] = fence_hall;
        }
    }
    private void FillHallFence(TETile[][] world) {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j].equals(background) && nextToHall(world, i, j)) {
                    world[i][j] = fence_hall;
                }
            }
        }
    }
    private boolean nextToHall(TETile[][] world, int i, int j) {
        boolean cond1 = i - 1 >= 0 && world[i - 1][j].equals(content_hall);
        boolean cond2 = i + 1 < world.length && world[i + 1][j].equals(content_hall);
        boolean cond3 = j - 1 >= 0 && world[i][j - 1].equals(content_hall);
        boolean cond4 = j + 1 < world[0].length && world[i][j + 1].equals(content_hall);
        boolean cond5 = i - 1 >= 0 && j - 1 >= 0 && world[i - 1][j - 1].equals(content_hall);
        boolean cond6= i - 1 >= 0 && j + 1 < world[0].length && world[i - 1][j + 1].equals(content_hall);
        boolean cond7= i + 1 < world.length && j - 1 >= 0 && world[i + 1][j - 1].equals(content_hall);
        boolean cond8= i + 1 < world.length && j + 1 < world[0].length && world[i + 1][j + 1].equals(content_hall);
        return (cond1 || cond2 || cond3 || cond4 || cond5 || cond6 || cond7 || cond8);
    }

    public int hallwayAlign(int x, int y, Position next, Position prev) {
        if (prev == null) {
            if (x == next.getX_position()) {
                return 0;
            } else if (y == next.getY_position()) {
                return 1;
            }
        }
        int prev_x = prev.getX_position();
        int prev_y = prev.getY_position();
        int next_x = next.getX_position();
        int next_y = next.getY_position();
        if (prev_x == x && x == next_x) { //horizontal align
            return 0;
        } else if (prev_y == y && y == next_y) { //vertical align
            return 1;
        } else if (prev_x == x && x < next_x && prev_y < y && y == next_y) {//go right then up
            return 2;
        } else if (prev_x == x && x > next_x && prev_y < y && y == next_y) {//go right then down
            return 3;
        } else if (prev_x == x && x < next_x && prev_y > y && y == next_y) {//go left then up
            return 4;
        } else if (prev_x == x && x > next_x && prev_y > y && y == next_y) {//go left then down
            return 5;
        } else if (prev_x < x && x == next_x && prev_y == y && y < next_y) {//go up then right
            return 6;
        } else if (prev_x < x && x == next_x && prev_y == y && y > next_y) {//go up then left
            return 7;
        } else if (prev_x > x && x == next_x && prev_y == y && y < next_y) {//go down then right
            return 8;
        }
        return 9;//go down then left
    }

    private boolean notValid(Position pos, TETile[][] world) {
        int x = pos.getX_position();
        int y = pos.getY_position();
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        if (x - 1 < 0 || x + 1 > worldWidth - 1 || y - 1 < 0 || y + 1 > worldHeight - 1) {
            return true;
        }
        if (world[x - 1][y].equals(fence_room) && world[x + 1][y].equals(fence_room) && world[x][y - 1].equals(fence_room) && world[x][y + 1].equals(fence_room)){
            return true;
        }
        return false;
    }


    private void spawnWorld() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = background;
            }
        }
    }

    private void spawnRooms(TETile[][] world, int roomLimit) {
        for (int i = 0; i < roomLimit; i++) {
            Room room = generateRoom();
            room.createRoom(world);
            rooms.add(room);
        }
    }

    private Room generateRoom() {
        //Room room = new Room(pos);
        Room room = buildRoom(random);
        while (overlapRoom(room, map)) {
            room = buildRoom(random);
        }
        return room;
    }

    private Room buildRoom(Random random) {
//        int x = RandomUtils.uniform(random, width);
        int x = random.nextInt(width);
//        int y = RandomUtils.uniform(random, height);
        int y = random.nextInt(height);
//        int room_width = RandomUtils.uniform(random, 5, 8);
//        int room_height = RandomUtils.uniform(random, 5, 8);
        int room_width = Math.max(5, random.nextInt(15));
        int room_height = Math.max(5, random.nextInt(15));
        Position pos = new Position(x, y);
        return new Room(random, pos, room_width, room_height);
    }

    private boolean overlapRoom(Room c, TETile[][] world) {
        int i = c.getX_position();
        int j = c.getY_position();
        int width = c.getWidth();
        int height = c.getHeight();
        if (i + width >= world.length - 1 || j + height >= world[0].length - 1 || i - 1 <= 0 || j - 1 <= 0) {
            return true;
        }
        for (int row = i - 1; row < i + width; row++) {
            if (world[row][j - 1].equals(fence_room) || world[row][j + height].equals(fence_room)) {
                return true;
            }
        }
        for (int col = j - 1; col < j + height; col++) {
            if(world[i - 1][col].equals(fence_room) || world[i + width][col].equals(fence_room)) {
                return true;
            }
        }
        return false;
    }

    public TETile[][] getTiles() {
        return map;
    }
}