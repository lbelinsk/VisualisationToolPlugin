import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by Sandro on 4/30/2016.
 */
public class VisualisationToolPlugin extends AnAction
{

    @Override
    public void actionPerformed(AnActionEvent event)
    {
        ThreadsToolMainWindow mainWindow = new ThreadsToolMainWindow();
    }
}
