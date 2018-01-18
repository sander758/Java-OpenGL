package nl.sander758.gameclient.engine.shaders;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

public class UniformVec4 extends Uniform {

    public UniformVec4(String name) {
        super(name);
    }

    public void loadUniform(Vector4f vector) {
        GL20.glUniform4f(super.getLocation(), vector.x, vector.y, vector.z, vector.w);
    }
}
