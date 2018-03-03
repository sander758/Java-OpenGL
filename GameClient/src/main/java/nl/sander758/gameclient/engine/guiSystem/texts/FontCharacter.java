package nl.sander758.gameclient.engine.guiSystem.texts;

public class FontCharacter {

    private int id;

    private int xTextureCoordinate;
    private int yTextureCoordinate;

    private int xTextureSize;
    private int yTextureSize;

    public FontCharacter(int id, int xTextureCoordinate, int yTextureCoordinate, int xTextureSize, int yTextureSize) {
        this.id = id;
        this.xTextureCoordinate = xTextureCoordinate;
        this.yTextureCoordinate = yTextureCoordinate;
        this.xTextureSize = xTextureSize;
        this.yTextureSize = yTextureSize;
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
}
