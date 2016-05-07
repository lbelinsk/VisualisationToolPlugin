import javax.swing.*;

/**
 * Created by Sandro on 5/5/2016.
 */
public class ThreadsToolMainWindow extends JFrame
{
    private JPanel MainGrid;
    private JButton MyButton;

    public ThreadsToolMainWindow()
    {
        super.frameInit();
        setContentPane(MainGrid);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
