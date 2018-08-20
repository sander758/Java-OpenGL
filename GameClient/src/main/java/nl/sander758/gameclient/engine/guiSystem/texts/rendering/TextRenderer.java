package nl.sander758.gameclient.engine.guiSystem.texts.rendering;

import nl.sander758.gameclient.engine.guiSystem.texts.FontStyle;
import nl.sander758.gameclient.engine.guiSystem.texts.GuiText;
import nl.sander758.gameclient.engine.guiSystem.texts.TextRegistry;
import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.utils.Maths;
import nl.sander758.gameclient.engine.utils.OpenGlUtils;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;

public class TextRenderer {

    private TextShader shader = new TextShader();

    public TextRenderer() {
        shader.start();
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render() {
        HashMap<FontStyle, List<GuiText>> texts = TextRegistry.getTexts();

        shader.start();

        OpenGlUtils.enableDepthTesting(false);
        OpenGlUtils.enableAlphaBlending();

        for (FontStyle font : texts.keySet()) {
            shader.fontAtlas.bindTexture(font.getFontAtlas().getTextureID());

            for (GuiText text : texts.get(font)) {
                shader.transformationMatrix.loadUniform(Maths.createTransformationMatrix(text.getPosition(), new Vector2f(1f, 1f)));

                Mesh mesh = text.getMesh();
                mesh.prepareRender();

                GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

                mesh.endRender();
            }
        }

        OpenGlUtils.enableDepthTesting(true);
        OpenGlUtils.disableBlending();

        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
