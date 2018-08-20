package nl.sander758.gameclient.engine.guiSystem.texts;

import java.util.ArrayList;
import java.util.List;

public class Line {

    private List<Word> words = new ArrayList<>();

    private int maxLineWidth;

    private int spaceWidth;

    public Line(int maxLineWidth, int spaceWidth) {
        this.maxLineWidth = maxLineWidth;
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
            currentWidth += spaceWidth;
        }
        return currentWidth;
    }

    public int getFreeLineSpace() {
        return maxLineWidth - getCurrentWidth();
    }

    public boolean doesWordFit(Word word) {
        return word.getCurrentWidth() < getFreeLineSpace();
    }

}
