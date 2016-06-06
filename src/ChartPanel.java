import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class C
{
    static int RealWidth;
    static int RealHeight;
    static final int Margin = 10;
    static final int BorderMargin = 10;
    static int OriginX;
    static int OriginY;
    static int LastX;
    static int LastY;
    static final int LineHeight = 18;
    static final int LineMargin = 8;
    static final int LineRadius = 5;
    static final Color LineFill = JBColor.BLUE;
    static final Color LineBorder = JBColor.BLACK;
    static Double Factor = 1.0;
}

class ChartPanel extends JPanel
{
    private RedSquare redMovingSquare = new RedSquare();
    private List<ThreadLine> threadLineList = new ArrayList<>();
    private int RealDurationMilliSec = 1;

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        redMovingSquare.paintRedSquare(g);

        for (ThreadLine line: threadLineList)
        {
            line.paintThreadLine(g);
        }
    }

    ChartPanel()
    {
        initBorders();

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                moveSquare(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                moveSquare(e.getX(), e.getY());
            }
        });

        addMouseWheelListener(new MouseAdapter()
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                resizeSquare(e.getWheelRotation());
            }
        });

        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                super.componentResized(e);
                updateChartDimension(RealDurationMilliSec);
            }
        });

    }

    private void initBorders()
    {
        Border in = BorderFactory.createLineBorder(JBColor.BLACK);
        Border out = BorderFactory.createEmptyBorder(C.Margin, C.Margin, C.Margin,0);
        setBorder(BorderFactory.createCompoundBorder(out,in));
        setBorder(BorderFactory.createCompoundBorder(out,in));
    }

    void updateChart(List<Thread> threads)
    {
        threadLineList.clear();
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        for (Thread thread : threads)
        {
            if (thread.earliestStart < start)
                start = thread.earliestStart;
            if (thread.latestEnd > end)
                end = thread.latestEnd;
        }
        RealDurationMilliSec = end;

        updateChartDimension(RealDurationMilliSec);
        for (int i = 0; i < threads.size(); i++)
        {
            ThreadLine newThreadLine = new ThreadLine(i, threads.get(i));
            threadLineList.add(newThreadLine);
        }
        repaint();
    }

    private void updateChartDimension(int realDurationMilliSec)
    {
        C.RealWidth = getSize().width - C.BorderMargin - 2*C.Margin;
        C.RealHeight = getSize().height - C.BorderMargin - 2* C.Margin;
        C.OriginX = C.BorderMargin + C.Margin;
        C.OriginY = C.BorderMargin + C.Margin;
        C.LastX = C.OriginX + C.RealWidth;
        C.LastY = C.OriginY + C.RealHeight;
        C.Factor = (double)C.RealWidth / realDurationMilliSec;
    }

    private void resizeSquare(int wheelRotation)
    {
        final int CURR_X = redMovingSquare.getX();
        final int CURR_Y = redMovingSquare.getY();
        final int CURR_W = redMovingSquare.getWidth();
        final int OFFSET = 1;

        repaint(CURR_X, CURR_Y, CURR_W + OFFSET , CURR_W + OFFSET);

        int newWidth = CURR_W + wheelRotation;
        newWidth = newWidth < 5 ? 5 : newWidth > 100 ? 100 : newWidth;
        redMovingSquare.setWidth(newWidth);

        repaint(redMovingSquare.getX(), redMovingSquare.getY(),
                redMovingSquare.getWidth() + OFFSET,
                redMovingSquare.getWidth() + OFFSET);
    }

    private void moveSquare(int x, int y)
    {
        final int CURR_X = redMovingSquare.getX();
        final int CURR_Y = redMovingSquare.getY();
        final int CURR_W = redMovingSquare.getWidth();
        final int OFFSET = 1;

        if ((CURR_X!=x) || (CURR_Y!=y))
        {
            repaint(CURR_X, CURR_Y, CURR_W + OFFSET, CURR_W + OFFSET);
            int newX = x < C.OriginX ? C.OriginX : x > C.LastX - CURR_W ? C.LastX - CURR_W : x;
            int newY = y < C.OriginY ? C.OriginY : y > C.LastY - CURR_W ? C.LastY - CURR_W : y;
            redMovingSquare.setX(newX);
            redMovingSquare.setY(newY);

            repaint(redMovingSquare.getX(), redMovingSquare.getY(),
                    redMovingSquare.getWidth() + OFFSET,
                    redMovingSquare.getWidth() + OFFSET);
        }
    }


}

class RedSquare
{
    private int xPos = 50;
    private int yPos = 50;
    private int width = 20;

    void setX(int xPos){
        this.xPos = xPos;
    }

    int getX(){
        return xPos;
    }

    void setY(int yPos){
        this.yPos = yPos;
    }

    int getY(){
        return yPos;
    }

    void setWidth(int width)
    {
        this.width = width;
    }

    int getWidth(){
        return width;
    }


    void paintRedSquare(Graphics g)
    {
        g.setColor(JBColor.RED);
        g.fillRect(xPos, yPos, width, width);
        g.setColor(JBColor.BLACK);
        g.drawRect(xPos, yPos, width, width);
    }
}

class ThreadLine
{
    private final Thread thread;
    private int startX;
    private int startY;
    private int row;

    private class Segment
    {
        Segment(int startX, int length)
        {
            this.startX = startX;
            this.length = length;
        }

        int startX;
        int length;
    }

    private List<Segment> segments = new ArrayList<>();

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
            g.fillRoundRect(s.startX, startY, s.length, C.LineHeight, C.LineRadius, C.LineRadius);
            g.setColor(C.LineBorder);
            g.drawRoundRect(s.startX, startY, s.length, C.LineHeight, C.LineRadius, C.LineRadius);
        }
    }

    private void updateState()
    {
        startX = C.OriginX;
        startY = C.OriginY + row * (C.LineHeight + C.LineMargin);

        for (ThreadAction action : thread.threadActions)
        {
            int X = (int)Math.round(action.startTime * C.Factor);
            int L = (int)Math.round(action.duration * C.Factor);
            segments.add(new Segment(X, L));
        }
    }
}