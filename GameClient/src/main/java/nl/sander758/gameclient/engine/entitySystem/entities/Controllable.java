package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.input.KeyboardInputListener;
import nl.sander758.gameclient.engine.input.MouseInputListener;
import org.joml.Matrix4f;

public interface Controllable extends MouseInputListener, KeyboardInputListener {

    Matrix4f getViewMatrix();

}
