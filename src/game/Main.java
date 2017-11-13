package game;

import renderer.Engine;
import org.lwjgl.opengl.Display;

public class Main {

    public static void main(String[] args) {
        Engine engine = new Engine();

        while (!Display.isCloseRequested()) {
            engine.update();
        }

        engine.cleanUp();
    }
}
