package nl.sander758.gameclient.engine.guiSystem.texts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextRegistry {

    public static HashMap<FontStyle, List<GuiText>> texts = new HashMap<>();

    public static HashMap<FontStyle, List<GuiText>> getTexts() {
        return texts;
    }

    public static void addText(GuiText text) {
        if (!texts.containsKey(text.getFontStyle())) {
            texts.put(text.getFontStyle(), new ArrayList<>());
        }
        texts.get(text.getFontStyle()).add(text);
    }

}
