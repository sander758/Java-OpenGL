package nl.sander758.gameclient.engine.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class UniformSampler extends Uniform {

    private int currentValue;
    private boolean used = false;

    public UniformSampler(String name) {
        super(name);
    }

    public void loadTexUnit(int texUnit) {
        if (!used || currentValue != texUnit) {
            GL20.glUniform1i(super.getLocation(), texUnit);
            used = true;
            currentValue = texUnit;
        }
    }

    public void bindTexture(int textureId) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + currentValue);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }
}
