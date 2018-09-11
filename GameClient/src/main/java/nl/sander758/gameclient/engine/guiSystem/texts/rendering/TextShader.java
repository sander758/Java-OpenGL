package nl.sander758.gameclient.engine.guiSystem.texts.rendering;

import nl.sander758.gameclient.engine.shaders.ShaderProgram;
import nl.sander758.gameclient.engine.shaders.UniformMatrix;
import nl.sander758.gameclient.engine.shaders.UniformSampler;
import nl.sander758.gameclient.engine.shaders.UniformVec3;

public class TextShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/guiSystem/texts/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/guiSystem/texts/fragmentShader.glsl";

    public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    public UniformVec3 color = new UniformVec3("color");
    public UniformSampler fontAtlas = new UniformSampler("fontAtlas");

    public TextShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);

        initializeUniformLocations(transformationMatrix, color, fontAtlas);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "textureCoordinates");
    }

    public void connectTextureUnits() {
        fontAtlas.loadTexUnit(0);
    }
}
