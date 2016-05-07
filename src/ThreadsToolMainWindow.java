import javax.swing.*;
import java.util.List;

/**
 * Created by Sandro on 5/5/2016.
 */
public class ThreadsToolMainWindow extends JFrame
{
    private final List<ThreadInfo> threadsList;
    private JPanel MainPanel;


    public ThreadsToolMainWindow(String title, List<ThreadInfo> threadsList)
    {
        super(title);
        this.threadsList = threadsList;
        super.frameInit();
        //change the java icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/VisualisationToolIcon.png"));
        setIconImage(icon.getImage());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
