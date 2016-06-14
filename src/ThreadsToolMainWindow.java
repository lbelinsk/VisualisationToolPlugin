import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;

public class ThreadsToolMainWindow extends JFrame
{
    private final ArrayList<UserSession> sessions;
    private final int borderMargin = 10;
    private ChartPanel chartPanel = new ChartPanel();
    private JPanel MainPanel;
    private JList<UserSession> SessionsList;
    private JList<UserAction> ActionsList;
    private JPanel CentralNorthPanel;
    private JLabel centralImageLabel;
    private JPanel SessionsPanel;
    private JPanel LabelsPanel;
    private JLabel DeviceLabel;
    private JLabel OSLabel;
    private JLabel NetworkLabel;
    private JPanel SessionValuesPanel;
    private JPanel SessionTitlesPanel;
    private JPanel ActionsPanel;
    private JPanel ActionTitlesPanel;
    private JPanel ActionValuesPanel;
    private JLabel ContextNameLabel;
    private JLabel ActionNameLabel;
    private JLabel DurationLabel;
    private JPanel CentralBorderPanel;

    ThreadsToolMainWindow(String title, ArrayList<UserSession> userSessions)
    {
        super(title);
        this.sessions = userSessions;
        super.frameInit();

        initSelectionLists();
        initSessionsModel();
        ActionsList.setModel(new DefaultListModel<>());

        initLegendPanel();
        initChartScrollPane();
        setBorders();

        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/VisualisationToolIcon.png"));
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        setLocationRelativeTo(null);
        //setMinimumSize(new Dimension(1500,800));
        setVisible(true);

        if (SessionsList.getModel().getSize() > 0)
            SessionsList.setSelectedIndex(0);
    }

    private void initChartScrollPane()
    {
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setViewportView(chartPanel);
        chartPanel.setScrollPane(scrollPane);
        Border in = BorderFactory.createLineBorder(JBColor.BLACK);
        Border out = BorderFactory.createEmptyBorder(0,borderMargin,0,borderMargin);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(out,in));
        scrollPane.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                super.componentResized(e);
                chartPanel.updateResized();
            }
        });
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        CentralBorderPanel.add(scrollPane);
    }

    private void initSelectionLists()
    {
        SessionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SessionsList.setCellRenderer(new ListRenderer());
        SessionsList.addListSelectionListener(this::sessionSelectionChanged);

        ActionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ActionsList.setCellRenderer(new ListRenderer());
        ActionsList.addListSelectionListener(this::actionSelectionChanged);
    }

    private void initLegendPanel()
    {
        int sideSize = 15;
        JLabel title = new JLabel(" Thread action types: ");
        title.setBorder(new EmptyBorder(0,10,0,10));
        RectangleComponent methodRect = new RectangleComponent("Method ", ChartColors.MethodColor, sideSize);
        RectangleComponent networkRect = new RectangleComponent("Network ", ChartColors.NetworkColor, sideSize);
        RectangleComponent blockingRect = new RectangleComponent("Blocking ", ChartColors.BlockingColor, sideSize);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(title);
        panel.add(methodRect);
        panel.add(networkRect);
        panel.add(blockingRect);
        panel.add(Box.createHorizontalGlue());
        CentralNorthPanel.add(panel, BorderLayout.SOUTH);
    }

    private void initActionsModel(DefaultListModel newModel, List<UserAction> actions)
    {
        actions.forEach(newModel::addElement);
    }

    private void initSessionsModel()
    {
        DefaultListModel<UserSession> sessionsModel = new DefaultListModel<>();
        sessions.forEach(sessionsModel::addElement);
        SessionsList.setModel(sessionsModel);
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
        updateChartPart(selectedAction);
    }

    private void updateUpperPart(UserAction selectedAction, UserSession selectedSession)
    {
        centralImageLabel.setIcon(new ImageIcon(selectedAction.image));

        setLabel(DeviceLabel, selectedSession.vendor + " " + selectedSession.model);
        setLabel(OSLabel, selectedSession.OSName + " " + selectedSession.OSVer);
        setLabel(NetworkLabel, selectedSession.netType);

        setLabel(ContextNameLabel, selectedAction.ctxName);
        setLabel(ActionNameLabel, selectedAction.name);
        setLabel(DurationLabel, String.valueOf(selectedAction.duration));
    }

    private void setLabel(JLabel Label, String text)
    {
        if(text.compareTo("")!= 0)
            Label.setText(text);
        else
            Label.setText("Unknown");
    }

    private void updateChartPart(UserAction selectedAction)
    {
        chartPanel.updateChart(selectedAction.threads);
    }

    private void setBorders()
    {
        Border in = BorderFactory.createRaisedBevelBorder();
        Border out = BorderFactory.createEmptyBorder(10,10,10,10);
        centralImageLabel.setBorder(BorderFactory.createCompoundBorder(out,in));

        Border out2 = BorderFactory.createEmptyBorder(10,10,10,0);
        LabelsPanel.setBorder(BorderFactory.createCompoundBorder(out2, in));

        SessionTitlesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        SessionValuesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,20));
        ActionTitlesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        ActionValuesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
}

