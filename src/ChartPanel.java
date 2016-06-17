import com.intellij.ui.JBColor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    static int OriginX;
    static int OriginY;
    static int LastX;
    static Double Factor = 1.0;
}

class ChartColors
{
    static final Color EvenLineBackground = new Color(255, 255, 255);
    static final Color OddLineBackground = new Color(228, 228, 228);
    static final Color MethodColor = new Color(71, 96, 196);
    static final Color BlockingColor = new Color(221,52,39);
    static final Color NetworkColor = new Color(0, 151, 50);
}

class ChartPanel extends JPanel
{
    private List<ThreadLine> threadLineList = new ArrayList<>();
    private int RealDurationMilliSec;
    private JScrollPane scrollPane;
    private int preferredHeight;
    private JTextPane textPane;

    ChartPanel()
    {
        setBackground(new JBColor(new Color(253, 253, 254), new Color(253, 253, 254)));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                selectThreadLine(e.getX(),e.getY());
            }
        });
    }

    private void selectThreadLine(int x, int y)
    {
        deselectAllSegments();
        textPane.setText("");

        int row = y / (C.LineMargin + C.LineHeight);
        int offset = y % (C.LineMargin + C.LineHeight);
        if (offset > C.MarginY &
            offset < C.MarginY + C.LineHeight &&
            row < threadLineList.size())
        {
            ThreadLine.Segment seg = threadLineList.get(row).selectSegment(x);
            if (seg != null)
            {
                textPane.setText(seg.toString());
            }
        }
        repaint();
    }

    private void deselectAllSegments()
    {
        threadLineList.forEach(ThreadLine::deselectSegments);
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
        textPane.setText("");
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

    void updateResized()
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

    void setTextArea(JTextPane threadsTextPane)
    {
        this.textPane = threadsTextPane;
    }
}