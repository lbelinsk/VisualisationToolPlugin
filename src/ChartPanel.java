import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.*;
import java.util.List;

class Chart {
    static int RealWidth;
    static int RealHeight;
    static final int Margin = 10;
    static int OriginX;
    static int OriginY;
    static int LastX;
    static int LastY;
    public static final int LineHeight = 50;
    public static final int LineMargin = 25;
}

class ChartPanel extends JPanel
{
    private RedSquare redMovingSquare = new RedSquare();
    //List<ThreadLine> threadLineList = new ArrayList<>();
    //private final int yStartPoint = 20;

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        redMovingSquare.paintRedSquare(g);

//        for (ThreadLine line: threadLineList)
//        {
//            int lineYPosition = yStartPoint + ChartLine.Height + ChartLine.Margin
//            line.paintRedSquare(g, yStartPoint + ChartLine.Height + ChartLine.Margin);
//        }

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
    }

    private void initBorders()
    {
        Border in = BorderFactory.createLineBorder(JBColor.BLACK);
        Border out = BorderFactory.createEmptyBorder(Chart.Margin,Chart.Margin,Chart.Margin,0);
        setBorder(BorderFactory.createCompoundBorder(out,in));
        setBorder(BorderFactory.createCompoundBorder(out,in));

    }

    void updateChart(List<Thread> threads)
    {
        Chart.OriginX = Chart.Margin;
        Chart.OriginY = Chart.Margin;
        Chart.RealWidth = getSize().width - Chart.Margin;
        Chart.RealHeight = getSize().height - 2*Chart.Margin;
        Chart.LastX = Chart.OriginX + Chart.RealWidth;
        Chart.LastY = Chart.OriginY + Chart.RealHeight;
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
            int newX = x < Chart.OriginX ? Chart.OriginX : x > Chart.LastX - CURR_W ? Chart.LastX - CURR_W : x;
            int newY = y < Chart.OriginY ? Chart.OriginY : y > Chart.LastY - CURR_W ? Chart.LastY - CURR_W : y;
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
    private int xPos = 50;
    private int yPos = 50;
    private int width = 20;

    public void setX(int xPos){
        this.xPos = xPos;
    }

    public int getX(){
        return xPos;
    }

    public void setY(int yPos){
        this.yPos = yPos;
    }

    public int getY(){
        return yPos;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getWidth(){
        return width;
    }


    public void paintThreadLine(Graphics g){
        g.setColor(JBColor.RED);
        g.fillRect(xPos,yPos,width, width);
        g.setColor(JBColor.BLACK);
        g.drawRect(xPos,yPos,width, width);
    }
}