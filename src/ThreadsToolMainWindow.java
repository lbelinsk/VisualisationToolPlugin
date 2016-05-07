import javax.swing.*;

/**
 * Created by Sandro on 5/5/2016.
 */
public class ThreadsToolMainWindow extends JFrame
{
    private JPanel MainGrid;

    public ThreadsToolMainWindow()
    {
        super.frameInit();
        setContentPane(MainGrid);
        pack();

        //change the java icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/VisualisationToolIcon.png"));
        setIconImage(icon.getImage());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
