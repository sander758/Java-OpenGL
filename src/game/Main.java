package game;

import org.lwjgl.opengl.GL11;
import renderer.Engine;
import org.lwjgl.opengl.Display;

public class Main {

    public static void main(String[] args) {
        Engine engine = new Engine();

//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        while (!Display.isCloseRequested()) {
            engine.update();
        }

        engine.cleanUp();
    }
}
