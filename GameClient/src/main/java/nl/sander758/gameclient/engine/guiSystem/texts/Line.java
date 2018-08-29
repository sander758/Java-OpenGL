package nl.sander758.gameclient.engine.guiSystem.texts;

import java.util.ArrayList;
import java.util.List;

public class Line {

    private List<Word> words = new ArrayList<>();

    private int maxLineWidth;

    private float fontSize;

    private int spaceWidth;

    public Line(int maxLineWidth, float fontSize, int spaceWidth) {
        this.maxLineWidth = maxLineWidth;
        this.fontSize = fontSize;
        this.spaceWidth = spaceWidth;
    }

    public boolean attemptToAddWord(Word word) {
        if (doesWordFit(word)) {
            words.add(word);
            return true;
        }
        return false;
    }

    public int getCurrentWidth() {
        int currentWidth = 0;
        for (Word word : words) {
            currentWidth += word.getCurrentWidth();
            currentWidth += spaceWidth * fontSize;
        }
        return currentWidth;
    }

    public int getFreeLineSpace() {
        return maxLineWidth - getCurrentWidth();
    }

    public boolean doesWordFit(Word word) {
        return word.getCurrentWidth() < getFreeLineSpace();
    }

    public List<Word> getWords() {
        return words;
    }
}
