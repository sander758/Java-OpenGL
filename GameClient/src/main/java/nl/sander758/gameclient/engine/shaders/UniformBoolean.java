package nl.sander758.gameclient.engine.shaders;

import org.lwjgl.opengl.GL20;

public class UniformBoolean extends Uniform {

    public UniformBoolean(String name) {
        super(name);
    }

    public void loadUniform(boolean value) {
        GL20.glUniform1i(super.getLocation(), value ? 1 : 0);
    }
}
