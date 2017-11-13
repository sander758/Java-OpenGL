package shaders.guiShader;

import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;

public class GuiShader extends ShaderProgram {

    private static final String VERTEX_FILE = "engine/shaders/guiShader/guiShaderVertex.glsl";
    private static final String FRAGMENT_FILE = "engine/shaders/guiShader/guiShaderFragment.glsl";

    private int location_transformationMatrix;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void initializeUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadTransformationMatrix(Matrix4f transformationMatrix) {
        super.loadMatrix(location_transformationMatrix, transformationMatrix);
    }
}
