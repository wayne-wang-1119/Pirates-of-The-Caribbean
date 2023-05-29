package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.InputDemo.KeyboardInputSource;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private Render render = new Render(this);
    protected long seed;
    private String userInput = "";
    private boolean worldCreated = false;
    private TETile[][] finalWorldFrame;
    private boolean endGame = false;
    private Generator generator;
    private TETile[][] copyWorld;
    private Position startPosition;
    private static final int SECONDS = 500;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource inputSource = new KeyboardInputSource();
        String readSeed = "";
        render.drawStart();
        while (inputSource.possibleNextInput()) {
            char nextInput = inputSource.getNextKey();
            if (!worldCreated) {
                if (nextInput == 'N') {
                    File file = new File("byow/Core/saved.txt");
                    if (file.exists()) {
                        file.delete();
                    }
                    render.drawSeedInput(readSeed);
                    nextInput = inputSource.getNextKey();
                    while (nextInput != 'S') {
                        if (nextInput != 'N') {
                            readSeed += nextInput;
                            render.drawSeedInput(readSeed);
                            nextInput = inputSource.getNextKey();
                        }
                    }
                    seed = Long.parseLong(readSeed);
                    userInput = seed + ":";
                    generator = new Generator(seed, WIDTH, HEIGHT);
                    finalWorldFrame = generator.getTiles();
                    copyWorld = TETile.copyOf(finalWorldFrame);
                    startPosition = generator.getPlayerPosition();
                    render.drawScreen(finalWorldFrame);
                    worldCreated = true;
                } else if (nextInput == 'L') {
                    ter.initialize(WIDTH, HEIGHT + 8);
                    load(false);
                    ter.renderFrame(finalWorldFrame);
                    worldCreated = true;
                } else if (nextInput == 'Q') {
                    quitNotFinal();
                } else if (nextInput == 'R') {
                    ter.initialize(WIDTH, HEIGHT + 8);
                    load(true);
                }
            } else {
                userInput += nextInput;
                if (!endGame && nextInput != 'R') {
                    playerMovement(finalWorldFrame, generator, nextInput);
                    ter.renderFrame(finalWorldFrame);
                    render.addHUD(finalWorldFrame);
                    endGame = generator.endGame();
                }
                if (!endGame && nextInput == 'R') {
                    userInput = userInput.substring(0, userInput.length() - 1);
                    File file = new File("byow/Core/saved.txt");
                    save(userInput);
                    userInput = "";
                    replay();
                }
                if (nextInput == ':') {
                    if (inputSource.getNextKey() == 'Q') {
                        userInput = userInput.substring(0, userInput.length() - 1);
                        save(userInput);
                        quitNotFinal();
                    } else {
                        userInput = userInput.substring(0, userInput.length() - 1);
                    }
                }
                if (endGame) {
                    save(userInput);
                    userInput = "";
                    render.drawFinish();
                    if (nextInput == 'Q') {
                        quit();
                    }
                }
                while (!endGame && !StdDraw.hasNextKeyTyped()) {
                    render.addHUD(finalWorldFrame);
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        InputSource inputSource = new StringInputDevice(input);
        String readSeed = "";
        while (inputSource.possibleNextInput()) {
            char nextInput = inputSource.getNextKey();
            if (!worldCreated) {
                if (nextInput == 'N' || nextInput == 'n') {
                    File file = new File("byow/Core/saved.txt");
                    if (file.exists()) {
                        file.delete();
                    }
                    nextInput = inputSource.getNextKey();
                    while (nextInput != 'S' && nextInput != 's') {
                        readSeed += nextInput;
                        nextInput = inputSource.getNextKey();
                    }
                    seed = Long.parseLong(readSeed);
                    userInput = seed + ":";
                    generator = new Generator(seed, WIDTH, HEIGHT);
                    finalWorldFrame = generator.getTiles();
                    copyWorld = TETile.copyOf(finalWorldFrame);
                    startPosition = generator.getPlayerPosition();
                    worldCreated = true;
                } else if (nextInput == 'L' || nextInput == 'l') {
                    load(false);
                    worldCreated = true;
                }
            } else {
                userInput += nextInput;
                if (!endGame && nextInput != ':') {
                    //move player
                    playerMovement(finalWorldFrame, generator, nextInput);
                }
                if (nextInput == ':') {
                    //quit game and save
                    char q = inputSource.getNextKey();
                    if (q == 'Q' || q == 'q') {
                        userInput = userInput.substring(0, userInput.length() - 1);
                        save(userInput);
                    } else {
                        userInput = userInput.substring(0, userInput.length() - 1);
                    }
                }
            }
        }
        return finalWorldFrame;
    }

    private void playerMovement(TETile[][] world, Generator g, char direction) {
        direction = Character.toUpperCase(direction);
        if (direction == 'W') {
            g.playerUp();
            world = g.getTiles();
        } else if (direction == 'S') {
            g.playerDown();
            world = g.getTiles();
        } else if (direction == 'A') {
            g.playerLeft();
            world = g.getTiles();
        } else if (direction == 'D') {
            g.playerRight();
            world = g.getTiles();
        }
        finalWorldFrame = world;
    }

    private void quit() {
        System.exit(0);
    }
    private void quitNotFinal() {
        render.drawQuit();
        StdDraw.pause(SECONDS);
        System.exit(0);
    }
    private void save(String inp) {
        try {
            FileWriter writer = new FileWriter("byow/Core/saved.txt", true);
            writer.write(inp);
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void load(boolean replayLastPlay) {
        try {
            Scanner scan = new Scanner(new File("byow/Core/saved.txt"));
            scan.useDelimiter(":");
            String s = "";
            String player = "";
            while (scan.hasNext()) {
                s = scan.next();
                player = scan.next();
            }
            String input = 'N' + s + 'S' + player;
            reloadString(input, replayLastPlay);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void reloadString(String input, boolean replayLastPlay) {
        InputSource inputSource = new StringInputDevice(input);
        String readSeed = "";
        while (inputSource.possibleNextInput()) {
            char nextInput = inputSource.getNextKey();
            if (!worldCreated && nextInput == 'N') {
                nextInput = inputSource.getNextKey();
                while (nextInput != 'S') {
                    if (nextInput != 'N') {
                        readSeed += nextInput;
                        nextInput = inputSource.getNextKey();
                    }
                }
                seed = Long.parseLong(readSeed);
                userInput = "";
                generator = new Generator(seed, WIDTH, HEIGHT);
                finalWorldFrame = generator.getTiles();
                copyWorld = TETile.copyOf(finalWorldFrame);
                startPosition = generator.getPlayerPosition();
                worldCreated = true;
                nextInput = inputSource.getNextKey();
            }
            if (replayLastPlay) {
                ter.renderFrame(finalWorldFrame);
                StdDraw.pause(SECONDS);
            }
            playerMovement(finalWorldFrame, generator, nextInput);
        }
        if (replayLastPlay) {
            ter.renderFrame(finalWorldFrame);
        }
    }

    private void replay() {
        finalWorldFrame = TETile.copyOf(copyWorld);
        generator.map = finalWorldFrame;
        generator.playerPosition = startPosition;
        ter.renderFrame(finalWorldFrame);
        try {
            Scanner scan = new Scanner(new File("byow/Core/saved.txt"));
            scan.useDelimiter(":");
            String player = "";
            while (scan.hasNext()) {
                player = scan.next();
            }
            String playerMove = player;
            for (int i = 0; i < playerMove.length(); i++) {
                char nextMove = playerMove.charAt(i);
                playerMovement(finalWorldFrame, generator, nextMove);
                StdDraw.pause(SECONDS);
                ter.renderFrame(finalWorldFrame);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
