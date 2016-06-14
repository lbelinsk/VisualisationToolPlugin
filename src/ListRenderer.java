import javax.swing.*;
import java.awt.*;

class ListRenderer extends DefaultListCellRenderer
{
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
