package nl.sander758.gameclient.engine.guiSystem;

import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.loader.MeshLoader;
import nl.sander758.gameclient.engine.utils.Maths;
import nl.sander758.gameclient.engine.utils.OpenGlUtils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class GuiRenderer {

    private GuiShader shader = new GuiShader();
    private Mesh quad;

    public GuiRenderer() {
        quad = MeshLoader.loadQuad();
        shader.start();
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(List<GuiTexture> textures) {
        if (textures.size() == 0) {
            return;
        }

        shader.start();

        quad.bindVAO();
        GL20.glEnableVertexAttribArray(0);
        OpenGlUtils.enableDepthTesting(false);

        for (GuiTexture texture : textures) {
            shader.guiTexture.bindTexture(texture.getTexture());
            Matrix4f transformationMatrix = Maths.createTransformationMatrix(texture.getPosition(), texture.getScale());
            shader.transformationMatrix.loadUniform(transformationMatrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }

        OpenGlUtils.enableDepthTesting(true);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
