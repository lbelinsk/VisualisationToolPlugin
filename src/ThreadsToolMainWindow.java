import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ThreadsToolMainWindow extends JFrame
{
    private final ArrayList<UserSession> sessions;
    private JPanel MainPanel;
    private JList<UserSession> SessionsList;
    private JList<UserAction> ActionsList;
    private JLabel centralImageLabel;

    ThreadsToolMainWindow(String title, ArrayList<UserSession> userSessions)
    {
        super(title);
        this.sessions = userSessions;
        super.frameInit();

        DefaultListModel<UserSession> sessionsModel = new DefaultListModel<>();
        initSessionsModel(sessionsModel, this.sessions);
        SessionsList.setModel(sessionsModel);
        SessionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SessionsList.setCellRenderer(new ListRenderer());
        SessionsList.addListSelectionListener(this::sessionSelectionChanged);

        DefaultListModel<UserAction> actionsModel = new DefaultListModel<>();
        ActionsList.setModel(actionsModel);
        ActionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ActionsList.setCellRenderer(new ListRenderer());
        ActionsList.addListSelectionListener(this::actionSelectionChanged);


        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/VisualisationToolIcon.png"));
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initActionsModel(DefaultListModel newModel, List<UserAction> actions)
    {
        actions.forEach(newModel::addElement);
    }

    private void initSessionsModel(DefaultListModel newModel, List<UserSession> sessions)
    {
        sessions.forEach(newModel::addElement);
    }

    private void sessionSelectionChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            DefaultListModel<UserAction> model = (DefaultListModel<UserAction>)ActionsList.getModel();
            model.removeAllElements();
            ActionsList.updateUI();
            if (!SessionsList.isSelectionEmpty())
            {
                UserSession selectedSession = SessionsList.getSelectedValue();
                DefaultListModel<UserAction> newActionsModel = new DefaultListModel<>();
                initActionsModel(newActionsModel, selectedSession.actions);
                ActionsList.setModel(newActionsModel);
            }
        if (ActionsList.getModel().getSize() > 0)
            ActionsList.setSelectedIndex(0);
        }
    }

    private void actionSelectionChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            if (ActionsList.isSelectionEmpty())
            {
                this.centralImageLabel.setIcon(new ImageIcon());
            }
            else
            {
                UserAction selectedAction = ActionsList.getSelectedValue();
                updateCentralWindow(selectedAction);
            }
        }
    }

    private void updateCentralWindow(UserAction selectedAction)
    {
        //setSpecificSize(centralImageLabel, new Dimension(180,320));
        centralImageLabel.setIcon(new ImageIcon(selectedAction.image));
        Border in = BorderFactory.createRaisedBevelBorder();
        Border out = BorderFactory.createEmptyBorder(10,10,10,10);
        centralImageLabel.setBorder(BorderFactory.createCompoundBorder(out,in));
    }

    private void setSpecificSize(JComponent component, Dimension dimension)
    {
        component.setMinimumSize(dimension);
        component.setPreferredSize(dimension);
        component.setMaximumSize(dimension);
    }
}

class ListRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList<?> list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setOpaque(true);
        setIconTextGap(12);
        if (value instanceof UserSession)
        {
            UserSession session = (UserSession) value;
            setText(" " + (index + 1) + ". " + session.toString());
        }
        else if (value instanceof UserAction)
        {
            UserAction action = (UserAction) value;
            setText(" " + (index + 1) + ". " + action.toString());
            //setIcon(action.getSmallIcon());
        }
        return this;
    }
}