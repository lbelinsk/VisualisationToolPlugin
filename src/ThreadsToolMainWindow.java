import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Sandro on 5/5/2016.
 */
public class ThreadsToolMainWindow extends JFrame
{
    private final ArrayList<UserSession> sessions;
    private JPanel MainPanel;
    private JList<UserSession> SessionsList;
    private DefaultListModel<UserSession> SessionsModel;

//    private JPanel LeftMenu;
//    private JTextArea RightMenu;

    public ThreadsToolMainWindow(String title, ArrayList<UserSession> userSessions)
    {
        super(title);
        this.sessions = userSessions;
        super.frameInit();

        SessionsModel = new DefaultListModel<>();
        initSessionsModel();
        SessionsList.setModel(SessionsModel);
        SessionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SessionsList.setCellRenderer(new SessionRenderer());


//        // set Layout manager
//        MainPanel.setLayout(new BorderLayout());
//
//        initRightMenu();
//        initLeftMenu(sessions);
//
//        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL, 30, 20, 0, 300);
//        scrollBar.setUnitIncrement(2);
//        MainPanel.add(scrollBar, BorderLayout.CENTER);
//
//
//        // Add component to content pane
//        MainPanel.add(RightMenu, BorderLayout.CENTER);
//        MainPanel.add(LeftMenu, BorderLayout.WEST);


        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/VisualisationToolIcon.png"));
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initSessionsModel()
    {
        for (UserSession session :
                this.sessions)
        {
            SessionsModel.addElement(session);
        }
    }

//    void initRightMenu()
//    {
//        RightMenu = new JTextArea();
//        RightMenu.setBackground(Color.WHITE);
//        RightMenu.setFont(RightMenu.getFont().deriveFont(16f));
//        RightMenu.setForeground(Color.BLACK);
//    }
//
//    void initLeftMenu(ArrayList<UserSession> sessions)
//    {
//        LeftMenu = new JPanel();
//        Dimension d = LeftMenu.getPreferredSize();
//        d.width = 400;
//        LeftMenu.setPreferredSize(d);
//        LeftMenu.setBorder(BorderFactory.createTitledBorder("Sessions"));
//        LeftMenu.setBackground(Color.getColor("658017"));
//
//        LeftMenu.setLayout(new GridBagLayout());
//        GridBagConstraints gc = new GridBagConstraints();
//
//        JButton buttons[];
//        buttons = new JButton[sessions.size() + 1];
//        gc.weightx = 0.5;
//        gc.weighty = 0.5;
//        gc.gridx = 0;
//        gc.gridy = 0;
//
//        for ( int i = sessions.size();  i > 0; --i) {
//            buttons[i] = new JButton("Session #" + i);
//            buttons[i].setBackground(Color.WHITE);
//            buttons[i].setForeground(Color.WHITE);
//            buttons[i].setBorderPainted(true);
//            Dimension d2 = buttons[i].getPreferredSize();
//            d2.width = 50;
//            buttons[i].setSize(d2);
//            final int j = i;
//            buttons[i].addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    RightMenu.append("ShowThread Info Of Session #" + String.valueOf(j) + "\n");
//                }
//            });
//            LeftMenu.add(buttons[i], gc);
//            gc.gridy += 1;
//        }
//    }
}

class SessionRenderer extends JLabel implements ListCellRenderer {

    public SessionRenderer() {
        setOpaque(true);
        setIconTextGap(12);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        UserSession entry = (UserSession) value;
        setText(entry.toString());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}

//public class IngredientListCellRenderer extends DefaultListCellRenderer {
//    public Component getListCellRendererComponent(JList<?> list,
//                                                  Object value,
//                                                  int index,
//                                                  boolean isSelected,
//                                                  boolean cellHasFocus) {
//        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        if (value instanceof Ingredient) {
//            Ingredient ingredient = (Ingredient)value;
//            setText(ingredient.getName());
//            setToolTipText(ingredient.getDescription());
//            // setIcon(ingredient.getIcon());
//        }
//        return this;
//    }
//}