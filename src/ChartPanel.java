import com.intellij.ui.JBColor;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

//Chart-Const
class C
{
    static int RealWidth;
    static final int MarginX = 40;
    static final int MarginY = 20;
    static final int BorderMargin = 10;
    static int OriginX;
    static int OriginY;
    static int LastX;
    static final int LineHeight = 18;
    static final int LineMargin = 8;
    static final int LineRadius = 5;
    static final Color LineFill = JBColor.BLUE;
    static final Color LineBorder = JBColor.BLACK;
    static Double Factor = 1.0;
}

class ChartPanel extends JPanel
{
    private List<ThreadLine> threadLineList = new ArrayList<>();
    private int RealDurationMilliSec;
    private JScrollPane scrollPane;
    private int preferredHeight;

    ChartPanel() { }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for (ThreadLine line: threadLineList)
        {
            line.paintThreadLine(g);
        }
    }

    void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    void updateChart(List<Thread> threads)
    {
        int RealDuration = Integer.MIN_VALUE;
        for (Thread thread : threads)
        {
            if (thread.latestEnd > RealDuration)
                RealDuration = thread.latestEnd;
        }
        RealDurationMilliSec = RealDuration;
        preferredHeight = threads.size() * (C.LineHeight + C.LineMargin) + 2 * C.MarginY;

        updateChartDimension();
        fillThreadsList(threads);
        revalidate();
        repaint();
    }

    private void fillThreadsList(List<Thread> threads)
    {
        threadLineList.clear();
        for (int i = 0; i < threads.size(); i++)
        {
            ThreadLine newThreadLine = new ThreadLine(i, threads.get(i));
            threadLineList.add(newThreadLine);
        }
    }

    public void updateResized()
    {
        updateChartDimension();
        updateThreadLines();
    }

    private void updateChartDimension()
    {
        Dimension scrollDim = this.scrollPane.getSize();
        C.RealWidth = scrollDim.width - 2*C.BorderMargin - 2*C.MarginX;
        C.OriginX = C.BorderMargin + C.MarginX;
        C.OriginY = C.BorderMargin + C.MarginY;
        C.LastX = C.OriginX + C.RealWidth;
        C.Factor = (double)C.RealWidth / RealDurationMilliSec;
        setPreferredSize(new Dimension(C.RealWidth, this.preferredHeight));
    }

    private void updateThreadLines()
    {
        threadLineList.forEach(ThreadLine::updateState);
        revalidate();
        repaint();
    }
}


class ThreadLine
{
    private final Thread thread;
    private int row;
    private List<Segment> segments = new ArrayList<>();

    private class Segment
    {
        int startX;
        int startY;
        int length;

        Segment(int startX, int startY, int length)
        {
            this.startX = startX;
            this.startY = startY;
            this.length = length;
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
        for (Segment s : segments)
        {
            g.setColor(C.LineFill);
            g.fillRoundRect(s.startX, s.startY, s.length, C.LineHeight, C.LineRadius, C.LineRadius);
            g.setColor(C.LineBorder);
            g.drawRoundRect(s.startX, s.startY, s.length, C.LineHeight, C.LineRadius, C.LineRadius);
        }
    }

    public void updateState()
    {
        segments.clear();
        int Y = C.OriginY + row * (C.LineHeight + C.LineMargin);

        for (ThreadAction action : thread.threadActions)
        {
            int X = (int)Math.round(action.startTime * C.Factor) + C.OriginX;
            int L = (int)Math.round(action.duration * C.Factor);
            segments.add(new Segment(X, Y, L));
        }
    }
}
