import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.structuralsearch.plugin.ui.UIUtil;
import org.json.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    @Override
    public void actionPerformed(AnActionEvent event)
    {
        initJsonFilesList();
        initSessionsList();
        new ThreadsToolMainWindow("Threads Visualisation Tool", sessionsList);
    }

    private void initJsonFilesList()
    {
        this.jsonFilesList = new ArrayList<>();
        JFileChooser dirChooser;
        String jsonString;
        Path dir;
        //TODO: dynamically find a relevant path. Alert if no jsons found
        dir = Paths.get(System.getProperty("user.home"),
                "IdeaProjects",
                "VisualisationToolPlugin",
                "temp",
                "com.hp.advantage");

//        dirChooser = new JFileChooser();
//        dirChooser.setCurrentDirectory(new java.io.File("."));
//        dirChooser.setDialogTitle("Choose directory with Json files");
//        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        dirChooser.setAcceptAllFileFilterUsed(false);
//        if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            dir = Paths.get(dirChooser.getSelectedFile().getAbsolutePath());
//            //dirChooser.getCurrentDirectory())
//        }
//        else {
//            System.out.println("No Selection ");
//            return;
//        }

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
        catch (JSONException e)
        {
            JOptionPane.showMessageDialog(new JFrame(),
                    e.getMessage() + "\nThe visualization may be incorrect.",
                    "Error parsing a Json file",
                    JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException x){
            System.err.format("IOException: %s%n", x);
        }
    }

    private void initJsonFile(JsonFile newJsonFile, JSONObject jsonObject, Path dir)
    {
        //TODO: consider using jsonObject.get functions to throw an exception and inform the user.
        newJsonFile.model = jsonObject.optString("model");
        newJsonFile.vendor = jsonObject.optString("vendor");
        newJsonFile.OSName = jsonObject.optString("osname");
        newJsonFile.OSVer = jsonObject.optString("osver");
        newJsonFile.carrier = jsonObject.optString("carrier");
        newJsonFile.netType = jsonObject.optString("nettype");
        newJsonFile.infuServer = jsonObject.optString("infuserver");
        newJsonFile.channel = jsonObject.optString("ch");
        newJsonFile.appVerName = jsonObject.optString("appvername");
        newJsonFile.appVerCode = jsonObject.optString("appvercode");
        newJsonFile.sentTime = jsonObject.optLong("senttime");

        if (jsonObject.has("session_start_time"))
            newJsonFile.sessionStartTime = jsonObject.getLong("session_start_time");
        else
            newJsonFile.sessionStartTime = jsonObject.optLong("time");

        newJsonFile.version = jsonObject.optLong("version");
        newJsonFile.deviceId = jsonObject.optString("deviceid");
        newJsonFile.rumAppKey = jsonObject.optString("rumappkey");

        JSONArray arr = jsonObject.optJSONArray("actions");
        if (arr != null)
        {
            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                UserAction newAction = new UserAction();

                newAction.name = obj.optString("action_name");
                newAction.id = obj.optLong("actionid");
                newAction.duration = obj.optInt("duration");
                newAction.uaSeq = obj.optInt("ua_seq");
                newAction.startTime = obj.optLong("starttime");
                newAction.ctxName = obj.optString("ctx_name");
                //TODO: parse the threads blob correctly
                newAction.info = obj.optString("threadsinfo");

                Path imagePath = dir.resolve(String.valueOf(newAction.id) + ".png");
                Image img;
                try
                {
                    if (Files.exists(imagePath))
                        img = ImageIO.read(new File(imagePath.toString()));
                    else
                    {
                        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/noImage.jpg"));
                        img = icon.getImage();
                    }

                    newAction.image = img.getScaledInstance(180, 320, Image.SCALE_SMOOTH);
                }
                catch (IOException e)
                {
                    System.err.format("IOException: %s%n", e);
                }
                newJsonFile.actions.add(newAction);
            }
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
}

