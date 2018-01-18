package nl.sander758.gameclient.engine.entitySystem;

import nl.sander758.gameclient.engine.shaders.ShaderProgram;
import nl.sander758.gameclient.engine.shaders.UniformMatrix;
import nl.sander758.gameclient.engine.shaders.UniformVec3;
import nl.sander758.gameclient.engine.shaders.UniformVec4;

public class EntityShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/entitySystem/vertexShader.glsl";
    private static final String GEOMETRY_FILE = "shaders/entitySystem/geometryShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/entitySystem/fragmentShader.glsl";

    public UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    public UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");

    public UniformVec3 lightDirection = new UniformVec3("lightDirection");
    public UniformVec3 lightColor = new UniformVec3("lightColor");

    public UniformVec4 clipPlane = new UniformVec4("clipPlane");

    public EntityShader() {
        super(VERTEX_FILE, GEOMETRY_FILE, FRAGMENT_FILE);

        super.initializeUniformLocations(
                projectionMatrix,
                viewMatrix,
                transformationMatrix,
                lightDirection,
                lightColor,
                clipPlane
        );
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_normal");
        super.bindAttribute(2, "in_color");
    }
}
