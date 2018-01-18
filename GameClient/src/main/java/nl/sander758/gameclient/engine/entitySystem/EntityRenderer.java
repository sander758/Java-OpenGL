package nl.sander758.gameclient.engine.entitySystem;

import nl.sander758.gameclient.engine.display.Camera;
import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.scene.Light;
import nl.sander758.gameclient.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class EntityRenderer {

    private EntityShader shader = new EntityShader();

    public EntityRenderer(Matrix4f projectionMatrix) {
        shader.start();
        shader.projectionMatrix.loadUniform(projectionMatrix);
        shader.stop();
    }

    public void render(List<GraphicalEntity> entities, Camera camera, Light light, Vector4f clipPlane) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        shader.start();
        shader.viewMatrix.loadUniform(viewMatrix);
        shader.lightDirection.loadUniform(light.getLightDirection());
        shader.lightColor.loadUniform(light.getLightColor());
        shader.clipPlane.loadUniform(clipPlane);

        for (GraphicalEntity graphicalEntity : entities) {
            Mesh mesh = graphicalEntity.getModel().getMesh();
            mesh.bindVAO();
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);

            Matrix4f transformationMatrix = Maths.createTransformationMatrix(graphicalEntity.getLocation(), graphicalEntity.getRotation(), graphicalEntity.getScale());
            shader.transformationMatrix.loadUniform(transformationMatrix);

            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
        }
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
