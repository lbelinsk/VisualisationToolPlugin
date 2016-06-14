import com.intellij.ui.JBColor;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class ThreadLine
{
    private final Thread thread;
    private int row;
    private int rowY;
    private List<Segment> segments = new ArrayList<>();

    private class Segment
    {
        int startX;
        int startY;
        int length;
        Color color;

        Segment(int startX, int startY, int length, ThreadActionType type)
        {
            this.startX = startX;
            this.startY = startY;
            this.length = length;
            switch (type)
            {
                case METHOD: this.color = ChartColors.MethodColor;
                    break;
                case NETWORK: this.color = ChartColors.NetworkColor;
                    break;
                case BLOCKING: this.color = ChartColors.BlockingColor;
                    break;
            }
        }
    }

    ThreadLine(int row, Thread thread)
    {
        this.row = row;
        this.thread = thread;
        updateState();
    }

    void paintThreadLine(Graphics g)
    {
        //Fill threads background
        Color rowBackground = row % 2 == 0 ? ChartColors.EvenLineBackground : ChartColors.OddLineBackground;
        g.setColor(rowBackground);
        g.fillRect(0, rowY - C.LineMargin/2, C.FullWidth, C.LineHeight + C.LineMargin);

        //Write thread's Id (X,Y) = Bottom-Left corner
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.setColor(JBColor.BLACK);
        g.drawString(thread.toString(), 0, rowY + C.LineHeight);

        //Draw all thread actions
        for (Segment s : segments)
        {
            g.setColor(s.color);
            g.fillRect(s.startX, s.startY, s.length, C.LineHeight);
            g.setColor(ChartColors.LineBorder);
            g.drawRect(s.startX, s.startY, s.length, C.LineHeight);
        }
    }

    void updateState()
    {
        segments.clear();
        rowY = C.OriginY + row * (C.LineHeight + C.LineMargin);

        for (ThreadAction action : thread.threadActions)
        {
            int X = (int)Math.round(action.startTime * C.Factor) + C.OriginX;
            int L = (int)Math.round(action.duration * C.Factor);
            segments.add(new Segment(X, rowY, L, action.type));
        }
    }
}
