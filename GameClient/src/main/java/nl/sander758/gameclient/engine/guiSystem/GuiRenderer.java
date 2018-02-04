package nl.sander758.gameclient.engine.guiSystem;

import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.loader.VBO;
import nl.sander758.gameclient.engine.utils.Maths;
import nl.sander758.gameclient.engine.utils.OpenGlUtils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GuiRenderer {

    private GuiShader shader = new GuiShader();
    private Mesh quad;

    public GuiRenderer() {
        quad = generateQuad();
        shader.start();
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render() {
        if (GuiTextureRegistry.getTextures().size() == 0) {
            return;
        }

        shader.start();

        quad.bindVAO();
        GL20.glEnableVertexAttribArray(0);
        OpenGlUtils.enableDepthTesting(false);

        for (GuiTexture texture : GuiTextureRegistry.getTextures()) {
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

    private Mesh generateQuad() {
        float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };

        Mesh mesh = new Mesh();
        mesh.bindVAO();
        mesh.attachVBO(new VBO(0, 2, positions));
        mesh.unbindVAO();
        mesh.setVertexCount(positions.length / 2);
        return mesh;
    }
}
