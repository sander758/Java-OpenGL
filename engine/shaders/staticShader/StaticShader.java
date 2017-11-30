package shaders.staticShader;

import org.lwjgl.util.vector.Vector4f;
import scene.Camera;
import scene.Light;
import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import utils.Maths;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "engine/shaders/staticShader/staticShaderVertex.glsl";
    private static final String GEOMETRY_FILE = "engine/shaders/staticShader/staticShaderGeometry.glsl";
    private static final String FRAGMENT_FILE = "engine/shaders/staticShader/staticShaderFragment.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightDirection;
    private int location_lightColor;
    private int location_clipPlane;

    public StaticShader() {
        super(VERTEX_FILE, GEOMETRY_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void initializeUniformLocations() {
        this.location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        this.location_viewMatrix = super.getUniformLocation("viewMatrix");
        this.location_lightDirection = super.getUniformLocation("lightDirection");
        this.location_lightColor = super.getUniformLocation("lightColor");
        this.location_clipPlane = super.getUniformLocation("clipPlane");
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

    public void loadClipPlane(Vector4f clipPlane) {
        super.loadVector(location_clipPlane, clipPlane);
    }
}
