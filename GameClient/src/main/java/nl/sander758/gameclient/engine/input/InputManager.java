package nl.sander758.gameclient.engine.input;

import nl.sander758.gameclient.engine.display.WindowManager;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class InputManager {

    private static List<MouseInputListener> mouseInputListeners = new ArrayList<>();
    private static List<KeyboardInputListener> keyboardInputListeners = new ArrayList<>();

    public static void register() {
        glfwSetInputMode(WindowManager.getWindow(), GLFW_STICKY_KEYS, GLFW_TRUE);

        // Setup a key keyboardInputCallback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(WindowManager.getWindow(), InputManager::invokeKeyCallback);

        glfwSetCursorPosCallback(WindowManager.getWindow(), InputManager::invokeCursorPosCallback);
    }

    public static void registerMouseInputListener(MouseInputListener listener) {
        mouseInputListeners.add(listener);
    }

    public static void registerKeyboardInputListener(KeyboardInputListener listener) {
        keyboardInputListeners.add(listener);
    }

    public static int getKeyState(int key) {
        return glfwGetKey(WindowManager.getWindow(), key);
    }

    public static int getMouseButtonState(int button) {
        return glfwGetMouseButton(WindowManager.getWindow(), button);
    }

    private static void invokeKeyCallback(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }
        for (KeyboardInputListener listener : keyboardInputListeners) {
            listener.keyboardInputCallback(key, action);
        }
    }

    private static double lastX = 0.0d;
    private static double lastY = 0.0d;
    private static boolean firstMouse = true;

    private static void invokeCursorPosCallback(long window, double xpos, double ypos) {
        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }

        double xoffset = xpos - lastX;
        double yoffset = lastY - ypos;
        lastX = xpos;
        lastY = ypos;

        for (MouseInputListener listener : mouseInputListeners) {
            listener.mouseInputCallback(xoffset, yoffset);
        }
    }
}
