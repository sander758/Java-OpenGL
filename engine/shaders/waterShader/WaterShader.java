package shaders.waterShader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import scene.Camera;
import scene.Light;
import shaders.ShaderProgram;
import utils.Maths;

public class WaterShader extends ShaderProgram {

    private static final String VERTEX_FILE = "engine/shaders/waterShader/waterShaderVertex.glsl";
    private static final String FRAGMENT_FILE = "engine/shaders/waterShader/waterShaderFragment.glsl";

    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_transformationMatrix;
    private int location_height;
    private int location_cameraPos;
    private int location_nearFarPlanes;
    private int location_waveTime;
    private int location_lightDirection;
    private int location_lightColor;
    private int location_lightBias;

    private int location_reflectionTexture;
    private int location_refractionTexture;
    private int location_depthTexture;

    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void initializeUniformLocations() {
        this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        this.location_viewMatrix = super.getUniformLocation("viewMatrix");
        this.location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        this.location_height = super.getUniformLocation("height");
        this.location_cameraPos = super.getUniformLocation("cameraPos");
        this.location_nearFarPlanes = super.getUniformLocation("nearFarPlanes");
        this.location_waveTime = super.getUniformLocation("waveTime");
        this.location_lightDirection = super.getUniformLocation("lightDirection");
        this.location_lightColor = super.getUniformLocation("lightColor");
        this.location_lightBias = super.getUniformLocation("lightBias");

        this.location_reflectionTexture = super.getUniformLocation("reflectionTexture");
        this.location_refractionTexture = super.getUniformLocation("refractionTexture");
        this.location_depthTexture = super.getUniformLocation("depthTexture");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_indicators");
    }

    public void connectTextureUnits() {
        super.loadInt(location_reflectionTexture, 0);
        super.loadInt(location_refractionTexture, 1);
        super.loadInt(location_depthTexture, 2);
    }

    public void loadProjectionMatrix(Matrix4f matrix4f) {
        super.loadMatrix(location_projectionMatrix, matrix4f);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadVector(location_cameraPos, camera.getPosition());
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadTransformationMatrix(Matrix4f matrix4f) {
        super.loadMatrix(location_transformationMatrix, matrix4f);
    }

    public void loadHeight(float height) {
        super.loadFloat(location_height, height);
    }

    public void loadNearFarPlanes(Vector2f nearFarPlanes) {
        super.loadVector(location_nearFarPlanes, nearFarPlanes);
    }

    public void loadWaveTime(float waveTime) {
        super.loadFloat(location_waveTime, waveTime);
    }

    public void loadLight(Light light) {
        super.loadVector(location_lightDirection, light.getDirection());
        super.loadVector(location_lightColor, light.getColor());
        super.loadVector(location_lightBias, light.getBias());
    }
}
