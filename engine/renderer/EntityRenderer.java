package renderer;

import entities.Camera;
import entities.Entity;
import entities.Light;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import scene.Scene;
import shaders.staticShader.StaticShader;
import utils.Maths;

public class EntityRenderer {

    private StaticShader staticShader = new StaticShader();

    public EntityRenderer() {
        Matrix4f projectionMatrix = Maths.createProjectionMatrix();

        this.staticShader.start();
        this.staticShader.loadProjectionMatrix(projectionMatrix);
        this.staticShader.connectTextureUnits();
        this.staticShader.stop();

    }

    public void render(Scene scene, Camera camera, Light light, Matrix4f toShadowSpace) {
        staticShader.start();
        staticShader.loadViewMatrix(camera);
        staticShader.loadLight(light);
        staticShader.loadToShadowSpaceMatrix(toShadowSpace);

        for (Entity entity : scene.getEntities()) {
            GL30.glBindVertexArray(entity.getModel().getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);

            Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
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
