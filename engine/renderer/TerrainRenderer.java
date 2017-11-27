package renderer;

import org.lwjgl.util.vector.Vector3f;
import scene.Camera;
import scene.Light;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import shaders.terrainShader.TerrainShader;
import entities.Terrain;
import utils.Maths;

import java.util.List;

public class TerrainRenderer {

    private TerrainShader terrainShader;

    public TerrainRenderer(float shadowDistance, float shadowMapSize) {
        terrainShader = new TerrainShader();

        Matrix4f projectionMatrix = Maths.createProjectionMatrix();

        terrainShader.start();
        terrainShader.loadProjectionMatrix(projectionMatrix);
        terrainShader.loadShadowDistance(shadowDistance);
        terrainShader.loadShadowMapSize(shadowMapSize);
        terrainShader.connectTextureUnits();
        terrainShader.stop();
    }

    public void render(List<Terrain> terrains, Camera camera, Light light, Matrix4f toShadowSpace) {
        terrainShader.start();
        terrainShader.loadViewMatrix(camera);
        terrainShader.loadLight(light);
        terrainShader.loadToShadowSpaceMatrix(toShadowSpace);

        for (Terrain terrain : terrains) {
            GL30.glBindVertexArray(terrain.getModel().getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);

            Matrix4f transformationMatrix = Maths.createTransformationMatrix(terrain.getPosition(), terrain.getRotation().x, terrain.getRotation().y, terrain.getRotation().z, 1);
//            Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(0, 0, 0), 0, 0, 0, 1);
            terrainShader.loadTransformationMatrix(transformationMatrix);

            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
        }

        GL30.glBindVertexArray(0);

        terrainShader.stop();
    }
}
