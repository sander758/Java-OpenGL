package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.packetsOut.PingPacketOut;
import nl.sander758.gameclient.engine.input.KeyboardInputListener;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class PingManager implements KeyboardInputListener {

    private static PingManager manager = new PingManager();

    private Random random = new Random();

    private int expectedCallbackData;
    private long startTime;

    public static PingManager getManager() {
        return manager;
    }

    @Override
    public void keyboardInputCallback(int key, int action) {
        if (key != GLFW.GLFW_KEY_P || action != GLFW.GLFW_PRESS) {
            return;
        }

        expectedCallbackData = random.nextInt();
        Logger.debug("sending ping packet with data: " + expectedCallbackData);

        SocketClient.getClient().trySend(new PingPacketOut(expectedCallbackData));
        startTime = System.currentTimeMillis();
    }

    public void callback(int data) {
        Logger.debug("Received pong with data: " + data);
        if (data != expectedCallbackData) {
            Logger.error("Unexpected ping data. Expected: " + expectedCallbackData + " but received: " + data);
            return;
        }

        long duration = System.currentTimeMillis() - startTime;
        Logger.info("Ping: " + duration);
    }
}
