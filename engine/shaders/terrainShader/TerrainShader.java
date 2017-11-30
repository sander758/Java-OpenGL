package shaders.terrainShader;

import org.lwjgl.util.vector.Vector4f;
import scene.Camera;
import scene.Light;
import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import utils.Maths;

public class TerrainShader extends ShaderProgram {
    private static final String VERTEX_FILE = "engine/shaders/terrainShader/terrainShaderVertex.glsl";
    private static final String GEOMETRY_FILE = "engine/shaders/terrainShader/terrainShaderGeometry.glsl";
    private static final String FRAGMENT_FILE = "engine/shaders/terrainShader/terrainShaderFragment.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightDirection;
    private int location_lightColor;
    private int location_toShadowMapSpace;
    private int location_shadowMap;
    private int location_shadowDistance;
    private int location_shadowMapSize;
    private int location_clipPlane;
    private int location_doShadow;

    public TerrainShader() {
        super(VERTEX_FILE, GEOMETRY_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void initializeUniformLocations() {
        this.location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        this.location_viewMatrix = super.getUniformLocation("viewMatrix");
        this.location_lightDirection = super.getUniformLocation("lightDirection");
        this.location_lightColor = super.getUniformLocation("lightColor");
        this.location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        this.location_shadowMap = super.getUniformLocation("shadowMap");
        this.location_shadowDistance = super.getUniformLocation("shadowDistance");
        this.location_shadowMapSize = super.getUniformLocation("mapSize");
        this.location_clipPlane = super.getUniformLocation("clipPlane");
        this.location_doShadow = super.getUniformLocation("doShadow");
    }

    public void connectTextureUnits() {
        super.loadInt(location_shadowMap, 0);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_color");
        super.bindAttribute(2, "in_normal");
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix(location_projectionMatrix, projectionMatrix);
    }

    public void loadTransformationMatrix(Matrix4f transformationMatrix) {
        super.loadMatrix(location_transformationMatrix, transformationMatrix);
    }

    public void loadLight(Light light) {
        super.loadVector(location_lightDirection, light.getDirection());
        super.loadVector(location_lightColor, light.getColor());
    }

    public void loadToShadowSpaceMatrix(Matrix4f matrix) {
        super.loadMatrix(location_toShadowMapSpace, matrix);
    }

    public void loadShadowDistance(float shadowDistance) {
        super.loadFloat(location_shadowDistance, shadowDistance);
    }

    public void loadShadowMapSize(float mapSize) {
        super.loadFloat(location_shadowMapSize, mapSize);
    }

    public void loadClipPlane(Vector4f clipPlane) {
        super.loadVector(location_clipPlane, clipPlane);
    }

    public void loadDoShadow(boolean doShadow) {
        super.loadBool(location_doShadow, doShadow);
    }
}
