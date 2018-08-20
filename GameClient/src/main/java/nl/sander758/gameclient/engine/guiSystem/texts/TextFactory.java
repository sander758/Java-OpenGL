package nl.sander758.gameclient.engine.guiSystem.texts;

import nl.sander758.common.logger.Logger;
import nl.sander758.gameclient.engine.display.WindowManager;
import nl.sander758.gameclient.engine.loader.Mesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextFactory {
    private static TextFactory textFactory = new TextFactory();
    public static final int ASCII_SPACE = 32;

    public static TextFactory getTextFactory() {
        return textFactory;
    }

    private List<FontStyle> fonts = new ArrayList<>();

    public FontStyle getFontStyle(String fontStyle) {
        for (FontStyle font : fonts) {
            if (font.getName().equalsIgnoreCase(fontStyle)) {
                return font;
            }
        }
        throw new NullPointerException();
    }

    public void addFontStyle(String fontStyle) throws FontLoadingException {
        FontStyle font = loadFontStyle(fontStyle);
        fonts.add(font);
    }

    /**
     * Creates a list of lines containing words from a single string.
     * A word is created every time a space character is found or a word is too long for a single line width.
     *
     * @param text The gui text used to create lines and words.
     * @return A list of lines containing the words and characters of the gui text.
     */
    public List<Line> buildGuiTextLines(GuiText text) {
        char[] characters = text.getText().toCharArray();

        int screenWidth = WindowManager.getWidth();

        int lineWidth = (int) (text.getLineWidth() * (float) screenWidth);

        FontStyle fontStyle = text.getFontStyle();

        List<Line> lines = new ArrayList<>();
        Line currentLine = new Line(lineWidth, fontStyle.getSpaceWidth());
        Word currentWord = new Word(lineWidth);

        for (char character : characters) {
            int ascii = (int) character;

            // 1. If character is a space, create a new word
            // 2. Try to add the current word to the line
            // 3. If the line is too long with the word to add, create new line and add the word to that line
            // 4. If a word is longer than a the max line length, create a new word and add that new word to the new line

            if (ascii == ASCII_SPACE) {
                // Try to add the current word to the current line.
                // If there is enough room on the current line, add the word to the current line.
                if (!currentLine.attemptToAddWord(currentWord)) {
                    // The word couldn't be added to the current line, create a new line and add the word to that line.
                    lines.add(currentLine);
                    currentLine = new Line(lineWidth, fontStyle.getSpaceWidth());
                    currentLine.attemptToAddWord(currentWord);
                }
                currentWord = new Word(lineWidth);

            } else {
                // Try to add the character to the current word, if the word is longer than the line width it should
                // go back and check which part of the word fits on the current line and add that part to the current line.
                // The rest of the characters will be a new word

                FontCharacter fontCharacter = fontStyle.getCharacter(ascii);

                if (!currentWord.attemptToAddCharacter(fontCharacter)) {
                    // Could not add font character to word because the word is longer then a single line, go back and
                    // check which part of the word fits on the current line, add the rest to a new word.

                    int freeLineWidth = currentLine.getFreeLineSpace();

                    // If there are more than 0 pixels left on the current line.
                    if (freeLineWidth > 0) {
                        // There is still space on the current line.
                        List<FontCharacter> freeLineWidthCharacters = currentWord.getCharactersWithin(freeLineWidth);
                        if (freeLineWidthCharacters.size() > 0) {
                            currentLine.attemptToAddWord(new Word(lineWidth, freeLineWidthCharacters));
                        }
                        lines.add(currentLine);
                        currentLine = new Line(lineWidth, fontStyle.getSpaceWidth());
                        List<FontCharacter> nextLineCharacters = currentWord.getCharactersOutside(freeLineWidth);
                        if (nextLineCharacters.size() > 0) {
                            currentLine.attemptToAddWord(new Word(lineWidth, nextLineCharacters));
                        }
                    } else {
                        // There is no available space on the current line and add the current word word to a complete new line.
                        // Also add the current line, the question is, why isn't this line added yet?
                        lines.add(currentLine);
                        Line singleWordLine = new Line(lineWidth, fontStyle.getSpaceWidth());
                        singleWordLine.attemptToAddWord(currentWord);
                        lines.add(currentLine);
                        currentLine = new Line(lineWidth, fontStyle.getSpaceWidth());
                        currentWord = new Word(lineWidth);
                    }
                }
            }
        }
        if (!currentLine.attemptToAddWord(currentWord)) {
            lines.add(currentLine);
            currentLine = new Line(lineWidth, fontStyle.getSpaceWidth());
            currentLine.attemptToAddWord(currentWord);
            lines.add(currentLine);
        } else {
            lines.add(currentLine);
        }

        return lines;
    }

    public Mesh generateGuiTextMesh(GuiText text) {
        Mesh mesh = new Mesh();

//        text.get

        return mesh;
    }

    /**
     * Loads a specific font name from a file to a FontStyle object.
     *
     * @param font The name of the font.
     * @return A FontStyle object which contains the configuration for the specific font.
     * @throws FontLoadingException Thrown when font could not be found or read.
     */
    private FontStyle loadFontStyle(String font) throws FontLoadingException {
        ClassLoader loader = TextFactory.class.getClassLoader();
        List<String> lines = new ArrayList<>();

        Logger.debug("Loading font data for font: " + font);

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
        FontStyle fontType = new FontStyle(font, lines);
        fonts.add(fontType);

        Logger.debug("Done with font loading for font: " + font);

        return fontType;
    }
}
