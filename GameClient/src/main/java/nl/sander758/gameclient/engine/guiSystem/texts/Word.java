package nl.sander758.gameclient.engine.guiSystem.texts;

import java.util.ArrayList;
import java.util.List;

public class Word {

    /**
     * Characters in the
     */
    private List<FontCharacter> characters = new ArrayList<>();

    private int maxLineWidth;

    private float fontSize;

    private int currentWidth = 0;

    /**
     * Create a new word without any characters.
     *
     * @param maxLineWidth The max line width in pixels.
     */
    public Word(int maxLineWidth, float fontSize) {
        this.maxLineWidth = maxLineWidth;
        this.fontSize = fontSize;
    }

    /**
     * Creates a new word with a pre specified list of characters.
     *
     * @param maxLineWidth The max line width in pixels.
     * @param characters The characters that are in this word.
     */
    public Word(int maxLineWidth, float fontSize, List<FontCharacter> characters) {
        this.maxLineWidth = maxLineWidth;
        this.fontSize = fontSize;
        this.characters = characters;

        for (FontCharacter character : characters) {
            currentWidth += character.getxAdvance() * fontSize;
        }
    }

    /**
     * Tries to add a character to this word and checks if the currentWidth stays within the maxLineWidth.
     *
     * @param character The character attempting to add.
     * @return whether the character could be added to the word and keep the word within the maxLineWidth.
     */
    public boolean attemptToAddCharacter(FontCharacter character) {
        if ((currentWidth + character.getxAdvance() * fontSize) > maxLineWidth) {
            return false;
        }
        characters.add(character);
        currentWidth += character.getxAdvance() * fontSize;
        return true;
    }

    public List<FontCharacter> getCharacters() {
        return characters;
    }

    /**
     * Get the first characters of this word within a certain width.
     *
     * @param width The width in which the characters must fit in pixels.
     * @return The list of characters within the specified width.
     */
    public List<FontCharacter> getCharactersWithin(int width) {
        List<FontCharacter> charactersWithin = new ArrayList<>();
        int cursor = 0;
        for (FontCharacter character : characters) {
            if ((cursor + character.getxAdvance() * fontSize) > width) {
                break;
            }
            cursor += character.getxAdvance() * fontSize;
            charactersWithin.add(character);
        }
        return charactersWithin;
    }

    /**
     * Get the remaining characters outside of a certain width.
     *
     * @param width The width of the first characters that must be skipped in pixels.
     * @return The list of characters outside the specified width.
     */
    public List<FontCharacter> getCharactersOutside(int width) {
        List<FontCharacter> charactersOutside = new ArrayList<>();
        int cursor = 0;
        for (FontCharacter character : characters) {
            cursor += character.getxAdvance() * fontSize;
            if (cursor <= width) {
                continue;
            }
            charactersOutside.add(character);
        }
        return charactersOutside;
    }

    /**
     * Get the width of the current characters in this word without aspect ratio calculation.
     *
     * @return The width in pixels.
     */
    public int getCurrentWidth() {
        return currentWidth;
    }
}
