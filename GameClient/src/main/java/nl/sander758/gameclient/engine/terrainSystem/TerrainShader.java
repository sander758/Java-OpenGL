package nl.sander758.gameclient.engine.terrainSystem;

import nl.sander758.gameclient.engine.shaders.*;

public class TerrainShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/terrainSystem/vertexShader.glsl";
    private static final String GEOMETRY_FILE = "shaders/terrainSystem/geometryShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/terrainSystem/fragmentShader.glsl";

    public UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    public UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");

    public UniformVec3 lightDirection = new UniformVec3("lightDirection");
    public UniformVec3 lightColor = new UniformVec3("lightColor");

    public UniformVec4 clipPlane = new UniformVec4("clipPlane");

    public UniformFloat shadowDistance = new UniformFloat("shadowDistance");
    public UniformFloat shadowMapSize = new UniformFloat("shadowMapSize");
    public UniformMatrix toShadowMapSpaceMatrix = new UniformMatrix("toShadowMapSpace");
    public UniformSampler shadowMap = new UniformSampler("shadowMap");
    public UniformBoolean doShadow = new UniformBoolean("doShadow");

    public TerrainShader() {
        super(VERTEX_FILE, GEOMETRY_FILE, FRAGMENT_FILE);

        super.initializeUniformLocations(
                projectionMatrix,
                viewMatrix,
                transformationMatrix,
                lightDirection,
                lightColor,
                clipPlane,
                shadowDistance,
                shadowMapSize,
                toShadowMapSpaceMatrix,
                shadowMap,
                doShadow
        );
    }


    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_normal");
        super.bindAttribute(2, "in_color");
    }

    public void connectTextureUnits() {
        shadowMap.loadTexUnit(0);
    }
}
