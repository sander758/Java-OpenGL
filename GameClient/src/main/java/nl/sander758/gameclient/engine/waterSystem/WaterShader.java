package nl.sander758.gameclient.engine.waterSystem;

import nl.sander758.gameclient.engine.shaders.*;

public class WaterShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/waterSystem/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/waterSystem/fragmentShader.glsl";

    public UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    public UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");

    public UniformFloat height = new UniformFloat("height");
    public UniformVec3 cameraPos = new UniformVec3("cameraPos");
    public UniformVec2 nearFarPlanes = new UniformVec2("nearFarPlanes");
    public UniformFloat waveTime = new UniformFloat("waveTime");

    public UniformVec3 lightDirection = new UniformVec3("lightDirection");
    public UniformVec3 lightColor = new UniformVec3("lightColor");
    public UniformVec2 lightBias = new UniformVec2("lightBias");

    public UniformSampler reflectionTexture = new UniformSampler("reflectionTexture");
    public UniformSampler refractionTexture = new UniformSampler("refractionTexture");
    public UniformSampler depthTexture = new UniformSampler("depthTexture");


    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);

        super.initializeUniformLocations(
                projectionMatrix,
                viewMatrix,
                transformationMatrix,
                height,
                cameraPos,
                nearFarPlanes,
                waveTime,
                lightDirection,
                lightColor,
                lightBias,

                reflectionTexture,
                refractionTexture,
                depthTexture
        );
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_indicators");
    }

    public void connectTextureUnits() {
        reflectionTexture.loadTexUnit(0);
        refractionTexture.loadTexUnit(1);
        depthTexture.loadTexUnit(2);
    }
}
