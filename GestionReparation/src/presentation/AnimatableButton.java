package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

public class AnimatableButton extends JButton {
    private Color baseColor, targetColor, currentColor;
    private final int ANIMATION_STEPS = 16;
    private Timer animTimer;
    private int animStep = 0;
    public AnimatableButton(String text) {
        super(text);
        baseColor = Theme.ACCENT;
        targetColor = Theme.ACCENT_DARK;
        currentColor = baseColor;
        setForeground(Theme.BUTTON_TEXT);
        setFont(getFont().deriveFont(Font.BOLD, 16f));
        setBackground(baseColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setMargin(new Insets(7,30,7,30));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { animateTo(targetColor); }
            @Override
            public void mouseExited(MouseEvent e) { animateTo(baseColor); }
        });
    }
    private void animateTo(Color toColor) {
        if(animTimer!=null && animTimer.isRunning()) animTimer.stop();
        final Color from = currentColor;
        animStep = 0;
        animTimer = new Timer(12, evt -> {
            animStep++;
            float f = Math.min(1, animStep/(float)ANIMATION_STEPS);
            currentColor = Theme.blend(from, toColor, f);
            setBackground(currentColor);
            if(f>=1 && animTimer!=null) animTimer.stop();
        });
        animTimer.start();
    }
}
