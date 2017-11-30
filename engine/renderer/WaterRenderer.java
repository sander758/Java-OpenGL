package renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import scene.Camera;
import scene.Light;
import shaders.waterShader.WaterShader;
import utils.Maths;
import utils.OpenGlUtils;
import water.WaterTile;

import java.util.List;

public class WaterRenderer {

    private WaterShader waterShader;

    private float time = 0;

    public WaterRenderer(Matrix4f projectionMatrix) {
        waterShader = new WaterShader();

        waterShader.start();
        waterShader.loadProjectionMatrix(projectionMatrix);
        waterShader.loadHeight(WaterTile.WATER_HEIGHT);
        waterShader.connectTextureUnits();
        waterShader.loadNearFarPlanes(new Vector2f(Maths.NEAR_PLANE, Maths.FAR_PLANE));
        waterShader.stop();
    }

    public void render(List<WaterTile> waterTiles, Camera camera, Light light) {

//        OpenGlUtils.goWireframe(true);
        OpenGlUtils.enableAlphaBlending();
        OpenGlUtils.cullBackFaces(false);

        waterShader.start();
        waterShader.loadViewMatrix(camera);
        waterShader.loadLight(light);
        updateTime();

        for (WaterTile waterTile : waterTiles) {
            GL30.glBindVertexArray(waterTile.getModel().getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);

            Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(waterTile.getPosition().x, WaterTile.WATER_HEIGHT, waterTile.getPosition().y), 0, 0, 0, 1);
            waterShader.loadTransformationMatrix(transformationMatrix);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, waterTile.getModel().getVertexCount());

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
        }

        GL30.glBindVertexArray(0);
        waterShader.stop();

        OpenGlUtils.disableBlending();
        OpenGlUtils.cullBackFaces(true);
//        OpenGlUtils.goWireframe(false);
    }

    private void updateTime() {
        time += WaterTile.WAVE_SPEED;
        waterShader.loadWaveTime(time);
    }

    public void cleanUp() {
        waterShader.cleanUp();
    }

}
