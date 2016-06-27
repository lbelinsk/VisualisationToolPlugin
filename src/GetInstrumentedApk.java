import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import java.awt.event.WindowAdapter;
import java.awt.event.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.util.List;

public class GetInstrumentedApk extends AnAction {
    private static final String LAST_USED_FOLDER = "last_used_folder2";
    private Preferences prefs = Preferences.userRoot().node(getClass().getName());
    final JDialog dialog = new JDialog();
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    JTextArea msgLabel = new JTextArea("");
    final int MAXIMUM = 100;
    JProgressBar progressBar = new JProgressBar(0, MAXIMUM);
    String APKFilePath;
    File ApkFile;

    HttpClient httpclient = HttpClients.createDefault();
    HttpPost httppost = new HttpPost("http://52.1.231.128:8282/easywrapper/uploadService");
    HttpGet httpGet = new HttpGet("");

    boolean isAdaptedOnClose = false;
    boolean finishedInstAPKCreation = false;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Path path = chooseFile();
        if (path == null) return;
        APKFilePath = path.toString();
        ApkFile = new File(APKFilePath);
        setProgressDialog("Uploading APK file...");

        SwingWorker worker = new SwingWorker<Void, Integer>() {
            @Override
            public Void doInBackground() throws Exception {

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addPart("file", new FileBody(ApkFile));

                httppost = new HttpPost("http://52.1.231.128:8282/easywrapper/uploadService");

                httppost.setEntity(builder.build());
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);

                String instrumentedBodyResponse = responseString.split("'")[1];
                httpGet = new HttpGet(instrumentedBodyResponse);

                publish(-1);
                response = httpclient.execute(httpGet);

                // Copy the response Instrumented APK File
                String InstrumentedAPKPath = APKFilePath + ".inst.apk";
                final InputStream istream = response.getEntity().getContent();
                long InstrumentedFileLength = response.getEntity().getContentLength();

                File outFile = new File(InstrumentedAPKPath);
                if(!outFile.exists()) {
                    outFile.createNewFile();
                }
                final OutputStream ostream = new FileOutputStream(outFile, false);

                long totlalCoppied = 0;
                final byte[] buffer = new byte[1024 * 8];
                while (!isCancelled()) {
                    final int len = istream.read(buffer);
                    if (len <= 0) {
                        break;
                    }
                    totlalCoppied += len;
                    int currentProgressPercentage = (int) ((100 * (double) totlalCoppied) / (double) InstrumentedFileLength);
                    publish(currentProgressPercentage);
                    ostream.write(buffer, 0, len);

                }
                ostream.close();
                istream.close();
                finishedInstAPKCreation = true;
                return null;
            }

            @Override
            public void done() {
                dialog.dispose();
                progressBar.setValue(0);
            }

            @Override
            protected void process(List<Integer> data) {
                if(data.get(0) == -1) {
                    msgLabel.setText("Downloading Instrumented APK file...");
                    progressBar.setIndeterminate(false);
                    progressBar.setStringPainted(true);
                    return;
                }
                    progressBar.setValue(data.get(0));
            }

        };
        worker.execute();
    }

    private void setProgressDialog(String title)
    {
        if(!isAdaptedOnClose) {
            isAdaptedOnClose = true;
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    httppost.abort();
                    httpGet.abort();
                    if(!finishedInstAPKCreation)
                        JOptionPane.showMessageDialog(null, "Aborted the Instrumented Apk file creation", "Error", JOptionPane.INFORMATION_MESSAGE);
                    finishedInstAPKCreation = false;
                }
            });
        }

        progressBar.setStringPainted(false);
        progressBar.setIndeterminate(true);

        msgLabel.setText(title);
        msgLabel.setEditable(false);

        panel.add(msgLabel, BorderLayout.PAGE_START);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));

        dialog.setTitle("Getting Instrumented APK");
        dialog.setAlwaysOnTop(true);
        dialog.getContentPane().add(panel);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setSize(500, dialog.getHeight() + 10);
        dialog.setLocation(dialog.getParent().getLocation());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        msgLabel.setBackground(panel.getBackground());
    }

    @Nullable
    private Path chooseFile() {
        File defaultFolder = new File(Paths.get(System.getProperty("user.home")).toString());
        JFileChooser chooser = new JFileChooser(prefs.get(LAST_USED_FOLDER, defaultFolder.getAbsolutePath()));
        chooser.setDialogTitle("Choose an APK File");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setApproveButtonText("OK");
        File choice = null;
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile() != null) {
                if (chooser.getSelectedFile().exists()) {
                    choice = chooser.getSelectedFile();
                } else {
                    choice = new File(chooser.getSelectedFile().getParent());
                }
            }
        } else {
            return null;
        }

        if (choice == null || !isAPKFile(choice)) {
            JOptionPane.showMessageDialog(null, "None valid APK File was chosen.", "Error", JOptionPane.INFORMATION_MESSAGE);
            return null;
        } else {
            prefs.put(LAST_USED_FOLDER, choice.getParent());
            return Paths.get(choice.getAbsolutePath());
        }
    }

    private boolean isAPKFile(File file) {
        return (file.exists() && file.isFile() && file.toString().toLowerCase().endsWith(".apk"));
    }
}