import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

//Chart-Const
class C
{
    static int ChartWidth;
    static int FullWidth;
    static final int LineHeight = 20;
    static final int LineMargin = 15;
    static final int MarginX = 40;
    static final int MarginY = LineMargin/2;
    static final int BorderMargin = 10;
    static int OriginX;
    static int OriginY;
    static int LastX;
    static final Color LineBorder = JBColor.BLACK;
    static Double Factor = 1.0;
    static Color EvenLineBackground = Color.WHITE;
    static Color OddLineBackground = Gray._228;
}

class ChartPanel extends JPanel
{
    private List<ThreadLine> threadLineList = new ArrayList<>();
    private int RealDurationMilliSec;
    private JScrollPane scrollPane;
    private int preferredHeight;

    ChartPanel() {
        setBackground(new JBColor(new Color(253, 253, 254), new Color(253, 253, 254)));
    }

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
        C.FullWidth = scrollDim.width;
        C.ChartWidth = C.FullWidth - 2* C.MarginX;
        C.OriginX = C.MarginX;
        C.OriginY = C.MarginY;
        C.LastX = C.OriginX + C.ChartWidth;
        C.Factor = (double)C.ChartWidth / RealDurationMilliSec;
        setPreferredSize(new Dimension(C.ChartWidth, this.preferredHeight));
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
                case METHOD: this.color = JBColor.BLUE;
                    break;
                case NETWORK: this.color = JBColor.CYAN;
                    break;
                case BLOCKING: this.color = JBColor.RED;
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
        Color rowBackground = row % 2 == 0 ? C.EvenLineBackground : C.OddLineBackground;
        g.setColor(rowBackground);
        g.fillRect(0, rowY - C.LineMargin/2, C.FullWidth, C.LineHeight + C.LineMargin);

        //Write thread's Id (X,Y) = Bottom-Left corner
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.setColor(JBColor.BLACK);
        g.drawString(thread.toString(), 0, rowY + C.LineHeight);

        for (Segment s : segments)
        {
            g.setColor(s.color);
            g.fillRect(s.startX, s.startY, s.length, C.LineHeight);
            g.setColor(C.LineBorder);
            g.drawRect(s.startX, s.startY, s.length, C.LineHeight);
        }
    }

    public void updateState()
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
