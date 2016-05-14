import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by Sandro on 5/5/2016.
 */
public class ThreadsToolMainWindow extends JFrame
{
    private final List <List<ThreadInfo> > sessions;
    private JPanel MainPanel;
    private JPanel LeftMenue;
    private JTextArea RightMenue;

    public ThreadsToolMainWindow(String title, List<List<ThreadInfo> > sessions)
    {
        super(title);
        this.sessions = sessions;
        super.frameInit();

        // set Layout manager
        MainPanel.setLayout(new BorderLayout());

        initRightMenue();
        initLeftMenue(sessions);

  /*    JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL, 30, 20, 0, 300);
        scrollBar.setUnitIncrement(2);
        MainPanel.add(scrollBar, BorderLayout.CENTER);
  */

        // Add component to content pane
        MainPanel.add(RightMenue, BorderLayout.CENTER);
        MainPanel.add(LeftMenue, BorderLayout.WEST);

        //change the java icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/VisualisationToolIcon.png"));
        setIconImage(icon.getImage());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void initRightMenue()
    {
        RightMenue = new JTextArea();
        RightMenue.setBackground(Color.WHITE);
        RightMenue.setFont(RightMenue.getFont().deriveFont(16f));
        RightMenue.setForeground(Color.BLACK);
    }

    void initLeftMenue(List<List<ThreadInfo> > sessions)
    {
        LeftMenue = new JPanel();
        Dimension d = LeftMenue.getPreferredSize();
        d.width = 400;
        LeftMenue.setPreferredSize(d);
        LeftMenue.setBorder(BorderFactory.createTitledBorder("Sessions"));
        LeftMenue.setBackground(Color.getColor("658017"));

        LeftMenue.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        JButton buttons[];
        buttons = new JButton[sessions.size() + 1];
        gc.weightx = 0.5;
        gc.weighty = 0.5;
        gc.gridx = 0;
        gc.gridy = 0;

        for ( int i = sessions.size();  i > 0; --i) {
            buttons[i] = new JButton("Session #" + i);
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setBorderPainted(true);
            Dimension d2 = buttons[i].getPreferredSize();
            d2.width = 50;
            buttons[i].setSize(d2);
            final int j = i;
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RightMenue.append("ShowThread Info Of Session #" + String.valueOf(j) + "\n");
                }
            });
            LeftMenue.add(buttons[i], gc);
            gc.gridy += 1;
        }
    }
}
