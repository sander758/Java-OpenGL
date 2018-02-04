package nl.sander758.gameclient.engine.terrainSystem;

import nl.sander758.gameclient.engine.entitySystem.entities.TerrainEntity;
import nl.sander758.gameclient.engine.player.Camera;
import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.player.PlayablePlayer;
import nl.sander758.gameclient.engine.scene.Light;
import nl.sander758.gameclient.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class TerrainRenderer {

    private TerrainShader shader = new TerrainShader();

    public TerrainRenderer(Matrix4f projectionMatrix, float shadowDistance, float shadowMapSize) {
        shader.start();
        shader.projectionMatrix.loadUniform(projectionMatrix);
        shader.shadowDistance.loadUniform(shadowDistance);
        shader.shadowMapSize.loadUniform(shadowMapSize);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void bindTextures(int shadowMap) {
        shader.shadowMap.bindTexture(shadowMap);
    }

    public void render(PlayablePlayer player, Light light, Vector4f clipPlane, Matrix4f shadowMapSpaceMatrix, boolean doShadow) {
        shader.start();
        shader.viewMatrix.loadUniform(player.getViewMatrix());
        shader.lightDirection.loadUniform(light.getLightDirection());
        shader.lightColor.loadUniform(light.getLightColor());
        shader.clipPlane.loadUniform(clipPlane);
        shader.toShadowMapSpaceMatrix.loadUniform(shadowMapSpaceMatrix);
        shader.doShadow.loadUniform(doShadow);

        for (TerrainEntity terrainEntity : TerrainEntityRegistry.getEntities()) {
            if (terrainEntity.getModel() == null) {
                continue;
            }

            shader.transformationMatrix.loadUniform(terrainEntity.getTransformationMatrix());
            for (Mesh mesh : terrainEntity.getModel().getMeshes()) {
                mesh.bindVAO();
                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);
                GL20.glEnableVertexAttribArray(2);

                GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

                GL20.glDisableVertexAttribArray(0);
                GL20.glDisableVertexAttribArray(1);
                GL20.glDisableVertexAttribArray(2);
            }
        }
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
