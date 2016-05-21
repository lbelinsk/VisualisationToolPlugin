import org.jetbrains.annotations.NotNull;
import sun.plugin.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

class UserAction implements Comparable<UserAction>
{
    String name;
    long id;
    //Path imagePath;
    //ImageIcon image;
    Image image;
    int duration;
    int uaSeq;
    long startTime;
    String ctxName;
    Object info;

    @Override
    public int compareTo(@NotNull UserAction otherAction) {
        if (this.uaSeq < otherAction.uaSeq) {
            return -1;
        } else if (this.uaSeq > otherAction.uaSeq) {
            return 1;
        } else return 0;
    }

    @Override
    public String toString()
    {
        String title;
        if (ctxName.isEmpty() && name.isEmpty())
            title = "unknown (ua_seq = " + this.uaSeq + ")";
        else
            title = ctxName + ": " + name;

        return title;
    }
}
