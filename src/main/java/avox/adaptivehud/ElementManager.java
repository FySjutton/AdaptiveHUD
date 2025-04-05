package avox.adaptivehud;

import java.util.ArrayList;

public class ElementManager {
    private static final ArrayList<HudElement> hudElements = new ArrayList<>();

    public static void addElement(HudElement element) {
        hudElements.add(element);
    }

    public static void removeElement(HudElement element) {
        hudElements.remove(element);
    }

    public static ArrayList<HudElement> getHudElements()  {
        return hudElements;
    }
}
