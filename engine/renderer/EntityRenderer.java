package renderer;

import scene.Camera;
import entities.Entity;
import scene.Light;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import shaders.staticShader.StaticShader;
import utils.Maths;

import java.util.List;

public class EntityRenderer {

    private StaticShader staticShader;

    public EntityRenderer() {
        staticShader = new StaticShader();
        Matrix4f projectionMatrix = Maths.createProjectionMatrix();

        staticShader.start();
        staticShader.loadProjectionMatrix(projectionMatrix);
        staticShader.stop();
    }

    public void render(List<Entity> entities, Camera camera, Light light) {
        staticShader.start();
        staticShader.loadViewMatrix(camera);
        staticShader.loadLight(light);

        for (Entity entity : entities) {
            GL30.glBindVertexArray(entity.getModel().getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);

//            entity.increaseRotation(0, 3, 0);
            Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation().x, entity.getRotation().y, entity.getRotation().z, entity.getScale());
            staticShader.loadTransformationMatrix(transformationMatrix);

            GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
        }

        GL30.glBindVertexArray(0);

        staticShader.stop();
    }

    public void cleanUp() {
        staticShader.cleanUp();
    }

}
