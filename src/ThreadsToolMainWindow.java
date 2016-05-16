import javax.swing.*;
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
    private DefaultListModel<UserSession> SessionsModel;
    private DefaultListModel<UserAction> ActionsModel;

    ThreadsToolMainWindow(String title, ArrayList<UserSession> userSessions)
    {
        super(title);
        this.sessions = userSessions;
        super.frameInit();

        SessionsModel = new DefaultListModel<>();
        initSessionsModel();
        SessionsList.setModel(SessionsModel);
        SessionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SessionsList.setCellRenderer(new ListRenderer());
        SessionsList.addListSelectionListener(this::sessionSelectionChanged);


        ActionsModel = new DefaultListModel<>();
        ActionsList.setModel(ActionsModel);
        ActionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ActionsList.setCellRenderer(new ListRenderer());


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

    private void initActionsModel(List<UserAction> actions)
    {
        for (UserAction action :
                actions)
        {
            ActionsModel.addElement(action);
        }
    }

    private void sessionSelectionChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            ActionsModel.clear();
            if (!SessionsList.isSelectionEmpty())
            {
                UserSession selectedSession = SessionsList.getSelectedValue();
                initActionsModel(selectedSession.actions);
            }
        }
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
            setText((index + 1) + ". " + session.toString());
        }
        else if (value instanceof UserAction)
        {
            UserAction action = (UserAction) value;
            setText((index + 1) + ". " + action.toString());
            setIcon(action.getSmallIcon());
        }
        return this;
    }
}