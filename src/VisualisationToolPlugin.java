import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.json.*;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sandro on 4/30/2016.
 */
public class VisualisationToolPlugin extends AnAction
{
    private List<ThreadInfo> threadsList;

    public VisualisationToolPlugin()
    {
        threadsList = new ArrayList<>();
        initThreadsList();
    }

    private void initThreadsList()
    {
        String jsonString;
        Path dir = Paths.get(System.getProperty("user.home"),
                "IdeaProjects",
                "VisualisationToolPlugin",
                "temp",
                "com.kayak.android");

        try (DirectoryStream<Path> stream =
             Files.newDirectoryStream(dir, "*.json")) {
            for (Path filePath: stream)
            {
                if (Files.isReadable(filePath))
                {
                    jsonString = new String(Files.readAllBytes(filePath));
                    JSONObject jsonObject = new JSONObject(jsonString);
                    ThreadInfo newThread = new ThreadInfo();
                    initThread(newThread, jsonObject);
                    threadsList.add(newThread);
                }
            }
        }
        catch (IOException x){
            System.err.format("IOException: %s%n", x);
        }
    }

    private void initThread(ThreadInfo newThread, JSONObject jsonObject)
    {
        newThread.model = jsonObject.getString("model");
        newThread.osName = jsonObject.getString("osname");
        newThread.sessionStartTime = jsonObject.getLong("session_start_time");

        JSONArray arr = jsonObject.getJSONArray("actions");
        for (int i=0; i<arr.length(); i++)
        {
            JSONObject obj = arr.getJSONObject(i);
            ThreadAction newAction = new ThreadAction();

            newAction.name = obj.getString("action_name");
            newAction.id = obj.getLong("actionid");
            newAction.duration = obj.getInt("duration");
            newAction.uaSeq = obj.getInt("ua_seq");
            newAction.startTime = obj.getLong("starttime");
            newAction.ctxName = obj.getString("ctx_name");
            newAction.info = obj.getString("threadsinfo");

            newThread.actions.add(newAction);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event)
    {
        ThreadsToolMainWindow mainWindow = new ThreadsToolMainWindow();

    }
}
