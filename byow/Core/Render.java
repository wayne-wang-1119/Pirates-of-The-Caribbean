package byow.Core;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import byow.TileEngine.Tileset;
import java.awt.*;

public class Render {
    private final Engine engine;
    private int width;
    private int height;

    public Render(Engine input) {
        this.engine = input;
        width = Engine.WIDTH;
        height = Engine.HEIGHT;
    }

    public void drawScreen(TETile[][] world) {
        width = world.length;
        height = world[0].length;
        engine.ter.initialize(width, height + 8);
        engine.ter.renderFrame(world);
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void drawStart() {
        StdDraw.setCanvasSize(800, 500);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledSquare(0, 0, 1);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(0.5, 0.7, "Welcome To Treasure Hunter!");
        StdDraw.text(0.5, 0.6, "To Win, Find The Hidden Stash!");
        StdDraw.text(0.5, 0.5, "To Create A New Game: (N)");
        StdDraw.text(0.5, 0.4, "To Load Last Game: (L)");
        StdDraw.text(0.5, 0.3, "To Load Last GamePlay Step by Step: (R)");
        StdDraw.text(0.5, 0.2, "To Save And Quit: (:Q)");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void drawSeedInput(String seed) {
        StdDraw.clear();
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledSquare(0, 0, 1);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(0.5, 0.75, "Enter Seed For World:");
        StdDraw.text(0.5, 0.5, seed);
        StdDraw.text(0.5, 0.25, "Press 'S' to Create World");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void drawFinish() {
        StdDraw.setCanvasSize(800, 500);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledSquare(0, 0, 1);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(0.5, 0.5, "Congratulations!");
        StdDraw.text(0.5, 0.4, "You Have Found The Treasure!");
        StdDraw.text(0.5, 0.3, "Press Q to Quit");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void drawQuit() {
        StdDraw.setCanvasSize(800, 500);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledSquare(0, 0, 1);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(0.5, 0.3, "Come Back Soon Before Someone Else Finds The Treasure!");
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void addHUD(TETile[][] world) {
        int hudWidth = 20;
        int hudHeight = 5;
        int hudX = (int) (width - hudWidth) / 2;
        int hudY = height + 5;
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(width / 2.0, hudY - hudHeight / 2.0, hudWidth / 2.0, hudHeight / 2.0);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width / 2.0, hudY - 1, tileCheck(world));
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(5, hudY - 1, hudWidth / 2.0, hudHeight / 2.0);
        StdDraw.filledRectangle(width - 10, hudY - 1, hudWidth / 2.0, hudHeight / 2.0);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(5, hudY - 1, "To Replay, Press R");
        StdDraw.text(width - 10, hudY - 1, "To Win, Find The Pirate's Treasure!");
        StdDraw.show();
        StdDraw.show();
    }

    private String tileCheck(TETile[][] world) {
        int i = (int) Math.floor(StdDraw.mouseX());
        int j = (int) Math.floor(StdDraw.mouseY());
        if (i < width && j < height && i >= 0 && j >= 0) {
            return world[i][j].description();
        }
        return "HUD INFO";
    }
}
