package nl.sander758.gameclient.engine.shaders;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform {

    public UniformFloat(String name) {
        super(name);
    }

    public void loadUniform(float value) {
        GL20.glUniform1f(super.getLocation(), value);
    }
}
