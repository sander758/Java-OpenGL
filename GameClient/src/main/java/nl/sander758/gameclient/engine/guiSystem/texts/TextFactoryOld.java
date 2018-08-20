package nl.sander758.gameclient.engine.guiSystem.texts;

import nl.sander758.common.logger.Logger;
import nl.sander758.gameclient.engine.display.WindowManager;
import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.loader.VBO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextFactoryOld {

//    private List<FontStyle> fonts = new ArrayList<>();
//
//    public static final int SPACE_ASCII = 32;
//
//    private static TextFactoryOld textFactory = new TextFactoryOld();
//
//    public static TextFactoryOld getTextFactory() {
//        return textFactory;
//    }
//
//    public GuiText getText(String text, String font, float fontSize, float lineWidth) throws FontLoadingException {
//        for (FontStyle type : fonts) {
//            if (type.getName().equalsIgnoreCase(font)) {
//                return createGuiTextMesh(createTextLines(text, type, fontSize));
//            }
//        }
//        FontStyle type = loadFontData(font);
//        return createGuiTextMesh(createTextLines(text, type, fontSize));
//    }
//
//    private List<Line> createTextLines(String text, FontStyle font, float fontSize) {
//        List<Line> lines = new ArrayList<>();
//
//        char[] characters = text.toCharArray();
//
//        float aspectRatio = (float) WindowManager.getWidth() / (float) WindowManager.getHeight();
//        int cursor = 0;
//
//        Line currentLine = new Line();
//        Word currentWord = new Word();
//
//        for (char character : characters) {
//            int ascii = (int) character;
//
//            if (ascii == SPACE_ASCII) {
//                if (shouldCreateNewline(currentLine, currentWord)) {
//
//                }
//                cursor += font.getSpaceWidth() / aspectRatio;
//            }
//
//            FontCharacter fontCharacter = font.getCharacter(ascii);
//            if (fontCharacter == null) {
//                continue;
//            }
//        }
//
//        return lines;
//    }
//
//    private boolean shouldCreateNewline(Line line, Word word) {
//
//    }
//
//    private GuiText createGuiTextMesh(List<Line> lines) {
//        Mesh mesh = new Mesh();
//
//        float aspectRatio = (float) WindowManager.getWidth() / (float) WindowManager.getHeight();
//
//        List<Float> vertices = new ArrayList<>();
//        List<Float> textureCoordinates = new ArrayList<>();
//        List<Integer> indices = new ArrayList<>();
//
//        int cursor = 0;
//        int vertexCount = 0;
//
//        char[] characters = text.toCharArray();
//        for (char character : characters) {
//            System.out.println(character);
//            int ascii = (int) character;
//
//            if (ascii == SPACE_ASCII) {
//                cursor += font.getSpaceWidth() / aspectRatio;
//            }
//
//            FontCharacter fontCharacter = font.getCharacter(ascii);
//
//            if (fontCharacter == null) {
//                continue;
//            }
//
//            float topLeftX = cursor / 512f;
//            float topLeftY = fontCharacter.getyTextureSize() / 512f;
//            float topLeftXTexture = fontCharacter.getxTextureCoordinate() / 512f;
//            float topLeftYTexture = fontCharacter.getyTextureCoordinate() / 512f;
//
//            float bottomLeftX = cursor / 512f;
//            float bottomLeftY = 0;
//            float bottomLeftXTexture = fontCharacter.getxTextureCoordinate() / 512f;
//            float bottomLeftYTexture = (fontCharacter.getyTextureCoordinate() + fontCharacter.getyTextureSize()) / 512f;
//
//            float topRightX = (cursor + (fontCharacter.getxTextureSize() / aspectRatio)) / 512f;
////            float topRightX = (cursor + (fontCharacter.getxTextureSize())) / 512f;
//            float topRightY = fontCharacter.getyTextureSize() / 512f;
//            float topRightXTexture = (fontCharacter.getxTextureCoordinate() + fontCharacter.getxTextureSize()) / 512f;
//            float topRightYTexture = fontCharacter.getyTextureCoordinate() / 512f;
//
//            float bottomRightX = (cursor + (fontCharacter.getxTextureSize() / aspectRatio)) / 512f;
////            float bottomRightX = (cursor + (fontCharacter.getxTextureSize())) / 512f;
//            float bottomRightY = 0;
//            float bottomRightXTexture = (fontCharacter.getxTextureCoordinate() + fontCharacter.getxTextureSize()) / 512f;
//            float bottomRightYTexture = (fontCharacter.getyTextureCoordinate() + fontCharacter.getyTextureSize()) / 512f;
//
//            cursor += fontCharacter.getxTextureSize() / aspectRatio;
////            cursor += fontCharacter.getxTextureSize();
//
//            vertices.add(topLeftX);
//            vertices.add(topLeftY);
//            textureCoordinates.add(topLeftXTexture);
//            textureCoordinates.add(topLeftYTexture);
//            int topLeftIndex = vertexCount;
//            vertexCount++;
//
//            vertices.add(bottomLeftX);
//            vertices.add(bottomLeftY);
//            textureCoordinates.add(bottomLeftXTexture);
//            textureCoordinates.add(bottomLeftYTexture);
//            int bottomLeftIndex = vertexCount;
//            vertexCount++;
//
//            vertices.add(topRightX);
//            vertices.add(topRightY);
//            textureCoordinates.add(topRightXTexture);
//            textureCoordinates.add(topRightYTexture);
//            int topRightIndex = vertexCount;
//            vertexCount++;
//
//            vertices.add(bottomRightX);
//            vertices.add(bottomRightY);
//            textureCoordinates.add(bottomRightXTexture);
//            textureCoordinates.add(bottomRightYTexture);
//            int bottomRightIndex = vertexCount;
//            vertexCount++;
//
//            indices.add(topLeftIndex);
//            indices.add(bottomLeftIndex);
//            indices.add(topRightIndex);
//
//            indices.add(topRightIndex);
//            indices.add(bottomLeftIndex);
//            indices.add(bottomRightIndex);
//        }
//
//        float[] verticesArray = new float[vertices.size()];
//        float[] textureCoordinatesArray = new float[textureCoordinates.size()];
//        int[] indicesArray = new int[indices.size()];
//
//        int pointer = 0;
//        for (Float vertex : vertices) {
//            verticesArray[pointer] = vertex;
//            pointer++;
//        }
//
//        pointer = 0;
//        for (Float coordinate : textureCoordinates) {
//            textureCoordinatesArray[pointer] = coordinate;
//            pointer++;
//        }
//
//        pointer = 0;
//        for (Integer index : indices) {
//            indicesArray[pointer] = index;
//            pointer++;
//        }
//
//        mesh.bindVAO();
//        mesh.attachVBO(new VBO(0, 2, verticesArray));
//        mesh.attachVBO(new VBO(1, 2, textureCoordinatesArray));
//        mesh.attachVBO(new VBO(indicesArray));
//        mesh.unbindVAO();
//        mesh.setVertexCount(indicesArray.length);
//
//        return new GuiText(mesh, text, font, fontSize);
//    }


}
