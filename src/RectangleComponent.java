import com.intellij.ui.JBColor;
import java.awt.*;
import javax.swing.*;

class RectangleComponent extends JComponent
{
    private final String title;
    private final Color color;
    private final Dimension preferredSize;
    private final int sideSize;

    RectangleComponent(String title, Color color, int sideSize)
    {
        this.title = title;
        this.color = color;
        this.sideSize = sideSize;
        preferredSize = new Dimension(sideSize+100, sideSize + 2);
    }

    public boolean isOpaque() {
        return true;
    }

    public void paint(Graphics g) {
        g.setColor(color);
        g.fillRect(0, 0, sideSize, sideSize);
        g.setColor(JBColor.BLACK);
        g.drawRect(0, 0, sideSize, sideSize);

        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(JBColor.BLACK);
        g.drawString(title, sideSize + 5, sideSize - 2);
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public Dimension getMinimumSize() {
        return preferredSize;
    }

    public Dimension getMaximumSize() {
            return preferredSize;
    }
}