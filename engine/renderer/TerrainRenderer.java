package renderer;

import entities.Camera;
import entities.Light;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import shaders.staticShader.StaticShader;
import terrains.Terrain;
import utils.Maths;

import java.util.List;

public class TerrainRenderer {

    private StaticShader staticShader;

    public TerrainRenderer(StaticShader staticShader) {
        this.staticShader = staticShader;
    }

    public void render(List<Terrain> terrains, Camera camera, Light light, Matrix4f toShadowSpace) {
        staticShader.start();
        staticShader.loadViewMatrix(camera);
        staticShader.loadLight(light);
        staticShader.loadToShadowSpaceMatrix(toShadowSpace);

        for (Terrain terrain : terrains) {
            GL30.glBindVertexArray(terrain.getEntity().getModel().getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);

            Matrix4f transformationMatrix = Maths.createTransformationMatrix(terrain.getEntity().getPosition(), 0, 0, 0, 1);
            staticShader.loadTransformationMatrix(transformationMatrix);

            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getEntity().getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
        }

        GL30.glBindVertexArray(0);

        staticShader.stop();
    }
}
