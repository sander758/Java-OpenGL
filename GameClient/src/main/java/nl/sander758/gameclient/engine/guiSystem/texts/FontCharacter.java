package nl.sander758.gameclient.engine.guiSystem.texts;

public class FontCharacter {

    private String character;

    private int id;

    private int xTextureCoordinate;
    private int yTextureCoordinate;

    private int xTextureSize;
    private int yTextureSize;

    private int xOffset;
    private int yOffset;

    private int xAdvance;

    public FontCharacter(int id, int xTextureCoordinate, int yTextureCoordinate, int xTextureSize, int yTextureSize, int xOffset, int yOffset, int xAdvance) {
        this.id = id;
        this.character = Character.toString((char) id);
        this.xTextureCoordinate = xTextureCoordinate;
        this.yTextureCoordinate = yTextureCoordinate;
        this.xTextureSize = xTextureSize;
        this.yTextureSize = yTextureSize;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xAdvance = xAdvance;
    }

    public int getId() {
        return id;
    }

    public int getxTextureCoordinate() {
        return xTextureCoordinate;
    }

    public int getyTextureCoordinate() {
        return yTextureCoordinate;
    }

    public int getxTextureSize() {
        return xTextureSize;
    }

    public int getyTextureSize() {
        return yTextureSize;
    }

    public int getxOffset() {
        return xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }

    public int getxAdvance() {
        return xAdvance;
    }
}
