package guis;

import loader.GLLoader;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import shaders.guiShader.GuiShader;
import utils.Maths;

import java.util.List;

public class GuiRenderer {

    private GuiShader shader;
    private RawModel quad;

    public GuiRenderer() {
        shader = new GuiShader();

        float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
        quad = GLLoader.getLoader().loadToVao(positions, "guiQuad");
    }

    public void render(List<GuiTexture> textures) {
        shader.start();

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        for (GuiTexture gui : textures) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
            Matrix4f transformationMatrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
            shader.loadTransformationMatrix(transformationMatrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
