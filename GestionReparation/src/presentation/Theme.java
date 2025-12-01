package presentation;

import java.awt.*;

public class Theme {
    // HackTheBox-inspired dark theme
    public static final Color ACCENT = new Color(159, 239, 0);          // neon green
    public static final Color ACCENT_DARK = new Color(124, 186, 0);
    public static final Color CARD_BG = new Color(17, 24, 39);          // dark card
    public static final Color DASHBOARD_BG1 = new Color(10, 16, 32);    // dark navy
    public static final Color DASHBOARD_BG2 = new Color(15, 23, 42);    // slightly lighter navy
    public static final Color CARD_BORDER = new Color(55, 65, 81);
    public static final Color BUTTON_TEXT = new Color(248, 250, 252);   // near white
    public static final Color TEXT_PRIMARY = new Color(229, 231, 235);  // light gray
    public static final Color TEXT_MUTED = new Color(148, 163, 184);    // muted gray
    public static final Color INPUT_BG = new Color(31, 41, 55);         // dark input background
    public static final Color INPUT_BORDER = new Color(55, 65, 81);
    public static final Color INPUT_TEXT = new Color(249, 250, 251);

    public static Color blend(Color c1, Color c2, float ratio) {
        int r = (int)(c1.getRed() + ratio * (c2.getRed() - c1.getRed()));
        int g = (int)(c1.getGreen() + ratio * (c2.getGreen() - c1.getGreen()));
        int b = (int)(c1.getBlue() + ratio * (c2.getBlue() - c1.getBlue()));
        return new Color(r, g, b);
    }
}
