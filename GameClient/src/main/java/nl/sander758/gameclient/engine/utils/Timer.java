package nl.sander758.gameclient.engine.utils;

public class Timer {
    private static float deltaTime;
    private static long lastFrameTime;

    /**
     * Gets the current delta time.
     * @return the delta time.
     */
    public static float getDeltaTime() {
        return deltaTime;
    }

    /**
     * Sets the initial frame time.
     */
    public static void start() {
        lastFrameTime = getCurrentTime();
    }

    /**
     * Update the delta time.
     */
    public static void update() {
        long currentTime = getCurrentTime();
        deltaTime = (currentTime - lastFrameTime) / 1000000f;
        lastFrameTime = currentTime;
    }

    /**
     * @return time in milliseconds
     */
    private static long getCurrentTime() {
        return System.nanoTime() / 1000;
    }
}
