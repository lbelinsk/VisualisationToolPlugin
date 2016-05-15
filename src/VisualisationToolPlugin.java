import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.jcraft.jsch.Session;
import org.json.*;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class VisualisationToolPlugin extends AnAction
{
    private List<JsonFile> jsonFilesList;
    private ArrayList<UserSession> sessionsList;

    public VisualisationToolPlugin()
    {
        initJsonFilesList();
        initSessionsList();
    }

    private void initJsonFilesList()
    {
        this.jsonFilesList = new ArrayList<>();
        String jsonString;
        //TODO: dynamically find a relevant path. Alert if no jsons found
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
                    JsonFile newJsonFile = new JsonFile();
                    initJsonFile(newJsonFile, jsonObject, dir);
                    jsonFilesList.add(newJsonFile);
                }
                else {
                   System.out.println(filePath + "not readable");
                }
            }
        }
        catch (IOException x){
            System.err.format("IOException: %s%n", x);
        }
    }

    private void initJsonFile(JsonFile newJsonFile, JSONObject jsonObject, Path dir)
    {
        newJsonFile.model = jsonObject.getString("model");
        newJsonFile.osName = jsonObject.getString("osname");
        newJsonFile.sessionStartTime = jsonObject.getLong("session_start_time");

        JSONArray arr = jsonObject.getJSONArray("actions");
        for (int i=0; i<arr.length(); i++)
        {
            JSONObject obj = arr.getJSONObject(i);
            UserAction newAction = new UserAction();

            newAction.name = obj.getString("action_name");
            newAction.id = obj.getLong("actionid");
            newAction.duration = obj.getInt("duration");
            newAction.uaSeq = obj.getInt("ua_seq");
            newAction.startTime = obj.getLong("starttime");
            newAction.ctxName = obj.getString("ctx_name");
            newAction.info = obj.getString("threadsinfo");

            Path imagePath = dir.resolve(String.valueOf(newAction.id) + ".png");
            if (Files.exists(imagePath))
                newAction.imagePath = imagePath;
            else
                newAction.imagePath = null;

            newJsonFile.actions.add(newAction);
        }
    }

    private void initSessionsList()
    {
        this.sessionsList = new ArrayList<>();
        if (jsonFilesList.isEmpty())
            return;

        Collections.sort(jsonFilesList);
        JsonFile []allJsonFiles = new JsonFile[jsonFilesList.size()];
        jsonFilesList.toArray(allJsonFiles);

        for (int i = 0; i < allJsonFiles.length; i++)
        {
            if (i != 0 && isSameSession(allJsonFiles[i], allJsonFiles[i-1]))
            {
                UserSession lastAddedSession = sessionsList.get(sessionsList.size()-1);
                lastAddedSession.mergeWith(allJsonFiles[i].actions);
            }
            else
            {
                sessionsList.add(new UserSession(allJsonFiles[i]));
            }
        }
    }

    private boolean isSameSession(JsonFile file1, JsonFile file2)
    {
        return (file1.compareTo(file2) == 0);
    }

    @Override
    public void actionPerformed(AnActionEvent event)
    {
        new ThreadsToolMainWindow("Threads Visualisation Tool", sessionsList);
    }
}

