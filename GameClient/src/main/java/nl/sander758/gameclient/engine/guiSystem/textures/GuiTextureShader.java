package nl.sander758.gameclient.engine.guiSystem.textures;

import nl.sander758.gameclient.engine.shaders.ShaderProgram;
import nl.sander758.gameclient.engine.shaders.UniformMatrix;
import nl.sander758.gameclient.engine.shaders.UniformSampler;

public class GuiTextureShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/guiSystem/textures/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/guiSystem/textures/fragmentShader.glsl";

    public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    public UniformSampler guiTexture = new UniformSampler("guiTexture");

    public GuiTextureShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);

        super.initializeUniformLocations(
                transformationMatrix,
                guiTexture
        );
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
    }

    public void connectTextureUnits() {
        guiTexture.loadTexUnit(0);
    }
}
