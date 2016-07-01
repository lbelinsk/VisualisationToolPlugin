import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Created by Lior on 30/06/2016.
 */
public  class ADBChooser {
    private static final String ADB_FILE_FOUND = "adb_file_found";
    private static ADBChooser Instance = new ADBChooser();
    static ADBChooser getInstance() { return Instance; }

    String choose(Preferences prefs)
    {
        String adbPath = prefs.get(ADB_FILE_FOUND, "");
        if (adbPath != "")
        {
            return adbPath;
        } else
        {
            File defaultFolder = new File(Paths.get(System.getProperty("user.home")).toString());
            JFileChooser chooser = new JFileChooser(defaultFolder.getAbsolutePath());
            chooser.setDialogTitle("Choose adb.exe file");

            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setApproveButtonText("OK");

            File choice = null;
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                if (chooser.getSelectedFile() != null && chooser.getSelectedFile().exists())
                {
                    choice = chooser.getSelectedFile();
                }
            } else
            {
                return null;
            }

            if (choice == null || !choice.getName().toLowerCase().endsWith("adb.exe"))
            {
                JOptionPane.showMessageDialog(null, "No valid adb.exe file was chosen.", "Error", JOptionPane.INFORMATION_MESSAGE);
                return null;
            } else
            {
                prefs.put(ADB_FILE_FOUND, Paths.get(choice.getAbsolutePath()).toString());
                return Paths.get(choice.getAbsolutePath()).toString();
            }
        }
    }
}
