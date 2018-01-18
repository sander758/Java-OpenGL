package nl.sander758.gameclient.engine.shaders;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class UniformVec3 extends Uniform {

    public UniformVec3(String name) {
        super(name);
    }

    public void loadUniform(Vector3f vector) {
        GL20.glUniform3f(super.getLocation(), vector.x, vector.y, vector.z);
    }
}
