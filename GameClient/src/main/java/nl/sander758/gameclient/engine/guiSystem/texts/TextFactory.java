package nl.sander758.gameclient.engine.guiSystem.texts;

import nl.sander758.gameclient.engine.display.WindowManager;
import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.loader.VBO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextFactory {

    private List<FontType> fonts = new ArrayList<>();

    public static final int SPACE_ASCII = 32;

    private static TextFactory textFactory = new TextFactory();

    public static TextFactory getTextFactory() {
        return textFactory;
    }

    public GuiText getText(String text, String font, float fontSize) throws FontLoadingException {
        for (FontType type : fonts) {
            if (type.getName().equalsIgnoreCase(font)) {
                return createGuiText(text, type, fontSize);
            }
        }
        FontType type = loadFontData(font);
        return createGuiText(text, type, fontSize);
    }

    private GuiText createGuiText(String text, FontType font, float fontSize) {
        Mesh mesh = new Mesh();

        float aspectRatio = (float) WindowManager.getWidth() / (float) WindowManager.getHeight();

        List<Float> vertices = new ArrayList<>();
        List<Float> textureCoordinates = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int cursor = 0;
        int vertexCount = 0;

        char[] characters = text.toCharArray();
        for (char character : characters) {
            int ascii = (int) character;

            if (ascii == SPACE_ASCII) {
                cursor += font.getSpaceWidth();
            }

            FontCharacter fontCharacter = font.getCharacter(ascii);

            if (fontCharacter == null) {
                continue;
            }

            float topLeftX = cursor / 512f;
            float topLeftY = fontCharacter.getyTextureSize() / 512f;
            float topLeftXTexture = fontCharacter.getxTextureCoordinate() / 512f;
            float topLeftYTexture = fontCharacter.getyTextureCoordinate() / 512f;

            float bottomLeftX = cursor / 512f;
            float bottomLeftY = 0;
            float bottomLeftXTexture = fontCharacter.getxTextureCoordinate() / 512f;
            float bottomLeftYTexture = (fontCharacter.getyTextureCoordinate() + fontCharacter.getyTextureSize()) / 512f;

            float topRightX = (cursor + fontCharacter.getxTextureSize()) / 512f;
            float topRightY = fontCharacter.getyTextureSize() / 512f;
            float topRightXTexture = (fontCharacter.getxTextureCoordinate() + fontCharacter.getxTextureSize()) / 512f;
            float topRightYTexture = fontCharacter.getyTextureCoordinate() / 512f;

            float bottomRightX = (cursor + fontCharacter.getxTextureSize()) / 512f;
            float bottomRightY = 0;
            float bottomRightXTexture = (fontCharacter.getxTextureCoordinate() + fontCharacter.getxTextureSize()) / 512f;
            float bottomRightYTexture = (fontCharacter.getyTextureCoordinate() + fontCharacter.getyTextureSize()) / 512f;

            cursor += fontCharacter.getxTextureSize();

            vertices.add(topLeftX);
            vertices.add(topLeftY);
            textureCoordinates.add(topLeftXTexture);
            textureCoordinates.add(topLeftYTexture);
            int topLeftIndex = vertexCount;
            vertexCount++;

            vertices.add(bottomLeftX);
            vertices.add(bottomLeftY);
            textureCoordinates.add(bottomLeftXTexture);
            textureCoordinates.add(bottomLeftYTexture);
            int bottomLeftIndex = vertexCount;
            vertexCount++;

            vertices.add(topRightX);
            vertices.add(topRightY);
            textureCoordinates.add(topRightXTexture);
            textureCoordinates.add(topRightYTexture);
            int topRightIndex = vertexCount;
            vertexCount++;

            vertices.add(bottomRightX);
            vertices.add(bottomRightY);
            textureCoordinates.add(bottomRightXTexture);
            textureCoordinates.add(bottomRightYTexture);
            int bottomRightIndex = vertexCount;
            vertexCount++;

            indices.add(topLeftIndex);
            indices.add(bottomLeftIndex);
            indices.add(topRightIndex);

            indices.add(topRightIndex);
            indices.add(bottomLeftIndex);
            indices.add(bottomRightIndex);
        }

        float[] verticesArray = new float[vertices.size()];
        float[] textureCoordinatesArray = new float[textureCoordinates.size()];
        int[] indicesArray = new int[indices.size()];

        int pointer = 0;
        for (Float vertex : vertices) {
            verticesArray[pointer] = vertex;
            pointer++;
        }

        pointer = 0;
        for (Float coordinate : textureCoordinates) {
            textureCoordinatesArray[pointer] = coordinate;
            pointer++;
        }

        pointer = 0;
        for (Integer index : indices) {
            indicesArray[pointer] = index;
            pointer++;
        }

        mesh.bindVAO();
        mesh.attachVBO(new VBO(0, 2, verticesArray));
        mesh.attachVBO(new VBO(1, 2, textureCoordinatesArray));
        mesh.attachVBO(new VBO(indicesArray));
        mesh.unbindVAO();
        mesh.setVertexCount(indicesArray.length);

        return new GuiText(mesh, text, font, fontSize);
    }

    private FontType loadFontData(String font) throws FontLoadingException {
        ClassLoader loader = TextFactory.class.getClassLoader();
        List<String> lines = new ArrayList<>();

        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = loader.getResourceAsStream("fonts/" + font + ".fnt");
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String readerLine;
            while ((readerLine = reader.readLine()) != null) {
                lines.add(readerLine);
            }
        } catch (IOException e) {
            throw new FontLoadingException("Could not load font file: 'fonts/" + font + ".fnt' with message: " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        FontType fontType = new FontType(font, lines);
        fonts.add(fontType);
        return fontType;
    }

}
