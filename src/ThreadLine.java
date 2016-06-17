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

    Segment selectSegment(int pixelX)
    {
        for (int i = segments.size()-1; i>=0; i--)
        {
            Segment seg = segments.get(i);
            if (seg.startX <= pixelX && seg.startX + seg.length >= pixelX)
            {
                seg.isSelected = true;
                return seg;
            }
        }
        return null;
    }

    void deselectSegments()
    {
        for (Segment seg : segments)
        {
            seg.isSelected = false;
        }
    }

    class Segment
    {
        int startX;
        int startY;
        int length;
        Color color;
        boolean isSelected;
        ThreadAction action;

        Segment(int startX, int startY, int length, ThreadAction action)
        {
            this.startX = startX;
            this.startY = startY;
            this.length = length;
            this.action = action;

            switch (action.type)
            {
                case METHOD: this.color = ChartColors.MethodColor;
                    break;
                case NETWORK: this.color = ChartColors.NetworkColor;
                    break;
                case BLOCKING: this.color = ChartColors.BlockingColor;
                    break;
            }
        }

        @Override
        public String toString()
        {
            String sharedString = "Name:    \t" + action.name + "\n" +
                                  "Start:   \t" + action.startTime + "\n" +
                                  "Duration:\t" + action.duration + "\n" +
                                  "End:     \t" + (action.startTime + action.duration);
            switch (action.type)
            {
                case METHOD: return "METHOD\n" + sharedString;
                case NETWORK: return "NETWORK\n" + sharedString + "\n" +
                                     "URL:   \t" + action.Url + action;
                case BLOCKING: return "BLOCKING\n" + sharedString;
            }
            return "";
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
            if (s.isSelected)
            {
                g.setColor(s.color.brighter().brighter());
            }
            else
            {
                g.setColor(s.color);
            }
            g.fillRect(s.startX, s.startY, s.length, C.LineHeight);
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
            segments.add(new Segment(X, rowY, L, action));
        }
    }
}
