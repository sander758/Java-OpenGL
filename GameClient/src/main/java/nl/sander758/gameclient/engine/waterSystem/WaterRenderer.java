package nl.sander758.gameclient.engine.waterSystem;

import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.player.Camera;
import nl.sander758.gameclient.engine.player.PlayablePlayer;
import nl.sander758.gameclient.engine.scene.Light;
import nl.sander758.gameclient.engine.utils.Maths;
import nl.sander758.gameclient.engine.utils.OpenGlUtils;
import nl.sander758.gameclient.engine.utils.Timer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class WaterRenderer {
    public static final float WATER_HEIGHT = -0.5f;
    public static final float WAVE_SPEED = 0.14f;

    public static final float REFRACT_OFFSET = 1f;
    public static final float REFLECT_OFFSET = 0.1f;

    private WaterShader shader = new WaterShader();
    private float time = 0;

    public WaterRenderer(Matrix4f projectionMatrix) {
        shader.start();
        shader.projectionMatrix.loadUniform(projectionMatrix);
        shader.height.loadUniform(WATER_HEIGHT);
        shader.connectTextureUnits();
        shader.nearFarPlanes.loadUniform(new Vector2f(Maths.NEAR_PLANE, Maths.FAR_PLANE));
        shader.stop();
    }

    public void bindTextures(int reflectionColor, int refractionColor, int refractionDepth) {
        shader.reflectionTexture.bindTexture(reflectionColor);
        shader.refractionTexture.bindTexture(refractionColor);
        shader.depthTexture.bindTexture(refractionDepth);
    }

    public void render(PlayablePlayer player, Light light) {
        OpenGlUtils.enableAlphaBlending();
        OpenGlUtils.cullBackFaces(false);

        shader.start();
        shader.viewMatrix.loadUniform(player.getViewMatrix());
        shader.cameraPos.loadUniform(player.getLocation());
        shader.lightDirection.loadUniform(light.getLightDirection());
        shader.lightColor.loadUniform(light.getLightColor());
        shader.lightBias.loadUniform(light.getBias());
        updateTime();
        shader.waveTime.loadUniform(time);

        for (WaterEntity waterEntity : WaterEntityRegistry.getEntities()) {
            if (waterEntity.getModel() == null) {
                continue;
            }

            shader.transformationMatrix.loadUniform(waterEntity.getTransformationMatrix());
            for (Mesh mesh : waterEntity.getModel().getMeshes()) {
                mesh.prepareRender();

                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());

                mesh.endRender();
            }
        }

        shader.stop();

        OpenGlUtils.disableBlending();
        OpenGlUtils.cullBackFaces(true);
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    private void updateTime() {
        time += WAVE_SPEED * Timer.getDeltaTime();
    }
}
