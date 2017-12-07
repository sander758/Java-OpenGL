package utils;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
import renderer.Engine;

public class DisplayManager {

    public static final float WIDTH = 1600;
    public static final float HEIGHT = 900;

    private static final String TITLE = "My game engine";
    private static final int FPS_CAP = 1000;

    private static long lastFrameTime;
    private static float delta;

    public static void init() {
        try {
            Display.setDisplayMode(new DisplayMode((int) WIDTH, (int) HEIGHT));
            ContextAttribs attributes = new ContextAttribs(3, 2).withProfileCore(true).withForwardCompatible(true);
            Display.create(new PixelFormat().withSamples(4).withDepthBits(24), attributes);
            Display.setTitle(TITLE);
            Display.setInitialBackground(Engine.skyColor.x, Engine.skyColor.y, Engine.skyColor.z);
            GL11.glEnable(GL13.GL_MULTISAMPLE);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.err.println("Couldn't create display!");
            System.exit(-1);
        }
        GL11.glViewport(0, 0, (int) WIDTH, (int) HEIGHT);
        lastFrameTime = getCurrentTime();
    }

    public static void update() {
        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getFrameTime() {
        return delta;
    }

    public static void closeDisplay() {
        Display.destroy();
    }

    private static long getCurrentTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }

}
