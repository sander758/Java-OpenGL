package nl.sander758.gameclient.engine.guiSystem.texts;

import nl.sander758.gameclient.engine.loader.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GuiText {

    private Mesh textMesh;

    private String text;
    private FontType fontType;

    private float fontSize;

    private Vector2f position = new Vector2f(0, 0);


    GuiText(Mesh mesh, String text, FontType fontType, float fontSize) {
        this.textMesh = mesh;
        this.text = text;
        this.fontSize = fontSize;
        this.fontType = fontType;
    }

    public FontType getFontType() {
        return fontType;
    }

    public Mesh getTextMesh() {
        return textMesh;
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
}
