package nl.sander758.gameclient.engine.entitySystem;

import nl.sander758.gameclient.engine.entitySystem.entities.StaticEntity;
import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.player.PlayablePlayer;
import nl.sander758.gameclient.engine.scene.Light;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class StaticEntityRenderer {

    private StaticEntityShader shader = new StaticEntityShader();

    public StaticEntityRenderer(Matrix4f projectionMatrix) {
        shader.start();
        shader.projectionMatrix.loadUniform(projectionMatrix);
        shader.stop();
    }

    public void render(PlayablePlayer player, Light light, Vector4f clipPlane) {
        shader.start();
        shader.viewMatrix.loadUniform(player.getViewMatrix());
        shader.lightDirection.loadUniform(light.getLightDirection());
        shader.lightColor.loadUniform(light.getLightColor());
        shader.clipPlane.loadUniform(clipPlane);

        for (StaticEntity fixedEntity : StaticEntityRegistry.getEntities()) {
            if (fixedEntity.getModel() == null) {
                continue;
            }
            shader.transformationMatrix.loadUniform(fixedEntity.getTransformationMatrix());
            for (Mesh mesh : fixedEntity.getModel().getMeshes()) {
                mesh.prepareRender();

                GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

                mesh.endRender();
            }
        }

        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
