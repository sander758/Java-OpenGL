package nl.sander758.gameclient.engine.waterSystem;

import nl.sander758.gameclient.engine.display.Camera;
import nl.sander758.gameclient.engine.scene.Light;
import nl.sander758.gameclient.engine.utils.Maths;
import nl.sander758.gameclient.engine.utils.OpenGlUtils;
import nl.sander758.gameclient.engine.utils.Timer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class WaterRenderer {

    private WaterShader shader = new WaterShader();
    private float time = 0;

    public WaterRenderer(Matrix4f projectionMatrix) {
        shader.start();
        shader.projectionMatrix.loadUniform(projectionMatrix);
        shader.height.loadUniform(WaterTile.WATER_HEIGHT);
        shader.connectTextureUnits();
        shader.nearFarPlanes.loadUniform(new Vector2f(Maths.NEAR_PLANE, Maths.FAR_PLANE));
        shader.stop();
    }

    public void bindTextures(int reflectionColor, int refractionColor, int refractionDepth) {
        shader.reflectionTexture.bindTexture(reflectionColor);
        shader.refractionTexture.bindTexture(refractionColor);
        shader.depthTexture.bindTexture(refractionDepth);
    }

    public void render(List<WaterTile> waterTiles, Camera camera, Light light) {
        OpenGlUtils.enableAlphaBlending();
        OpenGlUtils.cullBackFaces(false);

        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        shader.start();
        shader.viewMatrix.loadUniform(viewMatrix);
        shader.cameraPos.loadUniform(camera.getPosition());
        shader.lightDirection.loadUniform(light.getLightDirection());
        shader.lightColor.loadUniform(light.getLightColor());
        shader.lightBias.loadUniform(light.getBias());
        updateTime();
        shader.waveTime.loadUniform(time);

        for (WaterTile waterTile : waterTiles) {
            waterTile.getMesh().bindVAO();
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);

            Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(waterTile.getPosition().x, WaterTile.WATER_HEIGHT, waterTile.getPosition().y), new Vector3f(0, 0, 0), waterTile.getScale());
            shader.transformationMatrix.loadUniform(transformationMatrix);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, waterTile.getMesh().getVertexCount());

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
        }
        GL30.glBindVertexArray(0);

        shader.stop();

        OpenGlUtils.disableBlending();
        OpenGlUtils.cullBackFaces(true);
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    private void updateTime() {
        time += WaterTile.WAVE_SPEED * Timer.getDeltaTime();
    }
}
