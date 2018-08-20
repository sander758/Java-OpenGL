package nl.sander758.gameclient.engine.guiSystem.texts;

import nl.sander758.gameclient.engine.loader.Mesh;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class GuiText {

    private String text;
    private FontStyle fontStyle;
    private float fontSize;
    private float lineWidth;
    private Vector2f position;

    private Mesh mesh;

    private List<Line> lines = new ArrayList<>();

    /**
     * Creates a new renderable gui text containing lines, words and characters.
     *
     * @param text The text string to render
     * @param fontStyle The font used to render the text
     * @param fontSize Size of the font, 1 is normal size and 2 is double of 1
     * @param lineWidth The width of a single line in screen space, so for example 1 is the width of the whole screen
     *                  and 0.5 is half of the screen.
     * @param position The 2d position of the text this position is the top left corner.
     */
    public GuiText(String text, FontStyle fontStyle, float fontSize, float lineWidth, Vector2f position) {
        this.text = text;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        this.lineWidth = lineWidth;
        this.position = position;
        this.lines = TextFactory.getTextFactory().buildGuiTextLines(this);
    }

    public FontStyle getFontStyle() {
        return fontStyle;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public float getFontSize() {
        return fontSize;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void generateMesh() {
        mesh = TextFactory.getTextFactory().generateGuiTextMesh(this);
    }
}
