package nl.sander758.gameclient.engine.shaders;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class UniformVec2 extends Uniform {

    public UniformVec2(String name) {
        super(name);
    }

    public void loadUniform(Vector2f vector) {
        GL20.glUniform2f(super.getLocation(), vector.x, vector.y);
    }
}
