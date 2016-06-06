import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ThreadsToolMainWindow extends JFrame
{
    private final ArrayList<UserSession> sessions;
    private ChartPanel chartPanel = new ChartPanel();
    private JPanel MainPanel;
    private JList<UserSession> SessionsList;
    private JList<UserAction> ActionsList;
    private JPanel CentralNorthPanel;
    private JLabel centralImageLabel;
    private JPanel SessionsPanel;
    private JPanel LabelsPanel;
    private JLabel VendorLabel;
    private JLabel OSLabel;
    private JLabel NetworkLabel;
    private JLabel CarrierLabel;
    private JPanel SessionValuesPanel;
    private JPanel SessionTitlesPanel;
    private JPanel ActionsPanel;
    private JPanel ActionTitlesPanel;
    private JPanel ActionValuesPanel;
    private JLabel ContextNameLabel;
    private JLabel ActionNameLabel;
    private JLabel SeqNumLabel;
    private JLabel DurationLabel;
    private JList ThreadsList;
    private JScrollPane ThreadsListScrollPanel;
    private JPanel ThreadsPanel;
    private JPanel CentralBorderPanel;

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

        DefaultListModel<Thread> threadsModel = new DefaultListModel<>();
        ThreadsList.setModel(threadsModel);
        ThreadsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThreadsList.setCellRenderer(new ListRenderer());

        CentralBorderPanel.add(chartPanel);
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/VisualisationToolIcon.png"));
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        setLocationRelativeTo(null);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1500,800));
        setVisible(true);

        if (SessionsList.getModel().getSize() > 0)
            SessionsList.setSelectedIndex(0);
    }

    private void initActionsModel(DefaultListModel newModel, List<UserAction> actions)
    {
        actions.forEach(newModel::addElement);
    }

    private void initSessionsModel(DefaultListModel newModel, List<UserSession> sessions)
    {
        sessions.forEach(newModel::addElement);
    }

    private void initThreadsModel(DefaultListModel newModel, List<Thread> threads)
    {
        threads.forEach(newModel::addElement);
    }

    private void sessionSelectionChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            DefaultListModel<UserAction> model = (DefaultListModel<UserAction>)ActionsList.getModel();
            model.removeAllElements();
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
                updateCentralWindow(null, null);
            }
            else
            {
                UserAction selectedAction = ActionsList.getSelectedValue();
                UserSession selectedSession = SessionsList.getSelectedValue();
                updateCentralWindow(selectedAction, selectedSession);
            }
        }
    }

    private void updateCentralWindow(UserAction selectedAction, UserSession selectedSession)
    {
        if (selectedAction == null || selectedSession == null){
            return;
        }
        updateUpperPart(selectedAction, selectedSession);
        updateGraphPart(selectedAction);
    }

    private void updateUpperPart(UserAction selectedAction, UserSession selectedSession)
    {
        //TODO: consider setting the borders once in constructor
        centralImageLabel.setIcon(new ImageIcon(selectedAction.image));
        Border in = BorderFactory.createRaisedBevelBorder();
        Border out = BorderFactory.createEmptyBorder(10,10,10,10);
        centralImageLabel.setBorder(BorderFactory.createCompoundBorder(out,in));
        ThreadsPanel.setBorder(BorderFactory.createCompoundBorder(out,in));

        Border out2 = BorderFactory.createEmptyBorder(10,10,10,0);
        LabelsPanel.setBorder(BorderFactory.createCompoundBorder(out2, in));
        SessionTitlesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        SessionValuesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,20));
        ActionTitlesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        ActionValuesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setLabel(VendorLabel, selectedSession.vendor + " " + selectedSession.model);
        setLabel(OSLabel, selectedSession.OSName + " " + selectedSession.OSVer);
        setLabel(NetworkLabel, selectedSession.netType);
        setLabel(CarrierLabel, selectedSession.carrier);

        setLabel(ContextNameLabel, selectedAction.ctxName);
        setLabel(ActionNameLabel, selectedAction.name);
        setLabel(SeqNumLabel, String.valueOf(selectedAction.uaSeq));
        setLabel(DurationLabel, String.valueOf(selectedAction.duration));
    }

    private void setLabel(JLabel Label, String text)
    {
        if(text.compareTo("")!= 0)
            Label.setText(text);
        else
            Label.setText("Unknown");
    }

    private void updateGraphPart(UserAction selectedAction)
    {
        DefaultListModel<Thread> threadModel = new DefaultListModel<>();
        initThreadsModel(threadModel, selectedAction.threads);
        ThreadsList.setModel(threadModel);
        pack();
        chartPanel.updateChart(selectedAction.threads);
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